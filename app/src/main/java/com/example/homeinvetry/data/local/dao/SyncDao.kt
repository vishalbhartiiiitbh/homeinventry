package com.example.homeinvetry.data.local.dao

import androidx.room.*
import com.example.homeinvetry.data.local.entity.SyncConflictEntity
import com.example.homeinvetry.data.local.entity.SyncOperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncDao {
    @Query("SELECT * FROM sync_operations ORDER BY timestamp ASC")
    fun getAllOperationsFlow(): Flow<List<SyncOperationEntity>>

    @Query("SELECT * FROM sync_operations WHERE isSynced = 0 ORDER BY timestamp ASC")
    fun getUnsyncedOperationsSync(): List<SyncOperationEntity>

    @Query("SELECT * FROM sync_operations WHERE entityId = :entityId ORDER BY version DESC, timestamp DESC")
    fun getOperationsForEntitySync(entityId: String): List<SyncOperationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOperation(operation: SyncOperationEntity)

    @Query("UPDATE sync_operations SET isSynced = 1 WHERE operationId = :operationId")
    fun markOperationSynced(operationId: String)

    @Query("SELECT * FROM sync_conflicts ORDER BY timestamp DESC")
    fun getAllConflictsFlow(): Flow<List<SyncConflictEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConflict(conflict: SyncConflictEntity)

    @Query("UPDATE sync_conflicts SET isResolved = 1 WHERE conflictId = :conflictId")
    fun resolveConflict(conflictId: String)
}
