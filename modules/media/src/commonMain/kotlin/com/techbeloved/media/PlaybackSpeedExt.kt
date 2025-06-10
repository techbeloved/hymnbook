package com.techbeloved.media

val Int.ratePercentToFloat: Float
    get() = (this / 100f).let { if (it <= 0.05f) 1f else it }
val Float.rateToPercent: Int
    get() = (this * 100).toInt()
        // Workaround a situation where the player controller returns a speed of 0
        .let { if (it <= 0) 100 else it }
