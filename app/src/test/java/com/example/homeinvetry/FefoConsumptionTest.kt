package com.example.homeinvetry

import com.example.homeinvetry.data.local.entity.InventoryItemEntity
import com.example.homeinvetry.data.local.entity.StockBatchEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max

class FefoConsumptionTest {

    @Test
    fun testFefoBatchConsumptionOrder() {
        val batches = listOf(
            StockBatchEntity(id = "b1", itemId = "item1", batchNumber = "B1", quantity = 5.0, unit = "kg", purchaseDate = "2025-01-01", expiryDate = "2026-05-01"),
            StockBatchEntity(id = "b2", itemId = "item1", batchNumber = "B2", quantity = 10.0, unit = "kg", purchaseDate = "2025-01-01", expiryDate = "2026-12-01")
        ).sortedBy { it.expiryDate }

        var quantityToConsume = 7.0
        val updatedBatches = mutableListOf<StockBatchEntity>()

        for (batch in batches) {
            if (quantityToConsume <= 0) {
                updatedBatches.add(batch)
                continue
            }

            if (batch.quantity <= quantityToConsume) {
                quantityToConsume -= batch.quantity
                // Batch fully consumed (qty 0)
            } else {
                val remainingBatchQty = batch.quantity - quantityToConsume
                updatedBatches.add(batch.copy(quantity = remainingBatchQty))
                quantityToConsume = 0.0
            }
        }

        // Batch 1 (5 kg) should be completely consumed and removed.
        // Batch 2 (10 kg) should have 8.0 kg remaining.
        assertEquals(1, updatedBatches.size)
        assertEquals("b2", updatedBatches[0].id)
        assertEquals(8.0, updatedBatches[0].quantity, 0.001)
    }

    @Test
    fun testMinimumStockAndBufferCalculation() {
        val currentQty = 2.0
        val minStock = 5.0
        val bufferStock = 8.0

        val isLowStock = currentQty <= minStock
        val suggestedPurchaseQty = max(0.0, bufferStock - currentQty)

        assertTrue(isLowStock)
        assertEquals(6.0, suggestedPurchaseQty, 0.001)
    }

    @Test
    fun testExpiryStatusClassification() {
        val freshItem = InventoryItemEntity(name = "Fresh Milk", quantity = 3.0, minStock = 1.0, status = "FRESH")
        val lowStockItem = InventoryItemEntity(name = "Rice", quantity = 1.0, minStock = 5.0, status = "LOW_STOCK")

        assertEquals("FRESH", freshItem.status)
        assertEquals("LOW_STOCK", lowStockItem.status)
        assertTrue(lowStockItem.quantity <= lowStockItem.minStock)
    }
}
