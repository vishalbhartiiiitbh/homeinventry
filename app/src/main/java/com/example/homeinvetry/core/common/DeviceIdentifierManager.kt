package com.example.homeinvetry.core.common

import android.content.Context
import java.io.File
import java.util.UUID

object DeviceIdentifierManager {
    fun getOrCreateDeviceId(context: Context): String {
        val noBackupDir = context.noBackupFilesDir
        val idFile = File(noBackupDir, "device_id.txt")

        if (idFile.exists()) {
            val existingId = idFile.readText().trim()
            if (existingId.isNotBlank()) {
                return existingId
            }
        }

        val newDeviceId = UUID.randomUUID().toString()
        try {
            idFile.writeText(newDeviceId)
        } catch (e: Exception) {
            // Write fallback
        }
        return newDeviceId
    }
}
