package com.gravatar.quickeditor.data

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import java.io.File

internal class FileUtils(
    private val context: Context,
) {
    fun createCroppedAvatarFile(): File {
        return File(context.cacheDir, "cropped_avatar_${System.currentTimeMillis()}.jpg")
    }

    fun deleteFile(uri: Uri) {
        val toFile = uri.toFile()
        toFile.delete()
    }
}
