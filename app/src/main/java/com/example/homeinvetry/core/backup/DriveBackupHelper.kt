package com.example.homeinvetry.core.backup

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

object DriveBackupHelper {
    fun uploadToGoogleDrive(context: Context, backupFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            backupFile
        )

        val driveIntent = Intent(Intent.ACTION_SEND).apply {
            setPackage("com.google.android.apps.docs")
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_SUBJECT, "HomeStock Encrypted Backup - ${backupFile.name}")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            val chooser = Intent.createChooser(driveIntent, "Upload Backup directly to Google Drive ☁️")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            val genericIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(genericIntent, "Upload Backup to Google Drive")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        }
    }
}
