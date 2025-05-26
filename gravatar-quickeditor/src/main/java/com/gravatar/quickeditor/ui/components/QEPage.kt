package com.gravatar.quickeditor.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun QEPageDefault(onDoneClicked: () -> Unit, content: @Composable () -> Unit, modifier: Modifier = Modifier) {
    BackHandler {
        onDoneClicked()
    }

    QEPage(
        topBar = {
            QETopBar({
                QETopBarTextButton(
                    onClick = onDoneClicked,
                )
            })
        },
        content = content,
        modifier = modifier,
    )
}

@Composable
internal fun QEPage(topBar: @Composable () -> Unit, content: @Composable () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier) {
        topBar()
        content()
    }
}
