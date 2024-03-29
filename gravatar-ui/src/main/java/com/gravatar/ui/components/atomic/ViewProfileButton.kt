package com.gravatar.ui.components.atomic

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gravatar.api.models.UserProfile
import com.gravatar.ui.R

@Composable
public fun ViewProfileButton(profile: UserProfile, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val arrowString = "->"
    val text = buildAnnotatedString {
        append(stringResource(R.string.view_profile_button))
        append(" ")
        // Append a placeholder string "[myBox]" and attach an annotation "inlineContent" on it.
        appendInlineContent(arrowString, "[arrow]")
    }

    val inlineContent = mapOf(
        Pair(
            arrowString,
            InlineTextContent(
                Placeholder(
                    width = 16.sp,
                    height = 16.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                ),
            ) {
                // In RTL mode the Arrow will be mirrored
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    tint = LocalTextStyle.current.color,
                    contentDescription = "",
                )
            },
        ),
    )

    TextButton(
        onClick = {
            uriHandler.openUri(profile.profileUrl.toString())
        },
        modifier = modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
        contentPadding = PaddingValues(start = 0.dp, end = 0.dp),
    ) {
        Text(text, inlineContent = inlineContent)
    }
}
