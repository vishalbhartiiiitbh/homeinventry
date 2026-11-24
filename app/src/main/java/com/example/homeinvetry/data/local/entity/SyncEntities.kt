package com.example.homeinvetry.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "sync_operations")
data class SyncOperationEntity(
    @PrimaryKey val operationId: String = UUID.randomUUID().toString(),
    val deviceId: String,
    val entityType: String, // INVENTORY_ITEM, STOCK_BATCH, SHOPPING_ITEM, REMINDER, LOCATION
    val entityId: String,
    val operationType: String, // INSERT, UPDATE, DELETE_TOMBSTONE, STOCK_ADJUST
    val payloadJson: String,
    val version: Long = 1L,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

@Entity(tableName = "sync_conflicts")
data class SyncConflictEntity(
    @PrimaryKey val conflictId: String = UUID.randomUUID().toString(),
    val entityType: String,
    val entityId: String,
    val winningOperationId: String,
    val losingOperationId: String,
    val losingDeviceId: String,
    val losingPayloadJson: String,
    val conflictReason: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false
)
