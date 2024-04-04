package com.gravatar.ui.components.atomic

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.gravatar.api.models.UserProfile
import com.gravatar.ui.R
import java.net.URL

public enum class LocalIcon(val shortname: String, val imageResource: Int) {
    Gravatar("gravatar", R.drawable.gravatar_icon),
    Calendly("calendly", R.drawable.calendly_icon),
    Fediverse("fediverse", R.drawable.fediverse_icon),
    Foursquare("foursquare", R.drawable.foursquare_icon),
    Github("github", R.drawable.github_icon),
    Instagram("instagram", R.drawable.instagram_icon),
    Mastodon("mastodon", R.drawable.mastodongeneric_icon),
    StackOverflow("stackoverflow", R.drawable.stackoverflow_icon),
    TikTok("tiktok", R.drawable.tiktok_icon),
    TripIt("tripit", R.drawable.tripit_icon),
    Tumblr("tumblr", R.drawable.tumblr_icon),
    Twitch("twitch", R.drawable.twitch_icon),
    Twitter("twitter", R.drawable.twitter_icon),
    Vimeo("vimeo", R.drawable.vimeo_icon),
    WordPress("wordpress", R.drawable.wordpress_icon),
    ;

    companion object {
        private val shortnames = entries.associateBy { it.shortname }

        public fun valueOf(shortname: String?): LocalIcon? {
            return shortnames[shortname]
        }
    }
}

public class SocialMedia(val url: URL, val name: String, val iconUrl: URL? = null, val icon: LocalIcon? = null)

public fun mediaList(profile: UserProfile): List<SocialMedia> {
    val mediaList = mutableListOf<SocialMedia>()
    // Force the Gravatar icon
    profile.profileUrl?.let {
        mediaList.add(SocialMedia(URL(it), LocalIcon.Gravatar.name, icon = LocalIcon.Gravatar))
    }
    // List and filter the other accounts from the profile, keep the same order coming from UserProfile.accounts list
    profile.accounts?.forEach { account ->
        if (LocalIcon.valueOf(account.shortname) != null) {
            // Add local icon if the shortname exists in our predefined list
            mediaList.add(SocialMedia(URL(account.url), account.name, icon = LocalIcon.valueOf(account.shortname)))
        } else {
            // Add a "remote" icon (using the url coming from the endpoint response)
            mediaList.add(SocialMedia(URL(account.url), account.name, iconUrl = URL(account.iconUrl)))
        }
    }
    return mediaList
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
        if (media.icon != null) {
            Icon(
                imageVector = ImageVector.vectorResource(media.icon.imageResource),
                modifier = Modifier.fillMaxSize(),
                contentDescription = media.name,
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(media.iconUrl.toString())
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = media.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.FillHeight,
            )
        }
    }
}

@Composable
fun SocialIconRow(socialMedia: List<SocialMedia>, modifier: Modifier = Modifier, maxIcons: Int = 4) {
    Row(modifier = modifier) {
        socialMedia.take(maxIcons).forEach { media ->
            SocialIcon(media = media, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun SocialIconRow(profile: UserProfile, modifier: Modifier = Modifier, maxIcons: Int = 4) {
    SocialIconRow(mediaList(profile), modifier, maxIcons)
}

@Preview(showBackground = true)
@Composable
fun SocialIconRowPreview() {
    SocialIconRow(
        socialMedia = LocalIcon.entries.map {
            SocialMedia(URL("https://${it.shortname}.com"), it.shortname, icon = it)
        },
        maxIcons = 5,
    )
}
