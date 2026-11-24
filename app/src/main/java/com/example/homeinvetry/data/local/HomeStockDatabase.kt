package com.example.homeinvetry.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.homeinvetry.data.local.dao.*
import com.example.homeinvetry.data.local.entity.*
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
        StorageLocationEntity::class,
        SyncOperationEntity::class,
        SyncConflictEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class HomeStockDatabase : RoomDatabase() {

    abstract fun inventoryDao(): InventoryDao
    abstract fun stockBatchDao(): StockBatchDao
    abstract fun stockTransactionDao(): StockTransactionDao
    abstract fun shoppingDao(): ShoppingDao
    abstract fun reminderDao(): ReminderDao
    abstract fun storageLocationDao(): StorageLocationDao
    abstract fun syncDao(): SyncDao

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

        private fun populateInitialData(database: HomeStockDatabase) {
            val locationDao = database.storageLocationDao()

            // Populate default Storage Locations ONLY (No sample inventory items)
            listOf("Pantry", "Kitchen", "Fridge", "Freezer", "Bathroom", "Store Room").forEach { loc ->
                locationDao.insertLocation(StorageLocationEntity(name = loc))
            }
        }
    }
}
