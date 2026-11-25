package com.example.homeinvetry.core.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.homeinvetry.data.local.HomeStockDatabase
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        checkAndTriggerExpiryAndLowStockNotifications(applicationContext)
        return Result.success()
    }

    companion object {
        suspend fun checkAndTriggerExpiryAndLowStockNotifications(context: Context) {
            val database = HomeStockDatabase.getDatabase(context)
            val inventoryDao = database.inventoryDao()
            val reminderDao = database.reminderDao()

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayMillis = System.currentTimeMillis()
            val todayDateStr = sdf.format(Date(todayMillis))
            val todayDate = try { sdf.parse(todayDateStr) } catch (e: Exception) { Date(todayMillis) }

            val allItems = inventoryDao.getAllItems().first()

            // 1. Precise 3-Stage Expiry Tracking (5 days before, 2 days before, On Expiry Date)
            for (item in allItems) {
                if (item.expiryDate.isNotBlank()) {
                    try {
                        val expiryDateObj = sdf.parse(item.expiryDate.trim())
                        if (expiryDateObj != null && todayDate != null) {
                            val diffMillis = expiryDateObj.time - todayDate.time
                            val daysUntilExpiry = TimeUnit.MILLISECONDS.toDays(diffMillis)

                            when (daysUntilExpiry) {
                                5L -> {
                                    NotificationHelper.showNotification(
                                        context,
                                        item.id.hashCode() + 500,
                                        "Expiry Alert (5 Days) ${item.iconEmoji}",
                                        "${item.name} is expiring in 5 days on ${item.expiryDate}."
                                    )
                                }
                                2L -> {
                                    NotificationHelper.showNotification(
                                        context,
                                        item.id.hashCode() + 200,
                                        "Urgent Expiry Warning (2 Days) ${item.iconEmoji}",
                                        "${item.name} is expiring in 2 days on ${item.expiryDate}! Plan to consume soon."
                                    )
                                }
                                0L -> {
                                    NotificationHelper.showNotification(
                                        context,
                                        item.id.hashCode() + 100,
                                        "Expired Product Alert 🚨 ${item.iconEmoji}",
                                        "${item.name} expires TODAY (${item.expiryDate})! Use or discard immediately."
                                    )
                                    inventoryDao.updateItem(item.copy(status = "EXPIRED"))
                                }
                                else -> {
                                    if (daysUntilExpiry < 0L && item.status != "EXPIRED") {
                                        NotificationHelper.showNotification(
                                            context,
                                            item.id.hashCode() + 100,
                                            "Product Expired 🚨 ${item.iconEmoji}",
                                            "${item.name} expired on ${item.expiryDate}."
                                        )
                                        inventoryDao.updateItem(item.copy(status = "EXPIRED"))
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Date parse fallback
                    }
                }
            }

            // 2. Low Stock Alerts
            val lowStockItems = inventoryDao.getLowStockItems().first()
            if (lowStockItems.isNotEmpty()) {
                NotificationHelper.showNotification(
                    context,
                    101,
                    "Low Stock Alert (${lowStockItems.size} items)",
                    "${lowStockItems.first().name} is running low on stock (${lowStockItems.first().quantity.toInt()} ${lowStockItems.first().unit} left)."
                )
            }

            // 3. Pending Reminders & Household Tasks
            val reminders = reminderDao.getAllReminders().first().filter { !it.isCompleted }
            if (reminders.isNotEmpty()) {
                NotificationHelper.showNotification(
                    context,
                    103,
                    "Upcoming Household Task",
                    "${reminders.first().title} is due today!"
                )
            }
        }
    }
}
