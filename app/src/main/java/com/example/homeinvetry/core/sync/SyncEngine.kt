package com.example.homeinvetry.core.sync

import android.content.Context
import com.example.homeinvetry.core.common.DeviceIdentifierManager
import com.example.homeinvetry.data.local.HomeStockDatabase
import com.example.homeinvetry.data.local.entity.InventoryItemEntity
import com.example.homeinvetry.data.local.entity.SyncConflictEntity
import com.example.homeinvetry.data.local.entity.SyncOperationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SyncEngine(private val context: Context) {

    private val db = HomeStockDatabase.getDatabase(context)
    private val deviceId = DeviceIdentifierManager.getOrCreateDeviceId(context)

    suspend fun logLocalOperation(
        entityType: String,
        entityId: String,
        operationType: String,
        payloadJson: String,
        version: Long = 1L
    ): SyncOperationEntity = withContext(Dispatchers.IO) {
        val operation = SyncOperationEntity(
            deviceId = deviceId,
            entityType = entityType,
            entityId = entityId,
            operationType = operationType,
            payloadJson = payloadJson,
            version = version,
            timestamp = System.currentTimeMillis(),
            isSynced = false
        )
        db.syncDao().insertOperation(operation)
        return@withContext operation
    }

    suspend fun recordTombstone(entityType: String, entityId: String, currentVersion: Long): SyncOperationEntity {
        return logLocalOperation(
            entityType = entityType,
            entityId = entityId,
            operationType = "DELETE_TOMBSTONE",
            payloadJson = JSONObject().apply {
                put("id", entityId)
                put("isDeleted", true)
            }.toString(),
            version = currentVersion + 1
        )
    }

    suspend fun applyRemoteOperation(remoteOp: SyncOperationEntity): SyncResult = withContext(Dispatchers.IO) {
        val existingOps = db.syncDao().getOperationsForEntitySync(remoteOp.entityId)

        // Idempotency Check: Ignore if exact operationId already applied
        if (existingOps.any { it.operationId == remoteOp.operationId }) {
            return@withContext SyncResult.AlreadyApplied
        }

        val localLatest = existingOps.firstOrNull()

        if (localLatest == null || remoteOp.version > localLatest.version) {
            // Remote is newer: Apply remote operation
            applyOperationToDatabase(remoteOp)
            db.syncDao().insertOperation(remoteOp.copy(isSynced = true))
            return@withContext SyncResult.Applied
        } else if (remoteOp.version < localLatest.version) {
            // Local is newer: Ignore remote, mark local for sync
            return@withContext SyncResult.IgnoredLesserVersion
        } else {
            // Concurrent Conflict (same version) -> First-Committed-Wins Resolution Rule
            val isRemoteWinner = remoteOp.timestamp <= localLatest.timestamp

            if (isRemoteWinner) {
                // Remote won: Apply remote, preserve local in conflict log
                db.syncDao().insertConflict(
                    SyncConflictEntity(
                        entityType = remoteOp.entityType,
                        entityId = remoteOp.entityId,
                        winningOperationId = remoteOp.operationId,
                        losingOperationId = localLatest.operationId,
                        losingDeviceId = localLatest.deviceId,
                        losingPayloadJson = localLatest.payloadJson,
                        conflictReason = "First-Committed-Wins: Remote timestamp earlier"
                    )
                )
                applyOperationToDatabase(remoteOp)
                db.syncDao().insertOperation(remoteOp.copy(isSynced = true))
                return@withContext SyncResult.ConflictResolvedRemoteWon
            } else {
                // Local won: Keep local active state, preserve remote in conflict log
                db.syncDao().insertConflict(
                    SyncConflictEntity(
                        entityType = remoteOp.entityType,
                        entityId = remoteOp.entityId,
                        winningOperationId = localLatest.operationId,
                        losingOperationId = remoteOp.operationId,
                        losingDeviceId = remoteOp.deviceId,
                        losingPayloadJson = remoteOp.payloadJson,
                        conflictReason = "First-Committed-Wins: Local timestamp earlier"
                    )
                )
                db.syncDao().insertOperation(remoteOp.copy(isSynced = true))
                return@withContext SyncResult.ConflictResolvedLocalWon
            }
        }
    }

    private fun applyOperationToDatabase(op: SyncOperationEntity) {
        if (op.operationType == "DELETE_TOMBSTONE") {
            when (op.entityType) {
                "INVENTORY_ITEM" -> db.inventoryDao().deleteItemById(op.entityId)
                "SHOPPING_ITEM" -> db.shoppingDao().deleteShoppingItemById(op.entityId)
                "REMINDER" -> db.reminderDao().deleteReminderById(op.entityId)
            }
            return
        }

        if (op.entityType == "INVENTORY_ITEM" && op.payloadJson.isNotBlank()) {
            val json = JSONObject(op.payloadJson)
            val item = InventoryItemEntity(
                id = json.getString("id"),
                name = json.getString("name"),
                brand = json.optString("brand", ""),
                category = json.optString("category", "Food"),
                storageLocation = json.optString("storageLocation", "Pantry"),
                quantity = json.optDouble("quantity", 1.0),
                unit = json.optString("unit", "kg")
            )
            db.inventoryDao().insertItem(item)
        }
    }
}

sealed class SyncResult {
    object Applied : SyncResult()
    object AlreadyApplied : SyncResult()
    object IgnoredLesserVersion : SyncResult()
    object ConflictResolvedRemoteWon : SyncResult()
    object ConflictResolvedLocalWon : SyncResult()
}
