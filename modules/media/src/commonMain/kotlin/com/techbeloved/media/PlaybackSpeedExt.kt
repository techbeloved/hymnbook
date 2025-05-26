package com.techbeloved.media

val Int.ratePercentToFloat: Float get() = this / 100f
val Float.rateToPercent: Int get() = (this * 100).toInt()
