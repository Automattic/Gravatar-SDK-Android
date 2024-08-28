package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun QEDragHandle(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .padding(top = 10.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
    ) {
        Box(
            Modifier
                .size(
                    width = 32.dp,
                    height = 4.dp,
                ),
        )
    }
}
