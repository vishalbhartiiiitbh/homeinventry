package com.example.homeinvetry.data.repository

import com.example.homeinvetry.data.local.dao.*
import com.example.homeinvetry.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class InventoryRepository(
    private val inventoryDao: InventoryDao,
    private val stockBatchDao: StockBatchDao,
    private val stockTransactionDao: StockTransactionDao,
    private val storageLocationDao: StorageLocationDao
) {
    fun getAllItems(): Flow<List<InventoryItemEntity>> = inventoryDao.getAllItems()

    fun getItemsByLocation(location: String): Flow<List<InventoryItemEntity>> = inventoryDao.getItemsByLocation(location)

    fun getItemById(id: String): Flow<InventoryItemEntity?> = inventoryDao.getItemById(id)

    suspend fun getItemByIdSync(id: String): InventoryItemEntity? = withContext(Dispatchers.IO) {
        inventoryDao.getItemByIdSync(id)
    }

    fun getLowStockItems(): Flow<List<InventoryItemEntity>> = inventoryDao.getLowStockItems()

    fun getExpiringItems(): Flow<List<InventoryItemEntity>> = inventoryDao.getExpiringItems()

    suspend fun insertItem(item: InventoryItemEntity) = withContext(Dispatchers.IO) {
        inventoryDao.insertItem(item)
    }

    suspend fun updateItem(item: InventoryItemEntity) = withContext(Dispatchers.IO) {
        inventoryDao.updateItem(item)
    }

    suspend fun deleteItem(item: InventoryItemEntity) = withContext(Dispatchers.IO) {
        inventoryDao.deleteItem(item)
        stockBatchDao.deleteBatchesForItem(item.id)
    }

    suspend fun deleteItemById(id: String) = withContext(Dispatchers.IO) {
        inventoryDao.deleteItemById(id)
        stockBatchDao.deleteBatchesForItem(id)
    }

    // Batches & Transactions
    fun getBatchesForItem(itemId: String): Flow<List<StockBatchEntity>> = stockBatchDao.getBatchesForItem(itemId)

    suspend fun getBatchesForItemSync(itemId: String): List<StockBatchEntity> = withContext(Dispatchers.IO) {
        stockBatchDao.getBatchesForItemSync(itemId)
    }

    suspend fun insertBatch(batch: StockBatchEntity) = withContext(Dispatchers.IO) {
        stockBatchDao.insertBatch(batch)
    }

    suspend fun updateBatch(batch: StockBatchEntity) = withContext(Dispatchers.IO) {
        stockBatchDao.updateBatch(batch)
    }

    suspend fun deleteBatch(batch: StockBatchEntity) = withContext(Dispatchers.IO) {
        stockBatchDao.deleteBatch(batch)
    }

    fun getTransactionsForItem(itemId: String): Flow<List<StockTransactionEntity>> = stockTransactionDao.getTransactionsForItem(itemId)

    suspend fun insertTransaction(transaction: StockTransactionEntity) = withContext(Dispatchers.IO) {
        stockTransactionDao.insertTransaction(transaction)
    }

    // Storage Locations
    fun getAllLocations(): Flow<List<StorageLocationEntity>> = storageLocationDao.getAllLocations()

    suspend fun insertLocation(name: String) = withContext(Dispatchers.IO) {
        storageLocationDao.insertLocation(StorageLocationEntity(name = name))
    }

    suspend fun deleteLocationByName(name: String) = withContext(Dispatchers.IO) {
        storageLocationDao.deleteLocationByName(name)
        val itemsInLocation = inventoryDao.getItemsByLocationSync(name)
        itemsInLocation.forEach { item ->
            inventoryDao.updateItem(item.copy(storageLocation = "Unassigned"))
        }
    }
}

class ShoppingRepository(
    private val shoppingDao: ShoppingDao
) {
    fun getAllShoppingItems(): Flow<List<ShoppingItemEntity>> = shoppingDao.getAllShoppingItems()

    suspend fun insertShoppingItem(item: ShoppingItemEntity) = withContext(Dispatchers.IO) {
        shoppingDao.insertShoppingItem(item)
    }

    suspend fun updateShoppingItem(item: ShoppingItemEntity) = withContext(Dispatchers.IO) {
        shoppingDao.updateShoppingItem(item)
    }

    suspend fun deleteShoppingItem(item: ShoppingItemEntity) = withContext(Dispatchers.IO) {
        shoppingDao.deleteShoppingItem(item)
    }

    suspend fun deleteShoppingItemById(id: String) = withContext(Dispatchers.IO) {
        shoppingDao.deleteShoppingItemById(id)
    }
}

class ReminderRepository(
    private val reminderDao: ReminderDao
) {
    fun getAllReminders(): Flow<List<ReminderEntity>> = reminderDao.getAllReminders()

    suspend fun insertReminder(reminder: ReminderEntity) = withContext(Dispatchers.IO) {
        reminderDao.insertReminder(reminder)
    }

    suspend fun updateReminder(reminder: ReminderEntity) = withContext(Dispatchers.IO) {
        reminderDao.updateReminder(reminder)
    }

    suspend fun deleteReminder(reminder: ReminderEntity) = withContext(Dispatchers.IO) {
        reminderDao.deleteReminder(reminder)
    }

    suspend fun deleteReminderById(id: String) = withContext(Dispatchers.IO) {
        reminderDao.deleteReminderById(id)
    }
}
