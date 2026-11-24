package com.example.homeinvetry

import com.example.homeinvetry.core.backup.BackupManager
import org.junit.Assert.assertEquals
import org.junit.Test
import javax.crypto.AEADBadTagException

class BackupRestoreTest {

    @Test
    fun testAesGcmEncryptionDecryption() {
        val originalText = "Hello HomeStock Room Backup Payload 123"
        val passphrase = "MySecretPassphrase123"

        val encryptedBytes = BackupManager.encryptData(originalText.toByteArray(Charsets.UTF_8), passphrase)
        val decryptedBytes = BackupManager.decryptData(encryptedBytes, passphrase)

        val restoredText = String(decryptedBytes, Charsets.UTF_8)
        assertEquals(originalText, restoredText)
    }

    @Test(expected = Exception::class)
    fun testAesGcmDecryptionFailsWithWrongPassphrase() {
        val originalText = "Sensitive Household Data"
        val passphrase = "CorrectPassword123"
        val wrongPassphrase = "WrongPassword123"

        val encryptedBytes = BackupManager.encryptData(originalText.toByteArray(Charsets.UTF_8), passphrase)
        // Decrypting with wrong passphrase must throw AEADBadTagException
        BackupManager.decryptData(encryptedBytes, wrongPassphrase)
    }
}
