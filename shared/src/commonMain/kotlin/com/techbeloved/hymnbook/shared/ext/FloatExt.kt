package com.techbeloved.hymnbook.shared.ext

private const val NearestFiveMultiplier = 5
public val Int.percentToNearestFive: String
    get() {
        val roundedToNearestFive = (this / NearestFiveMultiplier) * NearestFiveMultiplier
        return (roundedToNearestFive / 100f).decimalPlaces(decimalCount = 2)
    }

public expect fun Float.decimalPlaces(decimalCount: Int): String
