package com.gravatar.quickeditor.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import java.io.File

internal class FileUtils(
    private val context: Context,
) {
    fun createTempFile(): File {
        return File(context.cacheDir, "gravatar_${System.currentTimeMillis()}")
    }

    fun deleteFile(uri: Uri) {
        val toFile = uri.toFile()
        toFile.delete()
    }
}
