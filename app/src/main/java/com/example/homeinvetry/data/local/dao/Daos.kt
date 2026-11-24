package com.example.homeinvetry.data.local.dao

import androidx.room.*
import com.example.homeinvetry.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun getAllItems(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE storageLocation = :location ORDER BY name ASC")
    fun getItemsByLocation(location: String): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE id = :id")
    fun getItemById(id: String): Flow<InventoryItemEntity?>

    @Query("SELECT * FROM inventory_items WHERE id = :id")
    fun getItemByIdSync(id: String): InventoryItemEntity?

    @Query("SELECT * FROM inventory_items WHERE status = 'LOW_STOCK'")
    fun getLowStockItems(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE status = 'EXPIRING_SOON' OR status = 'EXPIRED'")
    fun getExpiringItems(): Flow<List<InventoryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: InventoryItemEntity)

    @Update
    fun updateItem(item: InventoryItemEntity)

    @Delete
    fun deleteItem(item: InventoryItemEntity)

    @Query("DELETE FROM inventory_items WHERE id = :id")
    fun deleteItemById(id: String)
}

@Dao
interface StockBatchDao {
    @Query("SELECT * FROM stock_batches WHERE itemId = :itemId ORDER BY expiryDate ASC")
    fun getBatchesForItem(itemId: String): Flow<List<StockBatchEntity>>

    @Query("SELECT * FROM stock_batches WHERE itemId = :itemId ORDER BY expiryDate ASC")
    fun getBatchesForItemSync(itemId: String): List<StockBatchEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBatch(batch: StockBatchEntity)

    @Update
    fun updateBatch(batch: StockBatchEntity)

    @Delete
    fun deleteBatch(batch: StockBatchEntity)

    @Query("DELETE FROM stock_batches WHERE itemId = :itemId")
    fun deleteBatchesForItem(itemId: String)
}

@Dao
interface StockTransactionDao {
    @Query("SELECT * FROM stock_transactions WHERE itemId = :itemId ORDER BY timestamp DESC")
    fun getTransactionsForItem(itemId: String): Flow<List<StockTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(transaction: StockTransactionEntity)
}

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping_items ORDER BY isPurchased ASC, createdAt DESC")
    fun getAllShoppingItems(): Flow<List<ShoppingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShoppingItem(item: ShoppingItemEntity)

    @Update
    fun updateShoppingItem(item: ShoppingItemEntity)

    @Delete
    fun deleteShoppingItem(item: ShoppingItemEntity)

    @Query("DELETE FROM shopping_items WHERE id = :id")
    fun deleteShoppingItemById(id: String)
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReminder(reminder: ReminderEntity)

    @Update
    fun updateReminder(reminder: ReminderEntity)

    @Delete
    fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :id")
    fun deleteReminderById(id: String)
}

@Dao
interface StorageLocationDao {
    @Query("SELECT * FROM storage_locations ORDER BY name ASC")
    fun getAllLocations(): Flow<List<StorageLocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: StorageLocationEntity)

    @Query("DELETE FROM storage_locations WHERE name = :name")
    fun deleteLocationByName(name: String)
}
