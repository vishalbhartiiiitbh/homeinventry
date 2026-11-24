package com.example.homeinvetry.domain.usecase

import com.example.homeinvetry.data.local.entity.*
import com.example.homeinvetry.data.repository.InventoryRepository
import com.example.homeinvetry.data.repository.ReminderRepository
import com.example.homeinvetry.data.repository.ShoppingRepository
import kotlin.math.max

class ConsumeStockFefoUseCase(
    private val inventoryRepository: InventoryRepository,
    private val shoppingRepository: ShoppingRepository,
    private val reminderRepository: ReminderRepository
) {
    suspend operator fun invoke(itemId: String, quantityToConsume: Double) {
        if (quantityToConsume <= 0) return

        val item = inventoryRepository.getItemByIdSync(itemId) ?: return
        val batches = inventoryRepository.getBatchesForItemSync(itemId)

        var remainingToConsume = quantityToConsume

        // Consume stock from earliest expiring batch first (FEFO)
        for (batch in batches) {
            if (remainingToConsume <= 0) break

            if (batch.quantity <= remainingToConsume) {
                remainingToConsume -= batch.quantity
                inventoryRepository.deleteBatch(batch)
            } else {
                val updatedBatch = batch.copy(quantity = batch.quantity - remainingToConsume)
                inventoryRepository.updateBatch(updatedBatch)
                remainingToConsume = 0.0
            }
        }

        val newQuantity = max(0.0, item.quantity - quantityToConsume)
        val newStatus = if (newQuantity <= item.minStock) "LOW_STOCK" else item.status

        // Update item in Room
        val updatedItem = item.copy(
            quantity = newQuantity,
            status = newStatus,
            updatedAt = System.currentTimeMillis()
        )
        inventoryRepository.updateItem(updatedItem)

        // Record stock transaction log
        inventoryRepository.insertTransaction(
            StockTransactionEntity(
                itemId = itemId,
                transactionType = "CONSUME_FEFO",
                quantityChange = -quantityToConsume,
                unit = item.unit,
                notes = "Consumed $quantityToConsume ${item.unit} via FEFO order."
            )
        )

        // Auto suggest on shopping list & create Refill Reminder if low/out of stock
        if (newQuantity <= item.minStock) {
            val suggestedQty = max(1.0, item.bufferStock - newQuantity)
            shoppingRepository.insertShoppingItem(
                ShoppingItemEntity(
                    id = "auto_${item.id}",
                    name = item.name,
                    category = item.category,
                    requiredQuantity = suggestedQty,
                    unit = item.unit,
                    isPurchased = false,
                    isLowStockAlert = true,
                    brand = item.brand,
                    storageLocation = item.storageLocation,
                    iconEmoji = item.iconEmoji
                )
            )

            // Insert single Refill Reminder (overwrites existing with same id if already present)
            reminderRepository.insertReminder(
                ReminderEntity(
                    id = "refill_${item.id}",
                    title = "${item.name} Refill Needed",
                    description = if (newQuantity == 0.0) "Out of stock! Refill needed." else "Low stock (${newQuantity.toInt()} ${item.unit} left). Refill needed.",
                    type = "REFILL",
                    dueDate = "Today",
                    actionLabel = "Mark as Seen",
                    iconEmoji = "🧴",
                    relatedItemId = item.id
                )
            )
        }
    }
}

class AddStockUseCase(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(
        itemId: String,
        addedQuantity: Double,
        purchasePrice: Double = 0.0,
        expiryDate: String = ""
    ) {
        if (addedQuantity <= 0) return

        val item = inventoryRepository.getItemByIdSync(itemId) ?: return
        val newQuantity = item.quantity + addedQuantity
        val newStatus = if (newQuantity > item.minStock) "FRESH" else item.status
        val finalExpiry = if (expiryDate.isNotBlank()) expiryDate.trim() else item.expiryDate

        // Insert new stock batch
        inventoryRepository.insertBatch(
            StockBatchEntity(
                itemId = itemId,
                batchNumber = "BATCH-${System.currentTimeMillis() % 10000}",
                quantity = addedQuantity,
                unit = item.unit,
                purchaseDate = "Today",
                expiryDate = finalExpiry
            )
        )

        // Update inventory item with updated quantity and updated expiry date
        inventoryRepository.updateItem(
            item.copy(
                quantity = newQuantity,
                batchCount = item.batchCount + 1,
                status = newStatus,
                expiryDate = finalExpiry,
                purchasePrice = if (purchasePrice > 0.0) purchasePrice else item.purchasePrice,
                updatedAt = System.currentTimeMillis()
            )
        )

        // Insert transaction record
        inventoryRepository.insertTransaction(
            StockTransactionEntity(
                itemId = itemId,
                transactionType = "ADD",
                quantityChange = addedQuantity,
                unit = item.unit,
                notes = "Added $addedQuantity ${item.unit} to stock (Expiry: $finalExpiry)."
            )
        )
    }
}
