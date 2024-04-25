package com.gravatar.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toFile
import com.gravatar.services.AvatarService
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarListener
import com.gravatar.types.Email
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import java.io.File

/**
 * UI component that wraps the received [@Composable], allowing the user to pick an image from the
 * gallery and upload it to Gravatar.
 *
 * @param content The content to be wrapped by the [GravatarImagePickerWrapper].
 * @param email The email associated with the Gravatar account.
 * @param accessToken The access token to authenticate the Gravatar account.
 * @param listener The listener to be informed about the avatar upload status.
 * @param modifier Composable modifier that allows customize the [GravatarImagePickerWrapper].
 * @param imageEditionOptions The options to customize the image edition UI.
 */
@Composable
public fun GravatarImagePickerWrapper(
    content: @Composable () -> Unit,
    email: String,
    accessToken: String,
    listener: GravatarImagePickerWrapperListener,
    modifier: Modifier = Modifier,
    imageEditionOptions: ImageEditionStyling = ImageEditionStyling(),
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null, neverEqualPolicy()) }
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        selectedImageUri = it
    }
    val uCropLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        it.data?.let { intentData ->
            UCrop.getOutput(intentData)?.let { croppedImageUri ->
                listener.onAvatarUploadStarted()
                AvatarService().upload(croppedImageUri.toFile(), Email(email), accessToken, listener)
            }
        }
    }

    Surface(
        modifier = modifier.clickable {
            imagePickerLauncher.launch(IMAGE_MIME_TYPE)
        },
    ) {
        content()
    }

    selectedImageUri?.let { image ->
        selectedImageUri = null
        launchAvatarCrop(image, uCropLauncher, LocalContext.current, imageEditionOptions)
    }
}

private fun launchAvatarCrop(
    image: Uri,
    uCropLauncher: ActivityResultLauncher<Intent>,
    context: Context,
    imageEditionOptions: ImageEditionStyling,
) {
    val options = UCrop.Options().apply {
        with(imageEditionOptions) {
            toolbarColor?.let { setToolbarColor(it) }
            statusBarColor?.let { setStatusBarColor(it) }
            toolbarWidgetColor?.let { setToolbarWidgetColor(it) }
        }
        setShowCropGrid(false)
        setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.NONE)
        setCompressionQuality(UCROP_COMPRESSION_QUALITY)
        setCircleDimmedLayer(true)
    }
    uCropLauncher.launch(
        UCrop.of(image, Uri.fromFile(File(context.cacheDir, "cropped_for_gravatar.jpg")))
            .withAspectRatio(1f, 1f)
            .withOptions(options)
            .getIntent(context),
    )
}

/**
 * Listener for [GravatarImagePickerWrapper] that provides a way be informed about the avatar upload status.
 */
public interface GravatarImagePickerWrapperListener : GravatarListener<Unit, ErrorType> {
    /**
     * Called when the avatar upload is started.
     */
    public fun onAvatarUploadStarted()
}

/**
 * Options to customize the image edition screen.
 *
 * @property statusBarColor The color of the status bar.
 * @property toolbarColor The color of the toolbar.
 * @property toolbarWidgetColor The color of the toolbar widgets.
 */
public data class ImageEditionStyling(
    val statusBarColor: Int? = null,
    val toolbarColor: Int? = null,
    val toolbarWidgetColor: Int? = null,
)

private const val IMAGE_MIME_TYPE = "image/*"
private const val UCROP_COMPRESSION_QUALITY = 100
