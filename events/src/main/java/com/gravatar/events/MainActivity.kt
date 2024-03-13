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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.GravatarApi
import com.gravatar.events.gravatar.parseGravatarHash
import com.gravatar.events.scanner.ScannerPreview
import com.gravatar.events.scanner.Permission
import com.gravatar.events.scanner.Reticle
import com.gravatar.events.ui.components.EmailCheckingView
import com.gravatar.events.ui.theme.GravatarTheme
import com.gravatar.models.UserProfile
import com.gravatar.models.UserProfiles
import com.gravatar.ui.components.ProfileListItem
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var contacts by remember { mutableStateOf(getContacts()) }
            EventsApp(contacts)
        }
    }

    private fun getContacts(): List<String> {
        val sharedPreferences = getSharedPreferences("events", MODE_PRIVATE)
//        var contacts = sharedPreferences.getStringSet("contacts", mutableSetOf<String>())
        var contacts = listOf<String>(
            "741fc2861114a819b0ff85018ec8efd84e7c78483df32daf8fc5cf654869da3d",
            "741fc2861114a819b0ff85018ec8efd84e7c78483df32daf8fc5cf654869da3d",
            "741fc2861114a819b0ff85018ec8efd84e7c78483df32daf8fc5cf654869da3d",
            "741fc2861114a819b0ff85018ec8efd84e7c78483df32daf8fc5cf654869da3d",
            "741fc2861114a819b0ff85018ec8efd84e7c78483df32daf8fc5cf654869da3d",
            "741fc2861114a819b0ff85018ec8efd84e7c78483df32daf8fc5cf654869da3d",
        )
        return contacts?.toList() ?: emptyList()
    }

    private fun saveContact(profileHash: String) {
        val sharedPreferences = getSharedPreferences("events", MODE_PRIVATE)
        val contacts = sharedPreferences.getStringSet(
            "contacts",
            mutableSetOf<String>(),
        )?.toMutableSet() ?: mutableSetOf()
        contacts.add(profileHash)
        sharedPreferences.edit().putStringSet("contacts", contacts).apply()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsApp(contacts: List<String>) {
    GravatarTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberStandardBottomSheetState(
                    initialValue = SheetValue.PartiallyExpanded,
                ),
            )
            var hash by remember { mutableStateOf("") }
            var party by remember { mutableStateOf(listOf<Party>()) }

            BottomSheetScaffold(
                sheetContent = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        EmailCheckingView(
                            hash = hash,
                            onEmailValidated = {},
                        )
                    }
                    ProfilesList(
                        profiles = contacts,
                    )
                },
                sheetPeekHeight = 500.dp,
                scaffoldState = bottomSheetScaffoldState,
                content = {
                    Scanner(Modifier.padding(bottom = 500.dp), onCodeScanned = {
                        // TODO: Save the hash somewhere
                        hash = it
                        party = listOf(Party(
                            speed = 0f,
                            maxSpeed = 30f,
                            damping = 0.9f,
                            spread = 360,
                            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                            position = Position.Relative(0.5, 0.3),
                            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
                        ))
                    })
                    KonfettiView(
                        modifier = Modifier.fillMaxSize(),
                        parties = party,
                    )
                },
            )
        }
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
    EventsApp(emptyList())
}
