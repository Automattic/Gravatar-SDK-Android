package com.gravatar.quickeditor.data.datastore

import android.content.Context
import androidx.core.content.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import java.io.File

/**
 * This creates the encrypted file and fallbacks to a new file when previous one was restored from the
 * backup on another device.
 */
@Suppress("TooGenericExceptionCaught", "SwallowedException")
internal fun Context.createEncryptedFileWithFallbackReset(name: String): EncryptedFile {
    val plainFile = preferencesDataStoreFile(name)
    val encryptedFile = try {
        encryptedFile(plainFile)
    } catch (e: Exception) {
        clearMasterKey()
        plainFile.delete()
        encryptedFile(plainFile)
    }
    return encryptedFile
}

private fun Context.encryptedFile(file: File) = EncryptedFile.Builder(
    file,
    this,
    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB,
).build()

private fun Context.clearMasterKey() {
    // the name comes from private EncryptedFile.KEYSET_PREF_NAME
    getSharedPreferences(
        "__androidx_security_crypto_encrypted_file_pref__",
        Context.MODE_PRIVATE,
    ).edit {
        clear()
    }
}
