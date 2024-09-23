package com.gravatar.quickeditor.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gravatar.quickeditor.R

@Composable
internal fun ListEmptyStateBox(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 24.dp),
            painter = painterResource(id = R.drawable.gravatar_face_image),
            contentDescription = stringResource(R.string.happy_face_image_content_description),
        )
    }
}
