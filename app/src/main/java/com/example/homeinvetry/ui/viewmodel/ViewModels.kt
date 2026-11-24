package com.example.homeinvetry.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.homeinvetry.data.local.HomeStockDatabase
import com.example.homeinvetry.data.local.entity.*
import com.example.homeinvetry.data.repository.InventoryRepository
import com.example.homeinvetry.data.repository.ReminderRepository
import com.example.homeinvetry.data.repository.ShoppingRepository
import com.example.homeinvetry.domain.usecase.AddStockUseCase
import com.example.homeinvetry.domain.usecase.ConsumeStockFefoUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val db = HomeStockDatabase.getDatabase(application)
    private val inventoryRepo = InventoryRepository(db.inventoryDao(), db.stockBatchDao(), db.stockTransactionDao(), db.storageLocationDao())
    private val shoppingRepo = ShoppingRepository(db.shoppingDao())
    private val reminderRepo = ReminderRepository(db.reminderDao())

    val totalItemsCount = inventoryRepo.getAllItems().map { items -> items.sumOf { it.quantity.toInt() } }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val lowStockCount = inventoryRepo.getLowStockItems().map { it.size }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val expiringCount = inventoryRepo.getExpiringItems().map { it.size }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val pendingShoppingCount = shoppingRepo.getAllShoppingItems().map { items -> items.count { !it.isPurchased } }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val storageLocations = inventoryRepo.getAllLocations().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val lowStockItems = inventoryRepo.getLowStockItems().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val expiringItems = inventoryRepo.getExpiringItems().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val upcomingReminders = reminderRepo.getAllReminders().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveVoiceItem(item: InventoryItemEntity) {
        viewModelScope.launch {
            inventoryRepo.insertItem(item)
            inventoryRepo.insertBatch(
                StockBatchEntity(
                    itemId = item.id,
                    batchNumber = "BATCH-01",
                    quantity = item.quantity,
                    unit = item.unit,
                    purchaseDate = "Today",
                    expiryDate = "2026-12-31"
                )
            )
        }
    }
}

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = HomeStockDatabase.getDatabase(application)
    val inventoryRepo = InventoryRepository(db.inventoryDao(), db.stockBatchDao(), db.stockTransactionDao(), db.storageLocationDao())
    val shoppingRepo = ShoppingRepository(db.shoppingDao())

    private val consumeFefoUseCase = ConsumeStockFefoUseCase(inventoryRepo, shoppingRepo)

    val selectedLocation = MutableStateFlow("All")

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val inventoryItems: StateFlow<List<InventoryItemEntity>> = selectedLocation.flatMapLatest { loc ->
        if (loc == "All" || loc.isBlank()) {
            inventoryRepo.getAllItems()
        } else {
            inventoryRepo.getItemsByLocation(loc)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val storageLocations: StateFlow<List<StorageLocationEntity>> = inventoryRepo.getAllLocations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectLocation(loc: String) {
        selectedLocation.value = loc
    }

    fun consumeStockFefo(itemId: String, qty: Double) {
        viewModelScope.launch {
            consumeFefoUseCase(itemId, qty)
        }
    }

    fun deleteItem(item: InventoryItemEntity) {
        viewModelScope.launch {
            inventoryRepo.deleteItem(item)
        }
    }

    fun createLocation(name: String) {
        viewModelScope.launch {
            inventoryRepo.insertLocation(name)
        }
    }

    fun deleteLocation(name: String) {
        viewModelScope.launch {
            inventoryRepo.deleteLocationByName(name)
        }
    }
}

class ShoppingViewModel(application: Application) : AndroidViewModel(application) {
    private val db = HomeStockDatabase.getDatabase(application)
    private val shoppingRepo = ShoppingRepository(db.shoppingDao())
    private val inventoryRepo = InventoryRepository(db.inventoryDao(), db.stockBatchDao(), db.stockTransactionDao(), db.storageLocationDao())
    private val addStockUseCase = AddStockUseCase(inventoryRepo)

    val shoppingItems: StateFlow<List<ShoppingItemEntity>> = shoppingRepo.getAllShoppingItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun togglePurchased(item: ShoppingItemEntity) {
        viewModelScope.launch {
            shoppingRepo.updateShoppingItem(item.copy(isPurchased = !item.isPurchased))
        }
    }

    fun addShoppingItem(name: String, qty: Double = 1.0, unit: String = "kg") {
        viewModelScope.launch {
            shoppingRepo.insertShoppingItem(
                ShoppingItemEntity(
                    name = name,
                    requiredQuantity = qty,
                    unit = unit
                )
            )
        }
    }

    fun deleteShoppingItem(id: String) {
        viewModelScope.launch {
            shoppingRepo.deleteShoppingItemById(id)
        }
    }

    fun markItemPurchasedAndAddStock(item: ShoppingItemEntity, actualQty: Double, price: Double) {
        viewModelScope.launch {
            shoppingRepo.updateShoppingItem(item.copy(isPurchased = true))

            // Check if corresponding inventory item exists or create
            val allItems = inventoryRepo.getItemByIdSync(item.id)
            if (allItems != null) {
                addStockUseCase(item.id, actualQty, price)
            } else {
                // Insert new inventory item with stock
                inventoryRepo.insertItem(
                    InventoryItemEntity(
                        name = item.name,
                        category = item.category,
                        storageLocation = item.storageLocation,
                        quantity = actualQty,
                        unit = item.unit,
                        purchasePrice = price,
                        status = "FRESH"
                    )
                )
            }
        }
    }
}

class RemindersViewModel(application: Application) : AndroidViewModel(application) {
    private val db = HomeStockDatabase.getDatabase(application)
    private val reminderRepo = ReminderRepository(db.reminderDao())

    val reminders: StateFlow<List<ReminderEntity>> = reminderRepo.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleComplete(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderRepo.updateReminder(reminder.copy(isCompleted = !reminder.isCompleted))
        }
    }

    fun addReminder(title: String, description: String, type: String, dueDate: String) {
        viewModelScope.launch {
            reminderRepo.insertReminder(
                ReminderEntity(
                    title = title,
                    description = description,
                    type = type,
                    dueDate = dueDate
                )
            )
        }
    }

    fun deleteReminder(id: String) {
        viewModelScope.launch {
            reminderRepo.deleteReminderById(id)
        }
    }
}

