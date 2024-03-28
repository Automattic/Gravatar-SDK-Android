package com.gravatar.ui.components.atomic

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.gravatar.ui.R

public sealed class SocialIcon(val name: String, val imageResource: Int) {
    data object Gravatar : SocialIcon("Gravatar", R.drawable.gravatar_icon)

    data object Mastodon : SocialIcon("Mastodon", R.drawable.mastodon_icon)

    data object Tumblr : SocialIcon("Tumblr", R.drawable.tumblr_icon)

    data object WordPress : SocialIcon("WordPress", R.drawable.wordpress_icon)

    companion object {
        fun values(): Array<SocialIcon> {
            // TODO: let third party order this list
            return arrayOf(Gravatar, Mastodon, Tumblr, WordPress)
        }
    }
}

@Composable
fun SocialIcon(icon: SocialIcon, modifier: Modifier = Modifier) {
    Icon(
        imageVector = ImageVector.vectorResource(icon.imageResource),
        contentDescription = icon.name,
        modifier = modifier,
    )
}

@Composable
fun SocialIconList(icons: Array<SocialIcon>, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        for (icon in icons) {
            SocialIcon(icon = icon, Modifier.size(24.dp))
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}
