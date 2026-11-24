package com.example.homeinvetry.data.sample

import java.util.UUID

enum class ProductCategory(val displayName: String, val isFood: Boolean) {
    RICE("Rice", true),
    WHEAT_FLOUR("Wheat Flour", true),
    SUGAR("Sugar", true),
    SALT("Salt", true),
    PULSES("Pulses", true),
    COOKING_OIL("Cooking Oil", true),
    MILK("Milk", true),
    VEGETABLES("Vegetables", true),
    FRUITS("Fruits", true),
    SNACKS("Snacks", true),
    SPICES("Spices", true),
    OTHER_FOOD("Other Food", true),

    DETERGENTS("Detergents", false),
    SOAP("Soap", false),
    SHAMPOO("Shampoo", false),
    TOOTHPASTE("Toothpaste", false),
    CLEANING_SUPPLIES("Cleaning Supplies", false),
    BATTERIES("Batteries", false),
    BULBS("Bulbs", false),
    STATIONERY("Stationery", false),
    KITCHEN_SUPPLIES("Kitchen Supplies", false),
    TOOLS("Tools", false),
    ELECTRONICS("Electronics", false),
    OTHER_HOUSEHOLD("Other Household", false)
}

enum class StorageLocationType(val displayName: String, val itemCount: Int) {
    PANTRY("Pantry", 35),
    KITCHEN("Kitchen", 42),
    FRIDGE("Fridge", 14),
    FREEZER("Freezer", 8),
    BATHROOM("Bathroom", 12),
    STORE_ROOM("Store Room", 17),
    CUSTOM("Custom Location", 5)
}

enum class StockStatus {
    FRESH,
    EXPIRING_SOON,
    EXPIRED,
    LOW_STOCK
}

data class StockBatchSample(
    val id: String = UUID.randomUUID().toString(),
    val batchNumber: String,
    val quantity: Double,
    val unit: String,
    val purchaseDate: String,
    val expiryDate: String,
    val openedDate: String? = null,
    val useWithinDays: Int? = null
)

data class InventoryItemSample(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val brand: String = "",
    val category: ProductCategory,
    val storageLocation: StorageLocationType,
    val subLocation: String = "",
    val quantity: Double,
    val minStock: Double = 1.0,
    val bufferStock: Double = minStock * 1.5,
    val unit: String,
    val batchCount: Int = 1,
    val expiryDate: String,
    val purchasePrice: Double = 0.0,
    val status: StockStatus = StockStatus.FRESH,
    val batches: List<StockBatchSample> = emptyList(),
    val notes: String = "",
    val barcode: String = "",
    val iconEmoji: String = "📦"
)

data class ShoppingItemSample(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: ProductCategory = ProductCategory.OTHER_FOOD,
    val requiredQuantity: Double = 1.0,
    val unit: String = "kg",
    val estimatedPrice: Double = 0.0,
    val isPurchased: Boolean = false,
    val isAutoSuggested: Boolean = false,
    val isLowStockAlert: Boolean = false,
    val brand: String = "",
    val storageLocation: StorageLocationType = StorageLocationType.PANTRY,
    val iconEmoji: String = "🛒"
)

enum class ReminderType {
    EXPIRY,
    REFILL,
    MAINTENANCE,
    TASK
}

data class ReminderSample(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val type: ReminderType,
    val dueDate: String,
    val priority: String = "Medium",
    val isCompleted: Boolean = false,
    val relatedItemName: String? = null,
    val actionLabel: String = "View",
    val iconEmoji: String = "🔔"
)

