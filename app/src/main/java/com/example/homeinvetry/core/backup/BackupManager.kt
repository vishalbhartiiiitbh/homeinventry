package com.example.homeinvetry.core.backup

import android.content.Context
import android.net.Uri
import com.example.homeinvetry.data.local.HomeStockDatabase
import com.example.homeinvetry.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.security.SecureRandom
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

data class BackupMetadata(
    val appVersion: String = "1.0.0",
    val schemaVersion: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val itemCount: Int,
    val locationCount: Int,
    val reminderCount: Int
)

class BackupManager(private val context: Context) {

    private val db = HomeStockDatabase.getDatabase(context)

    companion object {
        const val DEFAULT_PASSPHRASE = "HomeStockMasterKey2026"

        private const val PBKDF2_ITERATIONS = 10000
        private const val KEY_LENGTH_BITS = 256
        private const val SALT_SIZE_BYTES = 16
        private const val IV_SIZE_BYTES = 12
        private const val GCM_TAG_LENGTH_BITS = 128

        fun deriveKey(passphrase: String, salt: ByteArray): SecretKey {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(passphrase.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS)
            val secretKey = factory.generateSecret(spec)
            return SecretKeySpec(secretKey.encoded, "AES")
        }

        fun encryptData(plainBytes: ByteArray, passphrase: String = DEFAULT_PASSPHRASE): ByteArray {
            val random = SecureRandom()
            val salt = ByteArray(SALT_SIZE_BYTES).also { random.nextBytes(it) }
            val iv = ByteArray(IV_SIZE_BYTES).also { random.nextBytes(it) }

            val key = deriveKey(passphrase, salt)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))

            val cipherBytes = cipher.doFinal(plainBytes)

