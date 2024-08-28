package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R
import com.gravatar.ui.GravatarTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun QETopBar(onDoneClick: () -> Unit, modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        windowInsets = WindowInsets(0, 0, 0, 0),
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
                    .size(34.dp)
                    .padding(end = 8.dp),
            )
        },
    )
}

@Preview
@Composable
private fun QETopBarPreview() {
    GravatarTheme {
        QETopBar(onDoneClick = {})
    }
}
