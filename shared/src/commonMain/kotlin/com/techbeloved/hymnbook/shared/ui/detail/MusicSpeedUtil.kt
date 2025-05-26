package com.techbeloved.hymnbook.shared.ui.detail

private const val PlaybackSpeedStep = 5
private const val MinSpeed = 50
private const val MaxSpeed = 200

internal fun changeMusicSpeed(currentSpeed: Int, isIncrease: Boolean): Int {
    val change = if (isIncrease) {
        PlaybackSpeedStep
    } else {
        -PlaybackSpeedStep
    }
    return (currentSpeed + change)
        .coerceIn(MinSpeed, MaxSpeed)
}
