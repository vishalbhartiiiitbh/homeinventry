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

enum class StorageLocationType(val displayName: String, val iconName: String) {
    PANTRY("Pantry", "shelves"),
    KITCHEN("Kitchen", "counter"),
    FRIDGE("Fridge", "ac_unit"),
    FREEZER("Freezer", "kitchen"),
    BATHROOM("Bathroom", "bathtub"),
    STORE_ROOM("Store Room", "inventory_2"),
    CUSTOM("Custom Location", "place")
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
    val brand: String,
    val category: ProductCategory,
    val storageLocation: StorageLocationType,
    val subLocation: String = "",
    val quantity: Double,
    val minStock: Double,
    val bufferStock: Double = minStock * 1.5,
    val unit: String,
    val expiryDate: String,
    val purchasePrice: Double,
    val status: StockStatus,
    val batches: List<StockBatchSample> = emptyList(),
    val notes: String = "",
    val barcode: String = ""
)

data class ShoppingItemSample(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: ProductCategory,
    val requiredQuantity: Double,
    val unit: String,
    val estimatedPrice: Double,
    val isPurchased: Boolean = false,
    val isAutoSuggested: Boolean = false,
    val brand: String = "",
    val storageLocation: StorageLocationType = StorageLocationType.PANTRY
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
    val relatedItemName: String? = null
)

object SampleDataProvider {
    val sampleInventory = listOf(
        InventoryItemSample(
            id = "1",
            name = "Basmati Rice",
            brand = "India Gate",
            category = ProductCategory.RICE,
            storageLocation = StorageLocationType.PANTRY,
            subLocation = "Shelf 1 - Container A",
            quantity = 2.0,
            minStock = 5.0,
            bufferStock = 8.0,
            unit = "kg",
            expiryDate = "2026-12-20",
            purchasePrice = 14.50,
            status = StockStatus.LOW_STOCK,
            notes = "Premium long grain basmati rice",
            batches = listOf(
                StockBatchSample(batchNumber = "BATCH-101", quantity = 2.0, unit = "kg", purchaseDate = "2025-01-15", expiryDate = "2026-12-20")
            )
        ),
        InventoryItemSample(
            id = "2",
            name = "Whole Milk",
            brand = "Amul",
            category = ProductCategory.MILK,
            storageLocation = StorageLocationType.FRIDGE,
            subLocation = "Top Shelf",
            quantity = 1.5,
            minStock = 1.0,
            bufferStock = 2.0,
            unit = "litre",
            expiryDate = "2025-03-02",
            purchasePrice = 2.80,
            status = StockStatus.EXPIRING_SOON,
            notes = "Use for tea and breakfast cereal",
            batches = listOf(
                StockBatchSample(batchNumber = "BATCH-204", quantity = 1.5, unit = "litre", purchaseDate = "2025-02-25", expiryDate = "2025-03-02")
            )
        ),
        InventoryItemSample(
            id = "3",
            name = "Liquid Detergent",
            brand = "Surf Excel",
            category = ProductCategory.DETERGENTS,
            storageLocation = StorageLocationType.BATHROOM,
            subLocation = "Cabinet Under Sink",
            quantity = 2.5,
            minStock = 1.0,
            bufferStock = 2.0,
            unit = "litre",
            expiryDate = "2027-05-10",
            purchasePrice = 12.00,
            status = StockStatus.FRESH,
            notes = "Front load washing machine refill"
        ),
        InventoryItemSample(
            id = "4",
            name = "AA Alkaline Batteries",
            brand = "Duracell",
            category = ProductCategory.BATTERIES,
            storageLocation = StorageLocationType.STORE_ROOM,
            subLocation = "Shelf 2 → Box 4",
            quantity = 8.0,
            minStock = 4.0,
            bufferStock = 6.0,
            unit = "pieces",
            expiryDate = "2029-01-01",
            purchasePrice = 9.99,
            status = StockStatus.FRESH,
            notes = "For remote controls and wall clocks"
        ),
        InventoryItemSample(
            id = "5",
            name = "Extra Virgin Olive Oil",
            brand = "Borges",
            category = ProductCategory.COOKING_OIL,
            storageLocation = StorageLocationType.KITCHEN,
            subLocation = "Spice Rack - Right",
            quantity = 0.4,
            minStock = 1.0,
            bufferStock = 2.0,
            unit = "litre",
            expiryDate = "2026-08-15",
            purchasePrice = 18.50,
            status = StockStatus.LOW_STOCK,
            notes = "Salad dressing & light sautéing"
        ),
        InventoryItemSample(
            id = "6",
            name = "Greek Yogurt",
            brand = "Chobani",
            category = ProductCategory.OTHER_FOOD,
            storageLocation = StorageLocationType.FRIDGE,
            subLocation = "Door Bin",
            quantity = 1.0,
            minStock = 2.0,
            bufferStock = 4.0,
            unit = "packets",
            expiryDate = "2025-02-28",
            purchasePrice = 4.20,
            status = StockStatus.EXPIRED,
            notes = "Plain unsweetened 500g"
        ),
        InventoryItemSample(
            id = "7",
            name = "Dishwashing Pods",
            brand = "Finish",
            category = ProductCategory.CLEANING_SUPPLIES,
            storageLocation = StorageLocationType.KITCHEN,
            subLocation = "Under Sink",
            quantity = 45.0,
            minStock = 15.0,
            bufferStock = 30.0,
            unit = "pieces",
            expiryDate = "2027-01-01",
            purchasePrice = 22.00,
            status = StockStatus.FRESH,
            notes = "Ultimate All-in-1 pack"
        )
    )

