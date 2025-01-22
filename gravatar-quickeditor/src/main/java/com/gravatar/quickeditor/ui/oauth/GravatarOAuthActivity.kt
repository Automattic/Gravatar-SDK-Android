package com.gravatar.quickeditor.ui.oauth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.gravatar.quickeditor.ui.GravatarQuickEditorActivity
import com.gravatar.types.Email
import java.net.URLDecoder

/**
 * Activity to handle OAuth authentication with Gravatar.
 *
 * This activity launches a custom tab for the user to authenticate with Gravatar.
 * Once the authentication is complete, it retrieves the access token from the redirect URI
 * and returns it as a result.
 *
 * It's used internally, but it's exposed as a public as it needs to be declared in your AndroidManifest.xml file
 * with the proper deep linking setup.
 */
public class GravatarOAuthActivity : AppCompatActivity() {
    private var oAuthStarted = false
    private var clientId: String? = null
    private var redirectUri: String? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }

        oAuthStarted = savedInstanceState?.getBoolean(OAUTH_STARTED_KEY) ?: false

        clientId = intent.getStringExtra(CLIENT_ID_KEY)
        redirectUri = intent.getStringExtra(REDIRECT_URI_KEY)
        email = intent.getStringExtra(EMAIL_KEY)

        if (clientId == null || redirectUri == null || email == null) {
            setResult(
                RESULT_OK,
                Intent().apply {
                    putExtra(ACTIVITY_RESULT, RESULT_CANCELED)
                },
            )
            finish()
            return
        }

        addOnNewIntentListener { newIntent ->
            val token = newIntent.data
                ?.encodedFragment
                ?.split("&")
                ?.associate {
                    val split = it.split("=")
                    split.first() to split.last()
                }
                ?.get("access_token")
                ?.let { URLDecoder.decode(it, "UTF-8") }

            if (token != null) {
                val resultIntent = Intent().apply {
                    putExtra(ACTIVITY_RESULT, RESULT_TOKEN_RETRIEVED)
                    putExtra(TOKEN_KEY, token)
                }

                setResult(RESULT_OK, resultIntent)
            } else {
                setResult(
                    RESULT_OK,
                    Intent().apply {
                        putExtra(ACTIVITY_RESULT, RESULT_TOKEN_ERROR)
                    },
                )
            }
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(OAUTH_STARTED_KEY, oAuthStarted)
    }

    override fun onResume() {
        super.onResume()

        if (!oAuthStarted) {
            launchCustomTab(
                this,
                clientId = clientId!!,
                redirectUri = redirectUri!!,
                email = Email(email!!),
            )
            oAuthStarted = true
        } else {
            setResult(
                RESULT_OK,
                Intent().apply {
                    putExtra(ACTIVITY_RESULT, RESULT_CANCELED)
                },
            )
            finish()
        }
    }

    private fun launchCustomTab(context: Context, clientId: String, redirectUri: String, email: Email) {
        val customTabIntent: CustomTabsIntent = CustomTabsIntent.Builder()
            .build()
        customTabIntent.launchUrl(
            context,
            Uri.parse(WordPressOauth.buildUrl(clientId, redirectUri, email)),
        )
    }

    internal companion object {
        private const val CLIENT_ID_KEY = "client_id"
        private const val REDIRECT_URI_KEY = "redirect_uri"
        private const val EMAIL_KEY = "email"
        private const val OAUTH_STARTED_KEY = "oauth_started"
        internal const val TOKEN_KEY = "auth_token"

        internal const val ACTIVITY_RESULT: String = "oAuthActivityResult"
        internal const val RESULT_CANCELED: Int = 1000
        internal const val RESULT_TOKEN_RETRIEVED: Int = 1001
        internal const val RESULT_TOKEN_ERROR: Int = 1002

        internal fun createIntent(context: Context, oAuthParams: OAuthParams, email: String): Intent {
            return Intent(context, GravatarOAuthActivity::class.java).apply {
                putExtra(CLIENT_ID_KEY, oAuthParams.clientId)
                putExtra(REDIRECT_URI_KEY, oAuthParams.redirectUri)
                putExtra(EMAIL_KEY, email)
            }
        }
    }
}

/**
 * Activity result contract to get the result from the [GravatarOAuthActivity].
 *
 * @see GravatarOAuthActivityParams
 * @see GravatarOAuthResult
 */
internal class GravatarOAuthResultContract :
    ActivityResultContract<GravatarOAuthActivityParams, GravatarOAuthResult>() {
    override fun createIntent(context: Context, input: GravatarOAuthActivityParams): Intent {
        return GravatarOAuthActivity.createIntent(
            context = context,
            oAuthParams = input.oAuthParams,
            email = input.email,
        )
    }

    override fun parseResult(resultCode: Int, intent: Intent?): GravatarOAuthResult {
        return when (intent?.getIntExtra(GravatarOAuthActivity.ACTIVITY_RESULT, -1)) {
            GravatarOAuthActivity.RESULT_TOKEN_RETRIEVED -> GravatarOAuthResult.TOKEN(
                intent.getStringExtra(GravatarOAuthActivity.TOKEN_KEY)!!,
            )

            GravatarOAuthActivity.RESULT_TOKEN_ERROR -> GravatarOAuthResult.ERROR
            else -> GravatarOAuthResult.DISMISSED
        }
    }
}

/**
 * Parameters for the [GravatarOAuthActivity].
 */
internal class GravatarOAuthActivityParams(
    val oAuthParams: OAuthParams,
    val email: String,
)

/**
 * Result enum for the [GravatarQuickEditorActivity].
 */
internal sealed class GravatarOAuthResult {
    data class TOKEN(val token: String) : GravatarOAuthResult()

    data object DISMISSED : GravatarOAuthResult()

    data object ERROR : GravatarOAuthResult()
}