class AddItemViewModel(application: Application) : AndroidViewModel(application) {
    private val db = HomeStockDatabase.getDatabase(application)
    private val inventoryRepo = InventoryRepository(db.inventoryDao(), db.stockBatchDao(), db.stockTransactionDao(), db.storageLocationDao())

    val storageLocations: StateFlow<List<StorageLocationEntity>> = inventoryRepo.getAllLocations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveItem(item: InventoryItemEntity) {
        viewModelScope.launch {
            inventoryRepo.insertItem(item)
            // Insert initial batch
            inventoryRepo.insertBatch(
                StockBatchEntity(
                    itemId = item.id,
                    batchNumber = "BATCH-01",
                    quantity = item.quantity,
                    unit = item.unit,
                    purchaseDate = "Today",
                    expiryDate = item.expiryDate
                )
            )
        }
    }

    fun createLocation(name: String) {
        viewModelScope.launch {
            inventoryRepo.insertLocation(name)
        }
    }
}

class ItemDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val db = HomeStockDatabase.getDatabase(application)
    private val inventoryRepo = InventoryRepository(db.inventoryDao(), db.stockBatchDao(), db.stockTransactionDao(), db.storageLocationDao())
    private val shoppingRepo = ShoppingRepository(db.shoppingDao())

    private val consumeFefoUseCase = ConsumeStockFefoUseCase(inventoryRepo, shoppingRepo)
    private val addStockUseCase = AddStockUseCase(inventoryRepo)

    fun getItem(itemId: String): Flow<InventoryItemEntity?> = inventoryRepo.getItemById(itemId)
    fun getBatches(itemId: String): Flow<List<StockBatchEntity>> = inventoryRepo.getBatchesForItem(itemId)
    fun getTransactions(itemId: String): Flow<List<StockTransactionEntity>> = inventoryRepo.getTransactionsForItem(itemId)

    fun consumeStockFefo(itemId: String, qty: Double) {
        viewModelScope.launch {
            consumeFefoUseCase(itemId, qty)
        }
    }

    fun addStock(itemId: String, qty: Double, price: Double = 0.0, expiry: String = "") {
        viewModelScope.launch {
            addStockUseCase(itemId, qty, price, expiry)
        }
    }

    fun deleteItem(item: InventoryItemEntity) {
        viewModelScope.launch {
            inventoryRepo.deleteItem(item)
        }
    }
}