object SampleDataProvider {
    val sampleInventory = listOf(
        InventoryItemSample(
            id = "1",
            name = "Rice (Basmati)",
            brand = "India Gate",
            category = ProductCategory.RICE,
            storageLocation = StorageLocationType.PANTRY,
            subLocation = "Container A",
            quantity = 15.0,
            minStock = 5.0,
            unit = "kg",
            batchCount = 2,
            expiryDate = "20 Dec 2026",
            status = StockStatus.FRESH,
            iconEmoji = "🍚"
        ),
        InventoryItemSample(
            id = "2",
            name = "Wheat Flour",
            brand = "Aashirvaad",
            category = ProductCategory.WHEAT_FLOUR,
            storageLocation = StorageLocationType.PANTRY,
            subLocation = "Shelf 1",
            quantity = 5.0,
            minStock = 2.0,
            unit = "kg",
            batchCount = 1,
            expiryDate = "15 Jan 2027",
            status = StockStatus.FRESH,
            iconEmoji = "🌾"
        ),
        InventoryItemSample(
            id = "3",
            name = "Sugar",
            brand = "Madhur",
            category = ProductCategory.SUGAR,
            storageLocation = StorageLocationType.PANTRY,
            subLocation = "Shelf 2",
            quantity = 2.0,
            minStock = 1.0,
            unit = "kg",
            batchCount = 1,
            expiryDate = "10 Mar 2027",
            status = StockStatus.FRESH,
            iconEmoji = "🍬"
        ),
        InventoryItemSample(
            id = "4",
            name = "Milk Powder",
            brand = "Nestle",
            category = ProductCategory.MILK,
            storageLocation = StorageLocationType.PANTRY,
            subLocation = "Cabinet 3",
            quantity = 500.0,
            minStock = 200.0,
            unit = "g",
            batchCount = 1,
            expiryDate = "30 Nov 2026",
            status = StockStatus.FRESH,
            iconEmoji = "🥛"
        ),
        InventoryItemSample(
            id = "5",
            name = "Cooking Oil",
            brand = "Fortune",
            category = ProductCategory.COOKING_OIL,
            storageLocation = StorageLocationType.KITCHEN,
            quantity = 1.0,
            minStock = 2.0,
            unit = "L",
            expiryDate = "12 Oct 2026",
            status = StockStatus.LOW_STOCK,
            iconEmoji = "🛢️"
        ),
        InventoryItemSample(
            id = "6",
            name = "Detergent",
            brand = "Surf Excel",
            category = ProductCategory.DETERGENTS,
            storageLocation = StorageLocationType.BATHROOM,
            quantity = 1.0,
            minStock = 2.0,
            unit = "Pack",
            expiryDate = "01 Jan 2028",
            status = StockStatus.LOW_STOCK,
            iconEmoji = "🧴"
        )
    )

    val sampleShoppingList = listOf(
        ShoppingItemSample(
            id = "s1",
            name = "Cooking Oil",
            category = ProductCategory.COOKING_OIL,
            requiredQuantity = 1.0,
            unit = "L",
            isPurchased = false,
            isLowStockAlert = true,
            iconEmoji = "🛢️"
        ),
        ShoppingItemSample(
            id = "s2",
            name = "Tomatoes",
            category = ProductCategory.VEGETABLES,
            requiredQuantity = 1.0,
            unit = "kg",
            isPurchased = false,
            iconEmoji = "🍅"
        ),
        ShoppingItemSample(
            id = "s3",
            name = "Onions",
            category = ProductCategory.VEGETABLES,
            requiredQuantity = 2.0,
            unit = "kg",
            isPurchased = false,
            iconEmoji = "🧅"
        ),
        ShoppingItemSample(
            id = "s4",
            name = "Detergent",
            category = ProductCategory.DETERGENTS,
            requiredQuantity = 1.0,
            unit = "Pack",
            isPurchased = false,
            iconEmoji = "🧴"
        ),
        ShoppingItemSample(
            id = "s5",
            name = "Milk",
            category = ProductCategory.MILK,
            requiredQuantity = 1.0,
            unit = "L",
            isPurchased = true,
            iconEmoji = "🥛"
        ),
        ShoppingItemSample(
            id = "s6",
            name = "Bread",
            category = ProductCategory.OTHER_FOOD,
            requiredQuantity = 1.0,
            unit = "Pack",
            isPurchased = true,
            iconEmoji = "🍞"
        )
    )

    val sampleReminders = listOf(
        ReminderSample(
            id = "r1",
            title = "Milk is expiring soon!",
            description = "Expires in 2 days • 1 L",
            type = ReminderType.EXPIRY,
            dueDate = "In 2 days",
            actionLabel = "View",
            iconEmoji = "🥛"
        ),
        ReminderSample(
            id = "r2",
            title = "Detergent is low",
            description = "Only 1 pack left",
            type = ReminderType.REFILL,
            dueDate = "Today",
            actionLabel = "Refill",
            iconEmoji = "🧴"
        ),
        ReminderSample(
            id = "r3",
            title = "Clean Water Filter",
            description = "Due in 7 days",
            type = ReminderType.MAINTENANCE,
            dueDate = "In 7 days",
            actionLabel = "View",
            iconEmoji = "🚰"
        ),
        ReminderSample(
            id = "r4",
            title = "Pay Electricity Bill",
            description = "Due on 30 Nov 2026",
            type = ReminderType.TASK,
            dueDate = "30 Nov 2026",
            actionLabel = "View",
            iconEmoji = "🔌"
        )
    )
}
