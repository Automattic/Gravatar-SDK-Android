package com.gravatar.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun Modifier.skeletonEffect(): Modifier {
    val animationDuration = integerResource(id = R.integer.gravatar_skeleton_animation_duration)
    val infiniteTransition = rememberInfiniteTransition(label = "skeletonTransition")
    val backgroundColor = infiniteTransition.animateColor(
        // ⚠️ We should add the colors to the theme and use them here.
        if (isSystemInDarkTheme()) Color(0xFF6B6B6B) else Color(0xFFE5E7E9),
        if (isSystemInDarkTheme()) Color(0xFF979797) else Color(0xFFF2F2F2),
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = animationDuration
            },
            repeatMode = RepeatMode.Reverse,
        ),
        label = "backgroundColorAnimation",
    ).value

    return this.background(backgroundColor)
}

@Composable
internal fun TextSkeletonEffect(
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    text: String = "",
    skeletonVerticalPadding: Dp = 2.dp,
    skeletonShape: Shape = RoundedCornerShape(10.dp),
) {
    val textMeasurer = rememberTextMeasurer()
    val measure = textMeasurer.measure(
        text,
        textStyle,
    )
    val height = with(LocalDensity.current) { measure.size.height.toDp() }
    Box(
        modifier
            .height(height),
    ) {
        Box(
            modifier = Modifier
                .padding(skeletonVerticalPadding)
                .fillMaxSize()
                .clip(skeletonShape)
                .skeletonEffect(),
        )
    }
}
