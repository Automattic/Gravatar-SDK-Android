package com.gravatar.quickeditor.data

import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import com.gravatar.BuildConfig
import com.gravatar.services.GravatarResult
import java.net.URI

internal class ImageDownloader(
    private val context: Context,
) {
    private val downloadManager: DownloadManager? = context.getSystemService(DownloadManager::class.java)
    private val appName: String by lazy {
        val applicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA,
        )
        context.packageManager.getApplicationLabel(applicationInfo).toString()
    }

    fun downloadImage(imageUrl: URI): GravatarResult<Unit, DownloadManagerError> {
        return when {
            downloadManager == null -> GravatarResult.Failure(DownloadManagerError.DOWNLOAD_MANAGER_NOT_AVAILABLE)
            !isDownloadManagerEnabled() -> GravatarResult.Failure(DownloadManagerError.DOWNLOAD_MANAGER_DISABLED)
            else -> {
                val request = DownloadManager.Request(Uri.parse(imageUrl.withMaxSizeQueryParam().toString()))
                    .addRequestHeader("X-Platform", "Android")
                    .addRequestHeader("X-SDK-Version", BuildConfig.SDK_VERSION)
                    .addRequestHeader("X-Source", appName)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setMimeType("image/*")
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "gravatar_image_${System.currentTimeMillis()}.png",
                    )
                downloadManager.enqueue(request)
                GravatarResult.Success(Unit)
            }
        }
    }

    private fun isDownloadManagerEnabled(): Boolean {
        val downloadManagerPackageName = "com.android.providers.downloads"
        val state: Int = context.packageManager.getApplicationEnabledSetting(downloadManagerPackageName)

        return !(
            state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
        )
    }

    private fun URI.withMaxSizeQueryParam(): URI {
        return URI(
            scheme,
            userInfo,
            host,
            port,
            path,
            "size=max",
            fragment,
        )
    }
}

internal enum class DownloadManagerError {
    DOWNLOAD_MANAGER_NOT_AVAILABLE,
    DOWNLOAD_MANAGER_DISABLED,
}
