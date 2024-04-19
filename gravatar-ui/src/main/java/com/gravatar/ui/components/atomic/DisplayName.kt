package com.gravatar.ui.components.atomic

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.gravatar.api.models.UserProfile

/**
 * [DisplayName] is a composable that displays the user's display name.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the text
 */
@Composable
public fun DisplayName(
    profile: UserProfile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
) {
    Text(text = profile.displayName.orEmpty(), modifier = modifier, style = textStyle)
}
