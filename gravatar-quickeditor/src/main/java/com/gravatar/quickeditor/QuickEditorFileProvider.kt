package com.gravatar.quickeditor

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

internal class QuickEditorFileProvider : FileProvider(R.xml.quickeditor_filepaths) {
    companion object {
        fun getTempCameraImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "quickeditor")
            directory.mkdirs()
            val file = File(directory, "temp_camera_image.jpg")
            val authority = "${context.packageName}.com.quickeditor.fileprovider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}
