package com.gravatar.ui.components.atomic

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.extensions.defaultProfile
import com.gravatar.extensions.formattedUserInfo
import com.gravatar.restapi.models.Profile
import com.gravatar.ui.R
import com.gravatar.ui.TextSkeletonEffect
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.LoadingToLoadedProfileStatePreview
import com.gravatar.ui.extensions.toApi2ComponentStateProfile
import com.gravatar.ui.extensions.toApi2Profile
import com.gravatar.api.models.Profile as LegacyProfile

/**
 * [UserInfo] is a composable that displays a user's information in a formatted way.
 * The user's information includes their company, job title, pronunciation, pronouns, and current
 * location when available.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the default text content
 * @param content Composable to display the formatted user information
 */
@Composable
public fun UserInfo(
    profile: Profile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
    content: @Composable ((String, Modifier) -> Unit) = { userInfo, contentModifier ->
        UserInfoDefaultContent(userInfo, textStyle, contentModifier)
    },
) {
    content(profile.formattedUserInfo(), modifier)
}

/**
 * [UserInfo] is a composable that displays a user's information in a formatted way.
 * The user's information includes their company, job title, pronunciation, pronouns, and current
 * location when available.
 *
 * @param state The user's profile state
 * @param modifier Composable modifier
 * @param skeletonModifier Composable modifier for the loading skeleton component
 * @param textStyle The style to apply to the default text content
 * @param content Composable to display the formatted user information
 */
@JvmName("UserInfoWithComponentState")
@Composable
public fun UserInfo(
    state: ComponentState<Profile>,
    modifier: Modifier = Modifier,
    skeletonModifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
    content: @Composable ((String, Modifier) -> Unit) = { userInfo, contentModifier ->
        UserInfoDefaultContent(userInfo, textStyle, contentModifier)
    },
) {
    when (state) {
        is ComponentState.Loading -> {
            TextSkeletonEffect(
                textStyle = textStyle,
                modifier = skeletonModifier,
                skeletonVerticalPadding = 4.dp,
            )
        }

        is ComponentState.Loaded -> {
            UserInfo(state.loadedValue, modifier, textStyle, content)
        }

        ComponentState.Empty -> content.invoke(stringResource(R.string.empty_state_user_info), modifier)
    }
}

/**
 * [UserInfo] is a composable that displays a user's information in a formatted way.
 * The user's information includes their company, job title, pronunciation, pronouns, and current
 * location when available.
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the default text content
 * @param content Composable to display the formatted user information
 */
@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.ui.components.atomic.UserInfo"),
    level = DeprecationLevel.WARNING,
)
@Composable
public fun UserInfo(
    profile: LegacyProfile,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
    content: @Composable ((String, Modifier) -> Unit) = { userInfo, contentModifier ->
        UserInfoDefaultContent(userInfo, textStyle, contentModifier)
    },
) {
    UserInfo(profile = profile.toApi2Profile(), modifier = modifier, textStyle = textStyle, content = content)
}

/**
 * [UserInfo] is a composable that displays a user's information in a formatted way.
 * The user's information includes their company, job title, pronunciation, pronouns, and current
 * location when available.
 *
 * @param state The user's profile state
 * @param modifier Composable modifier
 * @param textStyle The style to apply to the default text content
 * @param content Composable to display the formatted user information
 */
@Deprecated(
    "This class is deprecated and will be removed in a future release.",
    replaceWith = ReplaceWith("com.gravatar.ui.components.atomic.UserInfo"),
    level = DeprecationLevel.WARNING,
)
@Composable
public fun UserInfo(
    state: ComponentState<LegacyProfile>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.outline),
    content: @Composable ((String, Modifier) -> Unit) = { userInfo, contentModifier ->
        UserInfoDefaultContent(userInfo, textStyle, contentModifier)
    },
) {
    UserInfo(state.toApi2ComponentStateProfile(), modifier, skeletonModifier = Modifier, textStyle, content)
}

@Composable
private fun UserInfoDefaultContent(userInfo: String, textStyle: TextStyle, modifier: Modifier) = Text(
    userInfo,
    modifier = modifier,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
    style = textStyle,
)

@Preview
@Composable
private fun UserInfoPreview() {
    UserInfo(
        defaultProfile(
            "",
            location = "Crac'h, France",
            pronouns = "They/Them",
            pronunciation = "Tony with a P",
            jobTitle = "Pony Trainer",
            company = "Pony Land",
        ),
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LocationStatePreview() {
    LoadingToLoadedProfileStatePreview { UserInfo(it) }
}
