package com.gravatar.quickeditor.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gravatar.quickeditor.ui.oauth.findComponentActivity

internal fun Context.openAppPermissionSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.setData(uri)
    startActivity(intent)
}

internal fun Context.withPermission(
    permission: String,
    onRequestPermission: (String) -> Unit,
    onShowRationale: () -> Unit = {},
    grantedCallback: () -> Unit,
) {
    val activity = findComponentActivity()
    when {
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
            grantedCallback()
        }

        activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
            onShowRationale()
        }

        else -> {
            onRequestPermission(permission)
        }
    }
}

internal fun Context.hasCameraPermissionInManifest(): Boolean {
    val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
    val permissions = packageInfo.requestedPermissions

    return permissions?.any { perm -> perm == Manifest.permission.CAMERA } ?: false
}
