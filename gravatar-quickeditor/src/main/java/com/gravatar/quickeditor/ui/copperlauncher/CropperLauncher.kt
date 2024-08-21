package com.gravatar.quickeditor.ui.copperlauncher

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import java.io.File

internal interface CropperLauncher {
    fun launch(launcher: ActivityResultLauncher<Intent>, image: Uri, tempFile: File, context: Context)
}
