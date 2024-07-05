package com.gravatar.ui.components.atomic

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.extensions.defaultProfile
import com.gravatar.restapi.models.Profile
import com.gravatar.ui.R
import com.gravatar.ui.TextSkeletonEffect
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.LoadingToLoadedProfileStatePreview
import com.gravatar.ui.extensions.toApi2ComponentStateProfile
import com.gravatar.ui.extensions.toApi2Profile
import com.gravatar.api.models.Profile as LegacyProfile

/**
 * [AboutMe] is a composable that displays a user's about me description.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the default text content
 * @param content Composable to display the user's about me description
 */
@Composable
public fun AboutMe(
    profile: Profile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    content: @Composable ((String, Modifier) -> Unit) = { userInfo, contentModifier ->
        AboutMeDefaultContent(userInfo, textStyle, contentModifier)
    },
) {
    content(profile.description, modifier)
}

/**
 * [AboutMe] is a composable that displays a user's about me description.
 *
 * @param state The user's profile loading state
 * @param modifier Composable modifier
 * @param skeletonModifier Composable modifier for the loading skeleton component
 * @param textStyle The style to apply to the default text content
 * @param content Composable to display the user's about me description
 */
@JvmName("AboutMeWithComponentState")
@Composable
public fun AboutMe(
    state: ComponentState<Profile>,
    modifier: Modifier = Modifier,
    skeletonModifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    content: @Composable ((String, Modifier) -> Unit) = { userInfo, contentModifier ->
        AboutMeDefaultContent(userInfo, textStyle, contentModifier)
    },
) {
    when (state) {
        is ComponentState.Loading -> {
            Column(modifier = skeletonModifier) {
                TextSkeletonEffect(
                    textStyle = textStyle,
                    modifier = Modifier.fillMaxWidth(),
                    skeletonVerticalPadding = 4.dp,
                )
                TextSkeletonEffect(
                    textStyle = textStyle,
                    modifier = Modifier.fillMaxWidth(0.7f),
                    skeletonVerticalPadding = 4.dp,
                )
            }
        }

        is ComponentState.Loaded -> {
            AboutMe(state.loadedValue, modifier, textStyle, content)
        }

        ComponentState.Empty -> {
            DashedBorder(modifier) {
                content.invoke(
                    stringResource(id = R.string.empty_state_about_me),
                    Modifier.padding(8.dp),
                )
            }
        }
    }
}

/**
 * [AboutMe] is a composable that displays a user's about me description.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the default text content
 * @param content Composable to display the user's about me description
 */
@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.ui.components.atomic.AboutMe"),
    level = DeprecationLevel.WARNING,
)
@Composable
public fun AboutMe(
    profile: LegacyProfile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    content: @Composable ((String, Modifier) -> Unit) = { userInfo, contentModifier ->
        AboutMeDefaultContent(userInfo, textStyle, contentModifier)
    },
) {
    AboutMe(profile.toApi2Profile(), modifier, textStyle, content)
}

/**
 * [AboutMe] is a composable that displays a user's about me description.
 *
 * @param state The user's profile loading state
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the default text content
 * @param content Composable to display the user's about me description
 */
@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.ui.components.atomic.AboutMe"),
    level = DeprecationLevel.WARNING,
)
@Composable
public fun AboutMe(
    state: ComponentState<LegacyProfile>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    content: @Composable ((String, Modifier) -> Unit) = { userInfo, contentModifier ->
        AboutMeDefaultContent(userInfo, textStyle, contentModifier)
    },
) {
    AboutMe(
        state = state.toApi2ComponentStateProfile(),
        modifier = modifier,
        skeletonModifier = Modifier,
        textStyle = textStyle,
        content = content,
    )
}

@Composable
private fun DashedBorder(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val stroke = Stroke(
        width = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(9f, 4f), 0f),
    )
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier
            .drawBehind {
                drawRoundRect(
                    color = borderColor,
                    style = stroke,
                    cornerRadius = CornerRadius(4.dp.toPx()),
                )
            },
    ) {
        content.invoke()
    }
}

@Composable
private fun AboutMeDefaultContent(userInfo: String, textStyle: TextStyle, modifier: Modifier) = Text(
    userInfo,
    modifier = modifier,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
    style = textStyle,
)

@Preview
@Composable
private fun AboutMePreview() {
    AboutMe(
        defaultProfile(
            hash = "",
            description = "I'm a farmer, I love to code. I ride my bicycle to work. One apple a day keeps the " +
                "doctor away. This about me description is quite long, this is good for testing.",
        ),
    )
}

@Preview
@Composable
private fun AboutMeEmptyStatePreview() {
    AboutMe(ComponentState.Empty as ComponentState<Profile>)
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LocationStatePreview() {
    LoadingToLoadedProfileStatePreview { AboutMe(it) }
}
