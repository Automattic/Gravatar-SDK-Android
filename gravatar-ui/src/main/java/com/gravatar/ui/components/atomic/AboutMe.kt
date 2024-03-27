package com.gravatar.ui.components.atomic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gravatar.api.models.UserProfile

@Composable
public fun AboutMe(profile: UserProfile, maxLines: Int = 2, modifier: Modifier = Modifier) {
    var showAboutMeDialog by remember { mutableStateOf(false) }
    var clickableText by remember { mutableStateOf(false) }

    Text(
        text = profile.aboutMe ?: "",
        modifier = modifier.clickable(enabled = clickableText) {
            showAboutMeDialog = true
        },
        maxLines = maxLines,
        onTextLayout = { textLayoutResult ->
            clickableText = textLayoutResult.hasVisualOverflow
        },
        overflow = TextOverflow.Ellipsis,
    )
    if (showAboutMeDialog) {
        Dialog(onDismissRequest = { showAboutMeDialog = false }) {
            Card(
                modifier = Modifier.wrapContentSize(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = profile.aboutMe ?: "",
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentSize(),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview
@Composable
private fun AboutMe() {
    AboutMe(
        UserProfile(
            "4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a",
            aboutMe = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
                "doctor away. This about me description is quite long, this is good for testing.",
        ),
    )
}
