@file:OptIn(ExperimentalPermissionsApi::class)

package com.gravatar.events.scanner

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun Permission(
    permission: String = android.Manifest.permission.CAMERA,
    rationale: String = "We need Camera permission because of reasons.",
    permissionNotAvailableContent: @Composable () -> Unit = { },
    content: @Composable () -> Unit = { }
) {
    val permissionState = rememberPermissionStateSafe(permission)

    if (permissionState.status.isGranted) {
        content()
    } else {
        permissionNotAvailableContent()
        Rationale(rationale) {
            permissionState.launchPermissionRequest()
        }
    }
}

@Composable
private fun Rationale(
    text: String,
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Don't */ },
        title = {
            Text(text = "Permission request")
        },
        text = {
            Text(text)
        },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text("Next")
            }
        }
    )
}

@ExperimentalPermissionsApi
@Composable
fun rememberPermissionStateSafe(permission: String, onPermissionResult: (Boolean) -> Unit = {}) = when {
    LocalInspectionMode.current -> remember {
        object : PermissionState {
            override val permission = permission
            override val status = PermissionStatus.Granted
            override fun launchPermissionRequest() = Unit
        }
    }
    else -> rememberPermissionState(permission, onPermissionResult)
}