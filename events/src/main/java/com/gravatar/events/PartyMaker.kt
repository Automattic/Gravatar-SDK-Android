package com.gravatar.events

import android.graphics.Color
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

fun hashToPartyList(hash: String): List<Party> {
    val spread = 90
    val angle = Angle.RIGHT - 45
    return listOf(
        hashToParty(hash, angle, spread, Position.Relative(0.0, 0.3)),
        hashToParty(hash, angle - 90, spread, Position.Relative(1.0, 0.3)),
    )
}

fun hashToParty(hash: String, angle: Int, spread: Int, position: Position): Party {
    return Party(
        speed = 5f,
        maxSpeed = 20f,
        damping = 0.95f,
        angle = angle,
        spread = spread,
        position = position,
        // TODO: make a better color generation function
        // colors = listOf(hashToColor(hash), hashToColor(hash, 1)),
        emitter = Emitter(duration = 150, TimeUnit.MILLISECONDS).max(150),
    )
}

fun hashToColor(hash: String, shift: Int = 0): Int {
    val shifted = shift * 6
    val red = hash.substring(0 + shifted, 2 + shifted).toInt(16)
    val green = hash.substring(2 + shifted, 4 + shifted).toInt(16)
    val blue = hash.substring(4 + shifted, 6 + shifted).toInt(16)
    return Color.rgb(red, green, blue)
}
