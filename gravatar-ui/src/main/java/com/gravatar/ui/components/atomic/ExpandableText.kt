package com.gravatar.ui.components.atomic

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gravatar.ui.R

@Composable
internal fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 2,
    dialogContent: @Composable (() -> Unit)? = null,
) {
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
            if (dialogContent != null) {
                dialogContent()
            } else {
                DefaultDialogContent(text)
            }
        }
    }
}

@Composable
private fun DefaultDialogContent(text: String) {
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

@Preview
@Composable
private fun ExpandableTextPreview() {
    ExpandableText("Text that \ncan be expanded \nby clicking on it.")
}

@Preview
@Composable
private fun ExpandableTextPreviewWithDialogContent() {
    ExpandableText("Text that \ncan be expanded \nby clicking on it.") {
        Card(
            modifier = Modifier.wrapContentSize(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                Icon(
                    painter = painterResource(R.drawable.gravatar_icon),
                    contentDescription = "",
                )
                Text("This demonstrates how to pass a custom dialog content to the ExpandableText.")
            }
        }
    }
}
