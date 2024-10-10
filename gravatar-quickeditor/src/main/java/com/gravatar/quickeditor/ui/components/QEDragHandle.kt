package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.core.BottomSheetScope
import com.composables.core.DragIndication

@Composable
internal fun BottomSheetScope.QEDragHandle(modifier: Modifier = Modifier) {
    DragIndication(
        modifier = modifier
            .padding(top = 10.dp)
            .background(
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                MaterialTheme.shapes.extraLarge,
            )
            .width(32.dp)
            .height(4.dp),
    )
}
