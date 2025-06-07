package com.techbeloved.hymnbook.shared.ui.utils


import androidx.compose.ui.graphics.Color
import kotlin.random.Random

internal fun generateRandomPastelColor(seed: String? = null): Color {
    val random = if (seed != null) Random(seed.hashCode()) else Random.Default
    return Color(
        red = (random.nextInt(until = 128) + 127) / 255f,   // Bias towards lighter tones
        green = (random.nextInt(until = 128) + 127) / 255f, // Bias towards lighter tones
        blue = (random.nextInt(until = 128) + 127) / 255f,  // Bias towards lighter tones
    )
}
