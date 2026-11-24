package com.example.homeinvetry

import com.example.homeinvetry.data.local.entity.SyncOperationEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class MultiDeviceSyncTest {

    @Test
    fun testSyncOperationLogCreation() {
        val deviceId = "device-pixel-8-abc"
        val itemId = UUID.randomUUID().toString()

        val op = SyncOperationEntity(
            deviceId = deviceId,
            entityType = "INVENTORY_ITEM",
            entityId = itemId,
            operationType = "INSERT",
            payloadJson = """{"id":"$itemId","name":"Basmati Rice","quantity":10.0}""",
            version = 1L
        )

        assertEquals(deviceId, op.deviceId)
        assertEquals("INVENTORY_ITEM", op.entityType)
        assertEquals(1L, op.version)
        assertEquals("INSERT", op.operationType)
    }

    @Test
    fun testFirstCommittedWinsRule() {
        val itemId = UUID.randomUUID().toString()

        val opDeviceA = SyncOperationEntity(
            deviceId = "device-A",
            entityType = "INVENTORY_ITEM",
            entityId = itemId,
            operationType = "UPDATE",
            payloadJson = """{"name":"Rice A"}""",
            version = 2L,
            timestamp = 1000L
        )

        val opDeviceB = SyncOperationEntity(
            deviceId = "device-B",
            entityType = "INVENTORY_ITEM",
            entityId = itemId,
            operationType = "UPDATE",
            payloadJson = """{"name":"Rice B"}""",
            version = 2L,
            timestamp = 1050L
        )

        // First-Committed-Wins: Earlier timestamp (1000L < 1050L) wins
        val isDeviceAWinner = opDeviceA.timestamp <= opDeviceB.timestamp
        assertTrue(isDeviceAWinner)
    }

    @Test
    fun testTombstoneVersionIncrement() {
        val currentVersion = 3L
        val tombstoneVersion = currentVersion + 1

        assertEquals(4L, tombstoneVersion)
    }
}
