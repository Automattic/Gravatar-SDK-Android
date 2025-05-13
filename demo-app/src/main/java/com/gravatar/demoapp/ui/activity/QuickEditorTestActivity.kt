package com.gravatar.demoapp.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.gravatar.demoapp.BuildConfig
import com.gravatar.demoapp.R
import com.gravatar.quickeditor.GravatarQuickEditor
import com.gravatar.quickeditor.ui.GetQuickEditorResult
import com.gravatar.quickeditor.ui.GravatarQuickEditorActivity
import com.gravatar.quickeditor.ui.GravatarQuickEditorResult
import com.gravatar.quickeditor.ui.editor.AuthenticationMethod
import com.gravatar.quickeditor.ui.editor.AvatarPickerContentLayout
import com.gravatar.quickeditor.ui.editor.GravatarQuickEditorParams
import com.gravatar.quickeditor.ui.oauth.OAuthParams
import com.gravatar.restapi.models.Profile
import com.gravatar.services.GravatarResult
import com.gravatar.services.ProfileService
import com.gravatar.types.Email
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.ProfileSummary
import com.gravatar.ui.components.atomic.Avatar

class QuickEditorTestActivity : AppCompatActivity() {
    private var profileChanges by mutableStateOf(0)
    private val getQEResult = registerForActivityResult(GetQuickEditorResult()) { quickEditorResult ->
        when (quickEditorResult) {
            GravatarQuickEditorResult.AVATAR_SELECTED -> {
                profileChanges++
                Toast.makeText(this, "Avatar selected", Toast.LENGTH_SHORT).show()
            }

            GravatarQuickEditorResult.DISMISSED -> {
                Toast.makeText(this, "Dismissed", Toast.LENGTH_SHORT).show()
            }

            else -> {
                Toast.makeText(this, "Unexpected...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_editor_test)
        setupViews()
    }

    private fun setupViews() {
        val profileCard = findViewById<ComposeView>(R.id.profile_card)
        val btnUpdateAvatar = findViewById<Button>(R.id.btn_update_avatar)
        val btnUpdateAvatarWithQEActivity = findViewById<Button>(R.id.btn_update_avatar_qe_activity)

        profileCard.setContent {
            key(profileChanges) {
                GravatarProfileSummary(emailAddress = BuildConfig.DEMO_EMAIL)
            }
        }

        btnUpdateAvatar.setOnClickListener {
            GravatarQuickEditor.show(
                activity = this,
                gravatarQuickEditorParams = GravatarQuickEditorParams {
                    email = Email(BuildConfig.DEMO_EMAIL)
                    avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal
                },
                authenticationMethod = AuthenticationMethod.OAuth(
                    OAuthParams {
                        clientId = BuildConfig.DEMO_OAUTH_CLIENT_ID
                        redirectUri = BuildConfig.DEMO_OAUTH_REDIRECT_URI
                    },
                ),
                onAvatarSelected = {
                    profileChanges++
                },
                onDismiss = {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                },
            )
        }

        btnUpdateAvatarWithQEActivity.setOnClickListener {
            getQEResult.launch(
                GravatarQuickEditorActivity.GravatarEditorActivityArguments(
                    GravatarQuickEditorParams {
                        email = Email(BuildConfig.DEMO_EMAIL)
                        avatarPickerContentLayout = AvatarPickerContentLayout.Horizontal
                    },
                    AuthenticationMethod.OAuth(
                        OAuthParams {
                            clientId = BuildConfig.DEMO_OAUTH_CLIENT_ID
                            redirectUri = BuildConfig.DEMO_OAUTH_REDIRECT_URI
                        },
                    ),
                ),
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
            is GravatarResult.Success -> {
                result.value.let {
                    profileState = ComponentState.Loaded(it)
                }
            }

            is GravatarResult.Failure -> {
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
        avatar = {
            Avatar(
                state = profileState,
                size = 72.dp,
                modifier = Modifier.clip(CircleShape),
                forceRefresh = true,
            )
        },
    )
}
