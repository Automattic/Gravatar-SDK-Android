package com.gravatar.quickeditor.ui.copperlauncher

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import java.io.File

private const val UCROP_COMPRESSION_QUALITY = 75
private const val UCROP_MAX_IMAGE_SIZE = 1080

internal class UCropCropperLauncher : CropperLauncher {
    override fun launch(launcher: ActivityResultLauncher<Intent>, image: Uri, tempFile: File, context: Context) {
        val options = UCrop.Options().apply {
            setToolbarColor(Color.BLACK)
            setStatusBarColor(Color.BLACK)
            setToolbarWidgetColor(Color.WHITE)
            setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.NONE)
            setCompressionQuality(UCROP_COMPRESSION_QUALITY)
            withMaxResultSize(UCROP_MAX_IMAGE_SIZE, UCROP_MAX_IMAGE_SIZE)
            setCircleDimmedLayer(true)
        }
        launcher.launch(
            UCrop.of(image, Uri.fromFile(tempFile)).withAspectRatio(1f, 1f).withOptions(options).getIntent(context),
        )
    }
}
