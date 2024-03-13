package com.gravatar.events

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.GravatarApi
import com.gravatar.events.gravatar.parseGravatarHash
import com.gravatar.events.scanner.CameraPreview
import com.gravatar.events.scanner.Permission
import com.gravatar.events.scanner.Reticle
import com.gravatar.events.ui.components.EmailCheckingView
import com.gravatar.events.ui.theme.GravatarTheme
import com.gravatar.models.UserProfile
import com.gravatar.models.UserProfiles
import com.gravatar.ui.components.ProfileListItem

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
                    })
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
            val context = LocalContext.current
            CameraPreview { code ->
                val hash = parseGravatarHash(code)
                hash?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
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
