package com.gravatar.ui.components.atomic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
internal fun EpandableText(text: String, modifier: Modifier = Modifier, maxLines: Int = 2) {
    var showDialog by remember { mutableStateOf(false) }
    var clickableText by remember { mutableStateOf(false) }

    Text(
        text = text,
        modifier = modifier.clickable(enabled = clickableText) {
            showDialog = true
        },
        maxLines = maxLines,
        onTextLayout = { textLayoutResult ->
            clickableText = textLayoutResult.hasVisualOverflow
        },
        overflow = TextOverflow.Ellipsis,
    )
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                modifier = Modifier.wrapContentSize(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = text,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentSize(),
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}
