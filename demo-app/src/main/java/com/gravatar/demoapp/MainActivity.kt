package com.gravatar.demoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gravatar.Gravatar
import com.gravatar.demoapp.ui.DemoGravatarApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize the Gravatar SDK with the API key if it is available
        BuildConfig.DEMO_GRAVATAR_API_KEY?.let { Gravatar.apiKey(it).context(applicationContext) }

        setContent {
            DemoGravatarApp()
        }
    }
}
