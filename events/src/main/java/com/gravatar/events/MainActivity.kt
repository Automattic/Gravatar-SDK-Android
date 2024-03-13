package com.gravatar.events

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravatar.events.ui.theme.GravatarTheme
import com.gravatar.models.UserProfile
import com.gravatar.ui.components.ProfileCard
import com.gravatar.ui.components.ProfileListItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EventsApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsApp() {
    GravatarTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
                bottomSheetState = rememberStandardBottomSheetState(
                    initialValue = SheetValue.PartiallyExpanded,
                ),
            )

            BottomSheetScaffold(
                sheetContent = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ProfileCard(
                            UserProfile(
                                "61b4e17387f9a143d28f5083988b2999",
                                displayName = "John Doe",
                                aboutMe = "I'm John Doe",
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally),
                            avatarImageSize = 64.dp,
                        )
                    }
                    ProfilesList(
                        profiles = listOf(
                            UserProfile(hash = "a", displayName = "John Doe", aboutMe = "Software Engineer"),
                            UserProfile(hash = "b", displayName = "Daniel Smith", aboutMe = "Artist and Youtuber"),
                            UserProfile(hash = "c", displayName = "Mary Johnson", aboutMe = "Artist and Youtuber"),
                            UserProfile(hash = "d", displayName = "Robert Brown", aboutMe = "Artist and Youtuber"),
                            UserProfile(hash = "e", displayName = "Clarence White", aboutMe = "Artist and Youtuber"),
                            UserProfile(hash = "f", displayName = "Ana", aboutMe = "Artist and Youtuber"),
                            UserProfile(hash = "g", displayName = "Erika", aboutMe = "Artist and Youtuber"),
                            UserProfile(hash = "h", displayName = "Sophia", aboutMe = "Artist and Youtuber"),
                            UserProfile(hash = "i", displayName = "Charles", aboutMe = "Artist and Youtuber"),
                            UserProfile(hash = "g", displayName = "Eneko", aboutMe = "Artist and Youtuber"),
                        ),
                    )
                },
                sheetPeekHeight = 500.dp,
                scaffoldState = bottomSheetScaffoldState,
                content = {
                    Text(text = "Content here")
                },
            )
        }
    }
}

@Composable
fun ProfilesList(profiles: List<UserProfile>) {
    LazyColumn {
        items(profiles.size) { index ->
            ProfileListItem(modifier = Modifier.padding(8.dp), profile = profiles[index], avatarImageSize = 56.dp)
        }
    }
}
