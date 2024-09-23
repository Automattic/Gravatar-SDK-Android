package com.gravatar.demoapp.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.gravatar.demoapp.BuildConfig
import com.gravatar.demoapp.R
import com.gravatar.quickeditor.GravatarQuickEditor
import com.gravatar.quickeditor.ui.editor.AuthenticationMethod
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.oauth.OAuthParams
import com.gravatar.restapi.models.Profile
import com.gravatar.services.ProfileService
import com.gravatar.services.Result
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.ProfileSummary

class QuickEditorTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_editor_test)
        setupViews()
    }

    private fun setupViews() {
        val profileCard = findViewById<ComposeView>(R.id.profile_card)
        val btnUpdateAvatar = findViewById<Button>(R.id.btn_update_avatar)

        profileCard.setContent {
            GravatarProfileSummary(emailAddress = BuildConfig.DEMO_EMAIL)
        }

        btnUpdateAvatar.setOnClickListener {
            GravatarQuickEditor.show(
                activity = this,
                gravatarQuickEditorParams = GravatarQuickEditorParams {
                    appName = getString(R.string.app_name)
                    email = Email(BuildConfig.DEMO_EMAIL)
                },
                authenticationMethod = AuthenticationMethod.OAuth(
                    OAuthParams {
                        clientId = BuildConfig.DEMO_WORDPRESS_CLIENT_ID
                        clientSecret = BuildConfig.DEMO_WORDPRESS_CLIENT_SECRET
                        redirectUri = BuildConfig.DEMO_WORDPRESS_REDIRECT_URI
                    },
                ),
                onAvatarSelected = {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                },
                onDismiss = {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                },
            )
        }
    }
}

@Composable
fun GravatarProfileSummary(emailAddress: String = "gravatar@automattic.com") {
    val profileService = ProfileService()

    var profileState: ComponentState<Profile> by remember { mutableStateOf(ComponentState.Loading, neverEqualPolicy()) }

    LaunchedEffect(emailAddress) {
        profileState = ComponentState.Loading
        when (val result = profileService.retrieveCatching(Email(emailAddress))) {
            is Result.Success -> {
                result.value.let {
                    profileState = ComponentState.Loaded(it)
                }
            }

            is Result.Failure -> {
                Log.e("Gravatar", result.error.toString())
                profileState = ComponentState.Empty
            }
        }
    }

    // Show the profile as a ProfileCard
    ProfileSummary(
        profileState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    )
}
