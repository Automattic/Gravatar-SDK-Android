package com.gravatar.ui.components.atomic

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gravatar.api.models.UserProfile
import com.gravatar.ui.R
import java.net.URL

public sealed class Icon(val name: String, val imageResource: Int) {
    data object Gravatar : Icon("Gravatar", R.drawable.gravatar_icon)

    data object Mastodon : Icon("Mastodon", R.drawable.mastodon_icon)

    data object Tumblr : Icon("Tumblr", R.drawable.tumblr_icon)

    data object WordPress : Icon("WordPress", R.drawable.wordpress_icon)

    companion object {
        public fun valueOf(name: String): Icon? {
            return when (name) {
                Gravatar.name -> Gravatar
                Mastodon.name -> Mastodon
                Tumblr.name -> Tumblr
                WordPress.name -> WordPress
                else -> null
            }
        }
    }
}

public class SocialMedia(val icon: Icon, val url: URL)

fun sortMediaAccordingTo(media: List<SocialMedia>, sortBy: List<String>): List<SocialMedia> {
    // Return the media list sorted by the sortBy list
    return media.sortedBy { sortBy.indexOf(it.icon.name).takeIf { it != -1 } ?: Int.MAX_VALUE }
}

public fun mediaList(profile: UserProfile): List<SocialMedia> {
    val mediaList = mutableListOf<SocialMedia>()
    // Force the Gravatar icon
    profile.profileUrl?.let {
        mediaList.add(SocialMedia(Icon.Gravatar, URL(it)))
    }
    // List and filter the other accounts from the profile
    profile.accounts?.let {
        for (account in it) {
            Icon.valueOf(account.name)?.let { icon ->
                mediaList.add(SocialMedia(icon, URL(account.url)))
            }
        }
    }
    // TODO: update with the full list
    return sortMediaAccordingTo(mediaList, listOf("Gravatar", "WordPress", "Mastodon", "Tumblr"))
}

@Composable
fun SocialIcon(media: SocialMedia, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    IconButton(
        onClick = {
            uriHandler.openUri(media.url.toString())
        },
        modifier = modifier,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(media.icon.imageResource),
            contentDescription = media.icon.name,
        )
    }
}

@Composable
fun SocialIconRow(socialMedia: List<SocialMedia>, modifier: Modifier = Modifier, maxIcons: Int = 4) {
    var count = 0
    Row(modifier = modifier) {
        for (media in socialMedia) {
            if (count++ >= maxIcons) {
                break
            }
            SocialIcon(media = media, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}

@Composable
fun SocialIconRow(profile: UserProfile, modifier: Modifier = Modifier, maxIcons: Int = 4) {
    SocialIconRow(mediaList(profile), modifier, maxIcons)
}
