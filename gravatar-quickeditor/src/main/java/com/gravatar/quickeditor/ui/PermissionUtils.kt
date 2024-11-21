package com.gravatar.quickeditor.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

internal fun Context.openAppPermissionSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.setData(uri)
    startActivity(intent)
}
