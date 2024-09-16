package com.gravatar.ui.components.atomic

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.gravatar.GravatarConstants
import com.gravatar.extensions.defaultProfile
import com.gravatar.extensions.profileUrl
import com.gravatar.restapi.models.Profile
import com.gravatar.restapi.models.VerifiedAccount
import com.gravatar.ui.R
import com.gravatar.ui.components.ComponentState
import com.gravatar.ui.components.LoadingToLoadedProfileStatePreview
import com.gravatar.ui.skeletonEffect
import java.net.MalformedURLException
import java.net.URI
import java.net.URL

/**
 * LocalIcon is a predefined list of social media icons that can be used in the SocialIcon composable.
 *
 * @property shortname The shortname of the social media platform.
 * @property imageResource The drawable resource ID of the icon.
 */
public enum class LocalIcon(
    public val shortname: String,
    @DrawableRes public val imageResource: Int,
) {
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

    public companion object {
        private val shortnames = entries.associateBy { it.shortname }

        /**
         * Returns the LocalIcon enum value for the given label.
         */
        public fun valueForLabel(shortname: String): LocalIcon? {
            return shortnames[shortname.lowercase()]
        }
    }
}

/**
 * SocialMedia is a data class that represents a social media account that Gravatar users can add to their profiles.
 *
 * @property url The [URL] of the social media account.
 * @property name The name of the social media platform.
 * @property iconUrl The [URL] of the icon for the social media platform.
 * @property icon The [LocalIcon] for the social media platform.
 */
public class SocialMedia(
    public val url: URL,
    public val name: String,
    public val iconUrl: URL? = null,
    public val icon: LocalIcon? = null,
)

private fun mediaList(profile: Profile): List<SocialMedia> {
    val mediaList = mutableListOf<SocialMedia>()
    // Force the Gravatar icon
    mediaList.add(SocialMedia(profile.profileUrl().url, LocalIcon.Gravatar.name, icon = LocalIcon.Gravatar))
    // List and filter the other accounts from the profile, keep the same order coming from UserProfile.accounts list
    profile.verifiedAccounts.forEach { account ->
        try {
            if (LocalIcon.valueForLabel(account.serviceLabel) != null) {
                // Add local icon if the shortname exists in our predefined list
                mediaList.add(
                    SocialMedia(
                        account.url.toURL(),
                        account.serviceLabel,
                        icon = LocalIcon.valueForLabel(account.serviceLabel),
                    ),
                )
            } else {
                // Add a "remote" icon (using the url coming from the endpoint response)
                mediaList.add(
                    SocialMedia(
                        account.url.toURL(),
                        account.serviceLabel,
                        iconUrl = URL(account.serviceIcon.toString()),
                    ),
                )
            }
        } catch (e: MalformedURLException) {
            // Ignore invalid account or icon URLs that could be returned by the endpoint
        }
    }
    return mediaList
}

/**
 * [SocialIcon] is a composable that displays a clickable icon for a social media account.
 * The link will navigate to the Gravatar user's profile on the social media platform.
 *
 * @param media The social media account to display
 * @param modifier Composable modifier
 */
@Composable
public fun SocialIcon(media: SocialMedia, modifier: Modifier = Modifier) {
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

/**
 * [SocialIconRow] is a composable that displays a row of clickable [SocialIcon].
 *
 * @param socialMedia The list of social media accounts to display
 * @param modifier Composable modifier
 * @param maxIcons The maximum number of icons to display
 */
@Composable
public fun SocialIconRow(socialMedia: List<SocialMedia>, modifier: Modifier = Modifier, maxIcons: Int = 4) {
    Row(modifier = modifier) {
        socialMedia.take(maxIcons).forEach { media ->
            SocialIcon(media = media, modifier = Modifier.size(32.dp))
        }
    }
}

/**
 * [SocialIconRow] is a composable that displays a row of clickable [SocialIcon].
 *
 * @param profile The user's profile information
 * @param modifier Composable modifier
 * @param maxIcons The maximum number of icons to display
 */
@Composable
public fun SocialIconRow(profile: Profile, modifier: Modifier = Modifier, maxIcons: Int = 4) {
    SocialIconRow(mediaList(profile), modifier, maxIcons)
}

/**
 * [SocialIconRow] is a composable that displays a row of clickable [SocialIcon].
 *
 * @param state The user's profile state
 * @param modifier Composable modifier
 * @param maxIcons The maximum number of icons to display
 */
@Composable
public fun SocialIconRow(state: ComponentState<Profile>, modifier: Modifier = Modifier, maxIcons: Int = 4) {
    when (state) {
        is ComponentState.Loading -> {
            Row(
                modifier = modifier.padding(start = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                repeat(maxIcons) {
                    Box(
                        Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .skeletonEffect(),
                    )
                }
            }
        }

        is ComponentState.Loaded -> {
            SocialIconRow(state.loadedValue, modifier, maxIcons)
        }

        ComponentState.Empty -> {
            SocialIcon(
                media = SocialMedia(
                    URL(GravatarConstants.GRAVATAR_BASE_URL),
                    LocalIcon.Gravatar.name,
                    icon = LocalIcon.Gravatar,
                ),
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

internal fun Modifier.offsetGravatarIcon(): Modifier = this.offset(x = (-5).dp)

@Preview(showBackground = true)
@Composable
private fun SocialIconRowPreview() {
    val userProfile = defaultProfile(
        hash = "",
        verifiedAccounts = listOf(
            VerifiedAccount {
                serviceType = "mastodon"
                serviceLabel = "Mastodon"
                url = URI("https://example.com")
                serviceIcon = URI("https://example.com/icon.svg")
            },
            VerifiedAccount {
                serviceType = "tumblr"
                serviceLabel = "Tumblr"
                url = URI("https://example.com")
                serviceIcon = URI("https://example.com/icon.svg")
            },
            VerifiedAccount {
                serviceType = "tiktok"
                serviceLabel = "TikTok"
                url = URI("https://example.com")
                serviceIcon = URI("https://example.com/icon.svg")
            },
            VerifiedAccount {
                serviceType = "wordpress"
                serviceLabel = "WordPress"
                url = URI("https://example.com")
                serviceIcon = URI("https://example.com/icon.svg")
            },
            VerifiedAccount {
                serviceType = "github"
                serviceLabel = "GitHub"
                url = URI("https://example.com")
                serviceIcon = URI("https://example.com/icon.svg")
            },
        ),
    )
    SocialIconRow(userProfile, maxIcons = 5)
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LocationStatePreview() {
    LoadingToLoadedProfileStatePreview { SocialIconRow(it) }
}
