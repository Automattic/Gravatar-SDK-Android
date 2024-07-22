package com.gravatar.quickeditor.ui.oauth

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.util.Consumer
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

@Composable
internal fun OAuthPage(
    appName: String,
    oauthParams: OAuthParams,
    onAuthSuccess: () -> Unit,
    onAuthError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = context.findComponentActivity()
    val coroutineScope = rememberCoroutineScope()
    var isAuthorizing by rememberSaveable { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Log.d("QuickEditor", "Result is back: $it")
    }

    if (activity != null) {
        DisposableEffect(Unit) {
            val listener = Consumer<Intent> { newIntent ->
                val code = newIntent.data?.getQueryParameter("code")
                Log.d("QuickEditor", "code: $code, data: ${newIntent.data}")
                if (code != null) {
                    coroutineScope.launch(Dispatchers.IO) {
                        isAuthorizing = true
                        Log.d("QuickEditor", "code: $code, data: ${newIntent.data}")
                        handleAuthorizationCode(code, oauthParams.clientId, oauthParams.clientSecret)?.let { token ->
                            onAuthSuccess()
                        }
                    }
                } else {
                    // handle error
                    isAuthorizing = false
                    onAuthError()
                    Log.d("QuickEditor", "Error: ${newIntent.data?.getQueryParameter("error")}")
                }
            }
            activity.addOnNewIntentListener(listener)
            launchCustomTab(launcher, oauthParams)
            onDispose {
                activity.removeOnNewIntentListener(listener)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (isAuthorizing) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "$appName needs access to your Gravatar profile",
                    textAlign = TextAlign.Center,
                )
                TextButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        launchCustomTab(launcher, oauthParams)
                    },
                ) {
                    Text(text = "Authorize")
                }
            }
        }
    }
}

private fun launchCustomTab(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>, oauthParams: OAuthParams) {
    val customTabIntent: CustomTabsIntent = CustomTabsIntent.Builder()
        .build()
    launcher.launch(
        customTabIntent.intent.apply {
            setData(Uri.parse(WordPressOauth.buildUrl(oauthParams.clientId, oauthParams.redirectUri)))
        },
    )
}

internal fun Context.findComponentActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findComponentActivity()
    else -> null
}


// This will be extracted
private suspend fun handleAuthorizationCode(code: String, clientId: String, clientSecret: String): String? {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://public-api.wordpress.com")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    val service: WordPressOAuthApi = retrofit.create(WordPressOAuthApi::class.java)
    val result = service.getToken(
        clientId,
        "wp-oauth-test://authorization-callback",
        clientSecret,
        code,
        "authorization_code",
    )

    Log.d("MainActivity", "Token: ${result.body()?.token}")
    return result.body()?.token
}

internal interface WordPressOAuthApi {
    @POST("/oauth2/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Field("client_id") clientId: String?,
        @Field("redirect_uri") redirectUri: String?,
        @Field("client_secret") clientSecret: String?,
        @Field("code") user: String?,
        @Field("grant_type") grantType: String?,
    ): Response<TokenModel>
}

internal class TokenModel(
    @SerializedName("access_token") val token: String,
    @SerializedName("token_type") val type: String,
)
