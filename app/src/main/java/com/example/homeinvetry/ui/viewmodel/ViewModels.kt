package com.example.homeinvetry.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.homeinvetry.data.local.HomeStockDatabase
import com.example.homeinvetry.data.local.entity.*
import com.example.homeinvetry.data.repository.*
import com.example.homeinvetry.domain.usecase.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = HomeStockDatabase.getDatabase(application)
    val inventoryRepo = InventoryRepository(db.inventoryDao(), db.stockBatchDao(), db.stockTransactionDao(), db.storageLocationDao())

    val selectedLocation = MutableStateFlow("All")

    val inventoryItems: StateFlow<List<InventoryItemEntity>> = selectedLocation.flatMapLatest { loc ->
        if (loc == "All" || loc.isBlank()) {
            inventoryRepo.getAllItems()
        } else {
            inventoryRepo.getItemsByLocation(loc)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val storageLocations: StateFlow<List<StorageLocationEntity>> = inventoryRepo.getAllLocations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectLocation(location: String) {
        selectedLocation.value = location
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

            val targetItemId = item.id.removePrefix("auto_")
            val existingItem = inventoryRepo.getItemByIdSync(targetItemId)

            if (existingItem != null) {
                addStockUseCase(targetItemId, actualQty, price)
            } else {
                val allItems = inventoryRepo.getAllItems().first()
                val matchByName = allItems.find { it.name.equals(item.name, ignoreCase = true) }

                if (matchByName != null) {
                    addStockUseCase(matchByName.id, actualQty, price)
                } else {
                    val newItem = InventoryItemEntity(
                        name = item.name,
                        category = item.category,
                        storageLocation = item.storageLocation,
                        quantity = actualQty,
                        unit = item.unit,
                        purchasePrice = price,
                        status = "FRESH"
                    )
                    inventoryRepo.insertItem(newItem)
                    inventoryRepo.insertBatch(
                        StockBatchEntity(
                            itemId = newItem.id,
                            batchNumber = "BATCH-01",
                            quantity = actualQty,
                            unit = item.unit,
                            purchaseDate = "Today",
                            expiryDate = "2026-12-31"
                        )
                    )
                }
            }
        }
    }
}

class RemindersViewModel(application: Application) : AndroidViewModel(application) {
    private val db = HomeStockDatabase.getDatabase(application)
    private val reminderRepo = ReminderRepository(db.reminderDao())

    val reminders: StateFlow<List<ReminderEntity>> = reminderRepo.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleReminderCompleted(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderRepo.updateReminder(reminder.copy(isCompleted = !reminder.isCompleted))
        }
    }

    fun toggleComplete(reminder: ReminderEntity) = toggleReminderCompleted(reminder)

    fun addReminder(title: String, description: String = "", type: String = "TASK", dueDate: String = "Tomorrow") {
        viewModelScope.launch {
            reminderRepo.insertReminder(
                ReminderEntity(
                    title = title,
                    description = description.ifEmpty { "Due $dueDate" },
                    type = type,
                    dueDate = dueDate,
                    iconEmoji = when (type.uppercase()) {
                        "EXPIRY" -> "🥛"
                        "REFILL" -> "🧴"
                        "MAINTENANCE" -> "🚰"
                        else -> "🔔"
                    }
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
