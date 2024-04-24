package com.gravatar.demoapp.ui

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Size
import com.gravatar.AvatarQueryOptions
import com.gravatar.AvatarUrl
import com.gravatar.DefaultAvatarOption
import com.gravatar.ImageRating
import com.gravatar.api.models.UserProfiles
import com.gravatar.demoapp.BuildConfig
import com.gravatar.demoapp.R
import com.gravatar.demoapp.theme.GravatarDemoAppTheme
import com.gravatar.demoapp.ui.components.GravatarEmailInput
import com.gravatar.demoapp.ui.model.SettingsState
import com.gravatar.services.ErrorType
import com.gravatar.services.GravatarListener
import com.gravatar.services.ProfileService
import com.gravatar.types.Email
import com.gravatar.ui.components.LargeProfile
import com.gravatar.ui.components.LargeProfileSummary
import com.gravatar.ui.components.MiniProfileCard
import com.gravatar.ui.components.ProfileCard
import com.gravatar.ui.components.UserProfileState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DemoGravatarApp() {
    GravatarDemoAppTheme {
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        var gravatarUrl by remember { mutableStateOf("", neverEqualPolicy()) }

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        ) { innerPadding ->
            val defaultErrorMessage = stringResource(R.string.snackbar_unknown_error_message)
            GravatarTabs(
                modifier = Modifier.padding(innerPadding),
                gravatarUrl,
                { gravatarUrl = it },
            ) { message, exception ->
                showSnackBar(scope, snackbarHostState, message, exception, defaultErrorMessage)
            }
        }
    }
}

val defaultAvatarOptions by lazy {
    listOf(
        DefaultAvatarOption.MysteryPerson,
        DefaultAvatarOption.Status404,
        DefaultAvatarOption.Identicon,
        DefaultAvatarOption.MonsterId,
        DefaultAvatarOption.Wavatar,
        DefaultAvatarOption.Retro,
        DefaultAvatarOption.TransparentPNG,
        DefaultAvatarOption.RoboHash,
        DefaultAvatarOption.CustomUrl(
            "https://t.ly/o2EXH",
        ),
    )
}

private fun showSnackBar(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    message: String?,
    throwable: Throwable?,
    defaultMessage: String,
) {
    Log.e("DemoGravatarApp", "${message.orEmpty()}\n${throwable?.stackTraceToString().orEmpty()}")
    scope.launch {
        snackbarHostState.showSnackbar(
            message = message ?: throwable?.message ?: defaultMessage,
            duration = SnackbarDuration.Short,
        )
    }
}

@Composable
private fun GravatarTabs(
    modifier: Modifier = Modifier,
    gravatarUrl: String,
    onGravatarUrlChanged: (String) -> Unit,
    showSnackBar: (String?, Throwable?) -> Unit,
) {
    var tabIndex by remember { mutableStateOf(0) }

    val tabs = listOf(
        stringResource(R.string.tab_label_avatar),
        stringResource(R.string.tab_label_profile),
        stringResource(R.string.tab_label_avatar_update),
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                )
            }
        }
        when (tabIndex) {
            0 -> AvatarTab(modifier, gravatarUrl, onGravatarUrlChanged, showSnackBar)
            1 -> ProfileTab(modifier, showSnackBar)
            2 -> AvatarUpdateTab(showSnackBar, modifier)
        }
    }
}

