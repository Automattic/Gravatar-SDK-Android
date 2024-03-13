package com.gravatar.events

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.GravatarApi
import com.gravatar.events.gravatar.parseGravatarHash
import com.gravatar.events.scanner.Permission
import com.gravatar.events.scanner.Reticle
import com.gravatar.events.scanner.ScannerPreview
import com.gravatar.events.ui.components.EmailCheckingView
import com.gravatar.events.ui.theme.GravatarTheme
import com.gravatar.models.UserProfile
import com.gravatar.models.UserProfiles
import com.gravatar.ui.components.ProfileListItem
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventsApp(LocalDataStore(this))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsApp(localDataStore: LocalDataStore) {
    GravatarTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberStandardBottomSheetState(
                    initialValue = SheetValue.PartiallyExpanded,
                ),
            )
            var parties by remember { mutableStateOf(listOf<Party>()) }
            var hash by remember { mutableStateOf(localDataStore.getCurrentUser() ?: "") }
            var contacts by remember { mutableStateOf(localDataStore.getContacts()) }
            var validatedHash by remember { mutableStateOf(localDataStore.getCurrentUser()) }
            var userProfile by remember { mutableStateOf<UserProfile?>(null) }

            BottomSheetScaffold(
                sheetContent = {
                    ContactsBottomSheet(validatedHash, userProfile, hash, contacts, onUserProfileLoaded = {
                        userProfile = it
                    }, onValidatedHash = {
                        validatedHash = it
                        localDataStore.saveCurrentUser(it)
                    })
                },
                sheetPeekHeight = 500.dp,
                scaffoldState = bottomSheetScaffoldState,
                content = {
                    Scanner(Modifier.padding(bottom = 500.dp), onCodeScanned = {
                        hash = it
                        parties = hashToPartyList(hash)
                        if (validatedHash == null) {
                            hash = it
                        } else {
                            localDataStore.saveContact(it)
                            contacts = localDataStore.getContacts()
                        }
                    })
                },
            )
            if (parties.isNotEmpty()) {
                KonfettiView(
                    modifier = Modifier.fillMaxSize(),
                    parties = parties,
                    object : OnParticleSystemUpdateListener {
                        override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                            if (activeSystems == 0) {
                                parties = emptyList()
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun ContactsBottomSheet(
    validatedHash: String?,
    userProfile: UserProfile?,
    hash: String,
    contacts: List<String>,
    onUserProfileLoaded: (UserProfile) -> Unit,
    onValidatedHash: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        validatedHash?.let {
            GravatarApi().getProfile(
                it,
                object : GravatarApi.GravatarListener<UserProfiles> {
                    override fun onSuccess(response: UserProfiles) {
                        onUserProfileLoaded(response.entry.first())
                    }

                    override fun onError(errorType: GravatarApi.ErrorType) {
                        // Do nothing yet
                        Log.e("EventsApp", "Error getting profile: $errorType")
                    }
                },
            )
            userProfile?.let { profile ->
                ProfileListItem(profile = profile)
            } ?: CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
        } ?: EmailCheckingView(
            hash = hash,
            onEmailValidated = {
                if (it) {
                    onValidatedHash.invoke(hash)
                }
            },
        )

        ProfilesList(
            profiles = contacts,
        )
    }
}

@Composable
fun Scanner(modifier: Modifier = Modifier, onCodeScanned: (String) -> Unit) {
    Box(
        modifier = modifier,
    ) {
        Permission(
            permission = Manifest.permission.CAMERA,
            permissionNotAvailableContent = {
                Text("O noes! No Camera!")
            },
        ) {
            ScannerPreview { code ->
                val hash = parseGravatarHash(code)
                hash?.let {
                    onCodeScanned(hash)
                }
            }
            Box(Modifier.padding(54.dp)) {
                Reticle()
            }
        }
    }
}

@Composable
fun ProfilesList(profiles: List<String>) {
    LazyColumn {
        items(profiles.size) { index ->
            var profile by remember { mutableStateOf(UserProfile()) }
            ProfileListItem(modifier = Modifier.padding(8.dp), profile = profile, avatarImageSize = 56.dp)
            GravatarApi().getProfile(
                profiles[index],
                object : GravatarApi.GravatarListener<UserProfiles> {
                    override fun onSuccess(response: UserProfiles) {
                        profile = response.entry.first()
                    }

                    override fun onError(errorType: GravatarApi.ErrorType) {
                        // Do nothing yet
                        Log.e("EventsApp", "Error getting profile: $errorType")
                    }
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    EventsApp(LocalDataStore(LocalContext.current))
}