            val outputStream = ByteArrayOutputStream()
            outputStream.write(salt)
            outputStream.write(iv)
            outputStream.write(cipherBytes)
            return outputStream.toByteArray()
        }

        fun decryptData(encryptedBytes: ByteArray, passphrase: String = DEFAULT_PASSPHRASE): ByteArray {
            if (encryptedBytes.size < SALT_SIZE_BYTES + IV_SIZE_BYTES + 16) {
                throw IllegalArgumentException("Invalid encrypted backup payload size.")
            }

            val salt = encryptedBytes.copyOfRange(0, SALT_SIZE_BYTES)
            val iv = encryptedBytes.copyOfRange(SALT_SIZE_BYTES, SALT_SIZE_BYTES + IV_SIZE_BYTES)
            val cipherText = encryptedBytes.copyOfRange(SALT_SIZE_BYTES + IV_SIZE_BYTES, encryptedBytes.size)

            val key = deriveKey(passphrase, salt)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv))

            return cipher.doFinal(cipherText)
        }
    }

    suspend fun createEncryptedBackup(destUri: Uri, passphrase: String = DEFAULT_PASSPHRASE): BackupMetadata = withContext(Dispatchers.IO) {
        val items = db.inventoryDao().getAllItems().first()
        val batches = ArrayList<StockBatchEntity>()
        items.forEach { item ->
            batches.addAll(db.stockBatchDao().getBatchesForItemSync(item.id))
        }
        val shopping = db.shoppingDao().getAllShoppingItems().first()
        val reminders = db.reminderDao().getAllReminders().first()
        val locations = db.storageLocationDao().getAllLocations().first()

        val metadata = BackupMetadata(
            itemCount = items.size,
            locationCount = locations.size,
            reminderCount = reminders.size
        )

        // Step 1: Create JSON Export
        val jsonRoot = JSONObject().apply {
            put("metadata", JSONObject().apply {
                put("appVersion", metadata.appVersion)
                put("schemaVersion", metadata.schemaVersion)
                put("createdAt", metadata.createdAt)
            })

            put("items", JSONArray().also { arr ->
                items.forEach { item ->
                    arr.put(JSONObject().apply {
                        put("id", item.id)
                        put("name", item.name)
                        put("brand", item.brand)
                        put("category", item.category)
                        put("storageLocation", item.storageLocation)
                        put("subLocation", item.subLocation)
                        put("quantity", item.quantity)
                        put("minStock", item.minStock)
                        put("bufferStock", item.bufferStock)
                        put("unit", item.unit)
                        put("expiryDate", item.expiryDate)
                        put("purchasePrice", item.purchasePrice)
                        put("status", item.status)
                        put("notes", item.notes)
                        put("barcode", item.barcode)
                        put("iconEmoji", item.iconEmoji)
                        put("batchCount", item.batchCount)
                    })
                }
            })

            put("batches", JSONArray().also { arr ->
                batches.forEach { batch ->
                    arr.put(JSONObject().apply {
                        put("id", batch.id)
                        put("itemId", batch.itemId)
                        put("batchNumber", batch.batchNumber)
                        put("quantity", batch.quantity)
                        put("unit", batch.unit)
                        put("purchaseDate", batch.purchaseDate)
                        put("expiryDate", batch.expiryDate)
                    })
                }
            })

            put("shopping", JSONArray().also { arr ->
                shopping.forEach { s ->
                    arr.put(JSONObject().apply {
                        put("id", s.id)
                        put("name", s.name)
                        put("category", s.category)
                        put("requiredQuantity", s.requiredQuantity)
                        put("unit", s.unit)
                        put("estimatedPrice", s.estimatedPrice)
                        put("isPurchased", s.isPurchased)
                        put("isLowStockAlert", s.isLowStockAlert)
                        put("brand", s.brand)
                        put("storageLocation", s.storageLocation)
                        put("iconEmoji", s.iconEmoji)
                        put("createdAt", s.createdAt)
                    })
                }
            })

            put("reminders", JSONArray().also { arr ->
                reminders.forEach { r ->
                    arr.put(JSONObject().apply {
                        put("id", r.id)
                        put("title", r.title)
                        put("description", r.description)
                        put("type", r.type)
                        put("dueDate", r.dueDate)
                        put("priority", r.priority)
                        put("isCompleted", r.isCompleted)
                        put("actionLabel", r.actionLabel)
                        put("iconEmoji", r.iconEmoji)
                        put("createdAt", r.createdAt)
                    })
                }
            })

            put("locations", JSONArray().also { arr ->
                locations.forEach { l ->
                    arr.put(JSONObject().apply {
                        put("id", l.id)
                        put("name", l.name)
                        put("iconName", l.iconName)
                    })
                }
            })
        }

        // Step 2: Zip the JSON
        val baos = ByteArrayOutputStream()
        ZipOutputStream(baos).use { zos ->
            zos.putNextEntry(ZipEntry("homestock_backup.json"))
            zos.write(jsonRoot.toString(2).toByteArray(Charsets.UTF_8))
            zos.closeEntry()
        }

        val zipBytes = baos.toByteArray()

        // Step 3: Encrypt with AES-256-GCM
        val encrypted = encryptData(zipBytes, passphrase)

        // Step 4: Write to SAF URI
        context.contentResolver.openOutputStream(destUri)?.use { fos ->
            fos.write(encrypted)
        } ?: throw IOException("Could not open destination SAF URI for writing.")

        return@withContext metadata
    }

    suspend fun restoreEncryptedBackup(sourceUri: Uri, passphrase: String = DEFAULT_PASSPHRASE): BackupMetadata = withContext(Dispatchers.IO) {
        val encryptedBytes = context.contentResolver.openInputStream(sourceUri)?.use { fis ->
            fis.readBytes()
        } ?: throw IOException("Could not open backup file for reading.")

        val zipBytes = decryptData(encryptedBytes, passphrase)

        // Unzip payload
        var jsonContent: String? = null
        ZipInputStream(ByteArrayInputStream(zipBytes)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                if (entry.name == "homestock_backup.json") {
                    jsonContent = zis.bufferedReader().readText()
                    break
                }
                entry = zis.nextEntry
            }
        }

        if (jsonContent == null) {
            throw IllegalArgumentException("Invalid backup archive: missing homestock_backup.json")
        }

        val jsonRoot = JSONObject(jsonContent)
        val metaObj = jsonRoot.getJSONObject("metadata")

        val metadata = BackupMetadata(
            appVersion = metaObj.optString("appVersion", "1.0.0"),
            schemaVersion = metaObj.optInt("schemaVersion", 1),
            createdAt = metaObj.optLong("createdAt", System.currentTimeMillis()),
            itemCount = jsonRoot.getJSONArray("items").length(),
            locationCount = jsonRoot.getJSONArray("locations").length(),
            reminderCount = jsonRoot.getJSONArray("reminders").length()
        )

        // Restore into Room Database in transaction
        db.runInTransaction {
            db.clearAllTables()

            // Restore Locations
            val locsArr = jsonRoot.getJSONArray("locations")
            for (i in 0 until locsArr.length()) {
                val obj = locsArr.getJSONObject(i)
                db.storageLocationDao().insertLocation(
                    StorageLocationEntity(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        iconName = obj.optString("iconName", "place")
                    )
                )
            }

            // Restore Items
            val itemsArr = jsonRoot.getJSONArray("items")
            for (i in 0 until itemsArr.length()) {
                val obj = itemsArr.getJSONObject(i)
                db.inventoryDao().insertItem(
                    InventoryItemEntity(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        brand = obj.optString("brand", ""),
                        category = obj.optString("category", "Food"),
                        storageLocation = obj.optString("storageLocation", "Pantry"),
                        subLocation = obj.optString("subLocation", ""),
                        quantity = obj.optDouble("quantity", 1.0),
                        minStock = obj.optDouble("minStock", 1.0),
                        bufferStock = obj.optDouble("bufferStock", 2.0),
                        unit = obj.optString("unit", "kg"),
                        expiryDate = obj.optString("expiryDate", "2026-12-31"),
                        purchasePrice = obj.optDouble("purchasePrice", 0.0),
                        status = obj.optString("status", "FRESH"),
                        notes = obj.optString("notes", ""),
                        barcode = obj.optString("barcode", ""),
                        iconEmoji = obj.optString("iconEmoji", "📦"),
                        batchCount = obj.optInt("batchCount", 1)
                    )
                )
            }

            // Restore Batches
            val batchesArr = jsonRoot.getJSONArray("batches")
            for (i in 0 until batchesArr.length()) {
                val obj = batchesArr.getJSONObject(i)
                db.stockBatchDao().insertBatch(
                    StockBatchEntity(
                        id = obj.getString("id"),
                        itemId = obj.getString("itemId"),
                        batchNumber = obj.optString("batchNumber", "BATCH-01"),
                        quantity = obj.optDouble("quantity", 1.0),
                        unit = obj.optString("unit", "kg"),
                        purchaseDate = obj.optString("purchaseDate", "Today"),
                        expiryDate = obj.optString("expiryDate", "2026-12-31")
                    )
                )
            }

            // Restore Shopping
            val shoppingArr = jsonRoot.getJSONArray("shopping")
            for (i in 0 until shoppingArr.length()) {
                val obj = shoppingArr.getJSONObject(i)
                db.shoppingDao().insertShoppingItem(
                    ShoppingItemEntity(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        category = obj.optString("category", "Other Food"),
                        requiredQuantity = obj.optDouble("requiredQuantity", 1.0),
                        unit = obj.optString("unit", "kg"),
                        estimatedPrice = obj.optDouble("estimatedPrice", 0.0),
                        isPurchased = obj.optBoolean("isPurchased", false),
                        isLowStockAlert = obj.optBoolean("isLowStockAlert", false),
                        brand = obj.optString("brand", ""),
                        storageLocation = obj.optString("storageLocation", "Pantry"),
                        iconEmoji = obj.optString("iconEmoji", "🛒"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }

            // Restore Reminders
            val remindersArr = jsonRoot.getJSONArray("reminders")
            for (i in 0 until remindersArr.length()) {
                val obj = remindersArr.getJSONObject(i)
                db.reminderDao().insertReminder(
                    ReminderEntity(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        description = obj.optString("description", ""),
                        type = obj.optString("type", "TASK"),
                        dueDate = obj.optString("dueDate", ""),
                        priority = obj.optString("priority", "Medium"),
                        isCompleted = obj.optBoolean("isCompleted", false),
                        actionLabel = obj.optString("actionLabel", "View"),
                        iconEmoji = obj.optString("iconEmoji", "🔔"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
        }

        return@withContext metadata
    }
}