@Composable
private fun ProfileTab(modifier: Modifier = Modifier, onError: (String?, Throwable?) -> Unit) {
    var email by remember { mutableStateOf(BuildConfig.DEMO_EMAIL, neverEqualPolicy()) }
    var hash by remember { mutableStateOf("", neverEqualPolicy()) }
    var profileState: UserProfileState? by remember { mutableStateOf(null, neverEqualPolicy()) }
    var error by remember { mutableStateOf("") }
    val profileService = ProfileService()
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GravatarEmailInput(
                email = email,
                onValueChange = { email = it },
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
            Button(
                onClick = {
                    keyboardController?.hide()
                    profileState = UserProfileState.Loading
                    error = ""
                    profileService.fetch(
                        Email(email),
                        object : GravatarListener<UserProfiles> {
                            override fun onSuccess(response: UserProfiles) {
                                profileState = response.entry.firstOrNull()?.let { UserProfileState.Loaded(it) }
                            }

                            override fun onError(errorType: ErrorType) {
                                onError(errorType.name, null)
                                error = errorType.name
                            }
                        },
                    )
                },
            ) { Text(text = stringResource(R.string.button_get_profile)) }
            // Show the hash and loading indicator
            if (hash.isNotEmpty()) {
                GravatarDivider()
                LabelledText(R.string.gravatar_generated_hash_label, text = hash)
                GravatarDivider()
                if ((profileState is UserProfileState.Loading)) {
                    CircularProgressIndicator()
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Show the profile card if we got a result and there is no error and it's not loading
            if ((profileState is UserProfileState.Loaded) && error.isEmpty() && profileState != null) {
                (profileState as? UserProfileState.Loaded)?.let {
                    ProfileCard(
                        it.userProfile,
                        Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .fillMaxWidth()
                            .padding(24.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            if (error.isEmpty()) {
                profileState?.let {
                    MiniProfileCard(
                        it,
                        Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .align(Alignment.Start)
                            .fillMaxWidth()
                            .padding(24.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            if ((profileState is UserProfileState.Loaded) && error.isEmpty() && profileState != null) {
                (profileState as? UserProfileState.Loaded)?.let {
                    LargeProfile(
                        it.userProfile,
                        Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .fillMaxWidth()
                            .padding(24.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LargeProfileSummary(
                        it.userProfile,
                        Modifier
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 8.dp)
                            .fillMaxWidth(),
                    )
                }
            } else {
                if (error.isNotEmpty()) {
                    Text(text = error)
                }
            }
        }
    }
}

@Composable
private fun AvatarTab(
    modifier: Modifier = Modifier,
    gravatarUrl: String,
    onGravatarUrlChanged: (String) -> Unit,
    onError: (String?, Throwable?) -> Unit,
) {
    var settingsState by remember {
        mutableStateOf(
            SettingsState(
                email = BuildConfig.DEMO_EMAIL,
                size = null,
                defaultAvatarImageEnabled = false,
                selectedDefaultAvatar = DefaultAvatarOption.MonsterId,
                defaultAvatarOptions = defaultAvatarOptions,
                forceDefaultAvatar = false,
                imageRatingEnabled = false,
                imageRating = ImageRating.General,
            ),
        )
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Surface(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GravatarImageSettings(
                settingsState = settingsState,
                onEmailChanged = { settingsState = settingsState.copy(email = it) },
                onSizeChange = { settingsState = settingsState.copy(size = it) },
                onLoadGravatarClicked = {
                    @Suppress("TooGenericExceptionCaught")
                    try {
                        keyboardController?.hide()
                        onGravatarUrlChanged(
                            AvatarUrl(
                                Email(settingsState.email),
                                AvatarQueryOptions(
                                    preferredSize = settingsState.size,
                                    defaultAvatarOption = if (settingsState.defaultAvatarImageEnabled) {
                                        settingsState.selectedDefaultAvatar
                                    } else {
                                        null
                                    },
                                    forceDefaultAvatar = if (settingsState.forceDefaultAvatar) true else null,
                                    rating = if (settingsState.imageRatingEnabled) settingsState.imageRating else null,
                                ),
                            ).url().toString(),
                        )
                    } catch (e: Exception) {
                        onError(null, e.fillInStackTrace())
                    }
                },
                onDefaultAvatarImageEnabledChanged = {
                    settingsState = settingsState.copy(defaultAvatarImageEnabled = it)
                },
                onDefaultAvatarImageChanged = { settingsState = settingsState.copy(selectedDefaultAvatar = it) },
                onForceDefaultAvatarChanged = { settingsState = settingsState.copy(forceDefaultAvatar = it) },
                onImageRatingChanged = { settingsState = settingsState.copy(imageRating = it) },
                onImageRatingEnabledChange = { settingsState = settingsState.copy(imageRatingEnabled = it) },
            )

            if (gravatarUrl.isNotEmpty()) {
                GravatarDivider()

                LabelledText(R.string.gravatar_generated_url_label, gravatarUrl)

                GravatarDivider()

                GravatarImage(gravatarUrl = gravatarUrl, onError = onError)
            }
        }
    }
}

@Composable
fun GravatarDivider() = HorizontalDivider(
    thickness = 1.dp,
    color = MaterialTheme.colorScheme.primary,
    modifier = Modifier.padding(vertical = 8.dp),
)

@Composable
fun LabelledText(
    @StringRes label: Int,
    text: String,
) {
    Text(
        text = stringResource(label),
        fontWeight = FontWeight.Bold,
    )
    Text(text = text)
}

@Composable
fun GravatarImage(gravatarUrl: String, onError: (String?, Throwable?) -> Unit) {
    val forceRefresh = remember { mutableStateOf(0) }
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(gravatarUrl)
            .memoryCachePolicy(CachePolicy.WRITE_ONLY)
            .diskCachePolicy(CachePolicy.DISABLED)
            .setParameter("forceRefresh", forceRefresh)
            .size(Size.ORIGINAL)
            .build(),
        onState = { state ->
            if (state is AsyncImagePainter.State.Error) {
                onError(state.result.throwable.message, state.result.throwable)
                forceRefresh.value++
            }
        },
    )

    Image(
        painter = painter,
        contentDescription = "",
    )
}
