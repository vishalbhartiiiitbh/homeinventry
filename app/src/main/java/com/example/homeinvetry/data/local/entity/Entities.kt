package com.example.homeinvetry.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val brand: String = "",
    val category: String = "Other Food",
    val storageLocation: String = "Pantry",
    val subLocation: String = "",
    val quantity: Double = 0.0,
    val minStock: Double = 1.0,
    val bufferStock: Double = 2.0,
    val unit: String = "kg",
    val batchCount: Int = 1,
    val expiryDate: String = "",
    val purchasePrice: Double = 0.0,
    val status: String = "FRESH", // FRESH, LOW_STOCK, EXPIRING_SOON, EXPIRED
    val notes: String = "",
    val barcode: String = "",
    val iconEmoji: String = "📦",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "stock_batches")
data class StockBatchEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val itemId: String,
    val batchNumber: String,
    val quantity: Double,
    val unit: String,
    val purchaseDate: String,
    val expiryDate: String,
    val openedDate: String? = null,
    val useWithinDays: Int? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "stock_transactions")
data class StockTransactionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val itemId: String,
    val batchId: String? = null,
    val transactionType: String, // ADD, CONSUME_FEFO, DISCARD, ADJUST, MOVE
    val quantityChange: Double,
    val unit: String,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "shopping_items")
data class ShoppingItemEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: String = "Other Food",
    val requiredQuantity: Double = 1.0,
    val unit: String = "kg",
    val estimatedPrice: Double = 0.0,
    val isPurchased: Boolean = false,
    val isLowStockAlert: Boolean = false,
    val brand: String = "",
    val storageLocation: String = "Pantry",
    val iconEmoji: String = "🛒",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val type: String = "TASK", // EXPIRY, REFILL, MAINTENANCE, TASK
    val dueDate: String = "",
    val priority: String = "Medium",
    val isCompleted: Boolean = false,
    val actionLabel: String = "View",
    val iconEmoji: String = "🔔",
    val relatedItemId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "storage_locations")
data class StorageLocationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconName: String = "place"
)
