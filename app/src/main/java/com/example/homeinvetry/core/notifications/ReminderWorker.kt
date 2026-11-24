package com.example.homeinvetry.core.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.homeinvetry.data.local.HomeStockDatabase
import kotlinx.coroutines.flow.first

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val database = HomeStockDatabase.getDatabase(applicationContext)
        val inventoryDao = database.inventoryDao()
        val reminderDao = database.reminderDao()

        // Check low stock items
        val lowStockItems = inventoryDao.getLowStockItems().first()
        if (lowStockItems.isNotEmpty()) {
            NotificationHelper.showNotification(
                applicationContext,
                101,
                "Low Stock Alert (${lowStockItems.size} items)",
                "${lowStockItems.first().name} and others are running low. Tap to view your shopping list."
            )
        }

        // Check expiring items
        val expiringItems = inventoryDao.getExpiringItems().first()
        if (expiringItems.isNotEmpty()) {
            NotificationHelper.showNotification(
                applicationContext,
                102,
                "Expiry Reminder (${expiringItems.size} items)",
                "${expiringItems.first().name} is expiring soon! Tap to inspect inventory."
            )
        }

        // Check pending reminders
        val reminders = reminderDao.getAllReminders().first().filter { !it.isCompleted }
        if (reminders.isNotEmpty()) {
            NotificationHelper.showNotification(
                applicationContext,
                103,
                "Upcoming Household Task",
                "${reminders.first().title} is due today!"
            )
        }

        return Result.success()
    }
}
