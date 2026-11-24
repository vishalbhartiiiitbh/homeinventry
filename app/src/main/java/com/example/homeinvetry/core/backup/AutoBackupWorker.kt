package com.example.homeinvetry.core.backup

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.io.File

class AutoBackupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val backupManager = BackupManager(applicationContext)

            val backupDir = File(applicationContext.filesDir, "autobackups").apply {
                if (!exists()) mkdirs()
            }
            val targetFile = File(backupDir, "homestock_autobackup_${System.currentTimeMillis()}.homestock")

            val uri = FileProvider.getUriForFile(
                applicationContext,
                "${applicationContext.packageName}.fileprovider",
                targetFile
            )

            // Auto-backup using default internal passcode key
            backupManager.createEncryptedBackup(uri, "HomeStockAutoKey2026")

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
