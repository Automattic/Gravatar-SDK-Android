package com.gravatar.ui.components.atomic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gravatar.api.models.UserProfile
import com.gravatar.extensions.profileUrl
import com.gravatar.ui.R

/**
 * ViewProfileButton is a composable that displays a button to view a user's profile.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the text
 * @param inlineContent The content to display inline with the text, by default it is an arrow icon.
 * It can be null if no content is needed.
 */
@Composable
public fun ViewProfileButton(
    profile: UserProfile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground),
    inlineContent: @Composable ((String) -> Unit)? = { DefaultInlineContent(textStyle.color) },
) {
    val uriHandler = LocalUriHandler.current
    val iconInlineId = "->"
    val text = buildAnnotatedString {
        append(stringResource(R.string.view_profile_button))
        if (inlineContent != null) {
            append(" ")
            // Append a placeholder string "[myBox]" and attach an annotation "inlineContent" on it.
            appendInlineContent(iconInlineId, "[icon]")
        }
    }

    val inlineContentMap = mapOf(
        Pair(
            iconInlineId,
            InlineTextContent(
                Placeholder(
                    width = 16.sp,
                    height = 16.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                ),
            ) {
                inlineContent?.invoke(it)
            },
        ),
    )

    TextButton(
        onClick = {
            uriHandler.openUri(profile.profileUrl().url.toString())
        },
        contentPadding = PaddingValues(start = 0.dp, end = 0.dp),
        modifier = modifier,
    ) {
        Text(text, inlineContent = inlineContentMap, style = textStyle)
    }
}

@Composable
private fun DefaultInlineContent(tintColor: Color) {
    // In RTL mode the Arrow will be mirrored
    Icon(
        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
        tint = tintColor,
        contentDescription = "",
    )
}

@Preview(showBackground = true)
@Composable
private fun ViewProfileButtonPreview() {
    Column {
        // Preview in RTL mode
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ViewProfileButton(UserProfile("4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a"))
        }
        // Preview in LTR mode
        ViewProfileButton(UserProfile("4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a"))
    }
}

@Preview(showBackground = true)
@Composable
private fun ViewProfileButtonWithCustomizedInlineContentPreview() {
    ViewProfileButton(UserProfile("4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a"), inlineContent = {
        Icon(
            painter = painterResource(R.drawable.gravatar_icon),
            contentDescription = "",
        )
    })
}

@Preview(showBackground = true)
@Composable
private fun ViewProfileButtonWithoutInlineContentPreview() {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ViewProfileButton(
            UserProfile("4539566a0223b11d28fc47c864336fa27b8fe49b5f85180178c9e3813e910d6a"),
            inlineContent = null,
        )
    }
}
