package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gravatar.quickeditor.R

private val lightBorderColor = Color(0xFFF0B849)
private val lightBackgroundColor = Color(0xFFFEF8EE)
private val lightContentColor = Color.Black

private val darkBorderColor = Color(0xFFE1B77B)
private val darkBackgroundColor = Color(0xFF491F00)
private val darkContentColor = Color(0xFFE1B77B)

@Composable
private fun borderColor() = if (isSystemInDarkTheme()) darkBorderColor else lightBorderColor

@Composable
private fun backgroundColor() = if (isSystemInDarkTheme()) darkBackgroundColor else lightBackgroundColor

@Composable
private fun contentColor() = if (isSystemInDarkTheme()) darkContentColor else lightContentColor

@Composable
internal fun AlertBanner(message: String, onClose: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .leftBorder(borderColor(), 4.dp)
            .background(backgroundColor()),
    ) {
        Text(
            text = message,
            color = contentColor(),
            fontSize = 13.sp,
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
        )
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = stringResource(R.string.gravatar_qe_alert_banner_close_content_description),
            tint = contentColor(),
            modifier = Modifier
                .padding(top = 12.dp, end = 12.dp)
                .clickable(onClick = onClose)
                .size(24.dp)
                .padding(4.dp),
        )
    }
}

private fun Modifier.leftBorder(color: Color, width: Dp) = this.drawWithContent {
    val widthPx = width.toPx()
    drawContent()
    drawLine(
        color = color,
        start = Offset(widthPx / 2, 0f),
        end = Offset(widthPx / 2, size.height),
        strokeWidth = widthPx,
    )
}

@Preview
@Composable
private fun AlertBannerPreview() {
    AlertBanner(message = stringResource(id = R.string.gravatar_qe_alert_banner_no_avatar_selected), onClose = {})
}
