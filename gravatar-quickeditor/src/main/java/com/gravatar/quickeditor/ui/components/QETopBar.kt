package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.ui.GravatarTheme

@Composable
internal fun QETopBar(onDoneClick: () -> Unit, gravatarIconUrl: String, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    GravatarCenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                text = stringResource(id = R.string.gravatar),
            )
        },
        navigationIcon = {
            TextButton(
                onClick = onDoneClick,
            ) {
                Text(
                    style = MaterialTheme.typography.labelLarge,
                    text = stringResource(R.string.bottom_sheet_done),
                )
            }
        },
        actions = {
            Icon(
                painter = painterResource(id = com.gravatar.ui.R.drawable.gravatar_icon),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(id = R.string.gravatar),
                modifier = Modifier
                    .clickable(onClick = {
                        uriHandler.openUri(gravatarIconUrl)
                    })
                    .size(34.dp)
                    .padding(end = 8.dp),
            )
        },
    )
}

private val AppBarHeight = 64.dp

/*
 *  We can replace this Composable with CenterAlignedTopAppBar from the Material3 library
 *  when it removes the experimental annotation
 */
@Composable
private fun GravatarCenterAlignedTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(AppBarHeight),
    ) {
        Box {
            if (navigationIcon != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp),
                ) {
                    navigationIcon()
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Center),
            ) {
                title()
            }

            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp),
            ) {
                actions()
            }
        }
    }
}

@Preview
@Composable
private fun QETopBarPreview() {
    GravatarTheme {
        QETopBar(onDoneClick = {}, "")
    }
}