    val sampleShoppingList = listOf(
        ShoppingItemSample(
            id = "s1",
            name = "Basmati Rice",
            category = ProductCategory.RICE,
            requiredQuantity = 5.0,
            unit = "kg",
            estimatedPrice = 35.00,
            isPurchased = false,
            isAutoSuggested = true,
            brand = "India Gate",
            storageLocation = StorageLocationType.PANTRY
        ),
        ShoppingItemSample(
            id = "s2",
            name = "Olive Oil 1L",
            category = ProductCategory.COOKING_OIL,
            requiredQuantity = 2.0,
            unit = "litre",
            estimatedPrice = 36.00,
            isPurchased = false,
            isAutoSuggested = true,
            brand = "Borges",
            storageLocation = StorageLocationType.KITCHEN
        ),
        ShoppingItemSample(
            id = "s3",
            name = "Hand Soap Refill",
            category = ProductCategory.SOAP,
            requiredQuantity = 2.0,
            unit = "bottles",
            estimatedPrice = 8.50,
            isPurchased = true,
            isAutoSuggested = false,
            brand = "Dettol",
            storageLocation = StorageLocationType.BATHROOM
        ),
        ShoppingItemSample(
            id = "s4",
            name = "Fresh Eggs (12-pack)",
            category = ProductCategory.OTHER_FOOD,
            requiredQuantity = 1.0,
            unit = "boxes",
            estimatedPrice = 4.50,
            isPurchased = false,
            isAutoSuggested = false,
            storageLocation = StorageLocationType.FRIDGE
        )
    )

    val sampleReminders = listOf(
        ReminderSample(
            id = "r1",
            title = "Whole Milk Expiring Soon",
            description = "Milk in Fridge expires tomorrow (2025-03-02). Use for cooking or baking.",
            type = ReminderType.EXPIRY,
            dueDate = "Tomorrow",
            priority = "High",
            relatedItemName = "Whole Milk"
        ),
        ReminderSample(
            id = "r2",
            title = "Refill Basmati Rice",
            description = "Current stock (2 kg) is below minimum required stock (5 kg).",
            type = ReminderType.REFILL,
            dueDate = "Today",
            priority = "High",
            relatedItemName = "Basmati Rice"
        ),
        ReminderSample(
            id = "r3",
            title = "Clean Water Filter",
            description = "Quarterly maintenance service for RO water purifier filter replacement.",
            type = ReminderType.MAINTENANCE,
            dueDate = "In 3 days",
            priority = "Medium"
        ),
        ReminderSample(
            id = "r4",
            title = "Refill Gas Cylinder",
            description = "Check pressure gauge and book LPG refill tank online.",
            type = ReminderType.TASK,
            dueDate = "Next Monday",
            priority = "High"
        ),
        ReminderSample(
            id = "r5",
            title = "Clean Fridge Shelves",
            description = "Monthly deep cleaning of refrigerator glass drawers and compartments.",
            type = ReminderType.MAINTENANCE,
            dueDate = "This Weekend",
            priority = "Low"
        )
    )
}
