package com.gravatar.demoapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.android.gms.instantapps.InstantApps
import com.gravatar.Gravatar
import com.gravatar.demoapp.ui.DemoGravatarApp
import com.gravatar.quickeditor.QuickEditorContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize the Gravatar SDK with the API key if it is available
        @Suppress("UNNECESSARY_SAFE_CALL")
        BuildConfig.DEMO_GRAVATAR_API_KEY?.let { Gravatar.apiKey(it).context(applicationContext) }

        QuickEditorContainer.init(applicationContext)

        Log.d("GravatarInstant", "Instant app: ${packageManager.isInstantApp}")

        val client = InstantApps.getInstantAppsClient(this)
        client.areInstantAppsEnabledForDevice()
            .addOnSuccessListener {
                Log.d("GravatarInstant", "Instant app enabled: true")
            }
            .addOnFailureListener {
                Log.d("GravatarInstant", "Instant app enabled: false")
            }

        setContent {
            DemoGravatarApp()
        }
    }
}
