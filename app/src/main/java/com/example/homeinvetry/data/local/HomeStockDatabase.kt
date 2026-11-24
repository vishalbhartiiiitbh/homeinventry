package com.example.homeinvetry.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.homeinvetry.data.local.dao.*
import com.example.homeinvetry.data.local.entity.*
import com.example.homeinvetry.data.sample.SampleDataProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        InventoryItemEntity::class,
        StockBatchEntity::class,
        StockTransactionEntity::class,
        ShoppingItemEntity::class,
        ReminderEntity::class,
        StorageLocationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HomeStockDatabase : RoomDatabase() {

    abstract fun inventoryDao(): InventoryDao
    abstract fun stockBatchDao(): StockBatchDao
    abstract fun stockTransactionDao(): StockTransactionDao
    abstract fun shoppingDao(): ShoppingDao
    abstract fun reminderDao(): ReminderDao
    abstract fun storageLocationDao(): StorageLocationDao

    companion object {
        @Volatile
        private var INSTANCE: HomeStockDatabase? = null

        fun getDatabase(context: Context): HomeStockDatabase {
            return INSTANCE ?: synchronized(this) {
                var instance: HomeStockDatabase? = null
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    HomeStockDatabase::class.java,
                    "homestock_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                instance?.let { database ->
                                    populateInitialData(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(database: HomeStockDatabase) {
            val inventoryDao = database.inventoryDao()
            val batchDao = database.stockBatchDao()
            val shoppingDao = database.shoppingDao()
            val reminderDao = database.reminderDao()
            val locationDao = database.storageLocationDao()

            // Populate initial Storage Locations
            listOf("Pantry", "Kitchen", "Fridge", "Freezer", "Bathroom", "Store Room").forEach { loc ->
                locationDao.insertLocation(StorageLocationEntity(name = loc))
            }

            // Populate Sample Inventory Items
            SampleDataProvider.sampleInventory.forEach { item ->
                inventoryDao.insertItem(
                    InventoryItemEntity(
                        id = item.id,
                        name = item.name,
                        brand = item.brand,
                        category = item.category.displayName,
                        storageLocation = item.storageLocation.displayName,
                        subLocation = item.subLocation,
                        quantity = item.quantity,
                        minStock = item.minStock,
                        bufferStock = item.bufferStock,
                        unit = item.unit,
                        batchCount = item.batchCount,
                        expiryDate = item.expiryDate,
                        purchasePrice = item.purchasePrice,
                        status = item.status.name,
                        notes = item.notes,
                        barcode = item.barcode,
                        iconEmoji = item.iconEmoji
                    )
                )

                // Populate Sample Stock Batches
                batchDao.insertBatch(
                    StockBatchEntity(
                        itemId = item.id,
                        batchNumber = "BATCH-01",
                        quantity = item.quantity,
                        unit = item.unit,
                        purchaseDate = "2025-01-15",
                        expiryDate = item.expiryDate
                    )
                )
            }

            // Populate Sample Shopping Items
            SampleDataProvider.sampleShoppingList.forEach { sItem ->
                shoppingDao.insertShoppingItem(
                    ShoppingItemEntity(
                        id = sItem.id,
                        name = sItem.name,
                        category = sItem.category.displayName,
                        requiredQuantity = sItem.requiredQuantity,
                        unit = sItem.unit,
                        estimatedPrice = sItem.estimatedPrice,
                        isPurchased = sItem.isPurchased,
                        isLowStockAlert = sItem.isLowStockAlert,
                        brand = sItem.brand,
                        iconEmoji = sItem.iconEmoji
                    )
                )
            }

            // Populate Sample Reminders
            SampleDataProvider.sampleReminders.forEach { reminder ->
                reminderDao.insertReminder(
                    ReminderEntity(
                        id = reminder.id,
                        title = reminder.title,
                        description = reminder.description,
                        type = reminder.type.name,
                        dueDate = reminder.dueDate,
                        actionLabel = reminder.actionLabel,
                        iconEmoji = reminder.iconEmoji
                    )
                )
            }
        }
    }
}
