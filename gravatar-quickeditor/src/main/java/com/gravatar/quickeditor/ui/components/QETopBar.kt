package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.gravatar.GravatarConstants
import com.gravatar.quickeditor.R
import com.gravatar.ui.GravatarTheme

@Composable
internal fun QETopBar(leftButton: @Composable () -> Unit, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    GravatarCenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                text = stringResource(id = R.string.gravatar_qe_gravatar),
            )
        },
        navigationIcon = leftButton,
        actions = {
            Icon(
                painter = painterResource(id = com.gravatar.ui.R.drawable.gravatar_gravatar_icon),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(id = R.string.gravatar_qe_gravatar),
                modifier = Modifier
                    .clickable(onClick = {
                        uriHandler.openUri(GravatarConstants.GRAVATAR_SIGN_IN_URL)
                    })
                    .size(34.dp)
                    .padding(end = 8.dp),
            )
        },
    )
}

@Composable
internal fun QETopBarTextButton(
    onClick: () -> Unit,
    label: String = stringResource(R.string.gravatar_qe_bottom_sheet_done),
) {
    TextButton(
        onClick = onClick,
    ) {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = label,
        )
    }
}

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
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier.padding(vertical = 5.dp),
        ) {
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
        QETopBar(leftButton = { QETopBarTextButton({}) })
    }
}
