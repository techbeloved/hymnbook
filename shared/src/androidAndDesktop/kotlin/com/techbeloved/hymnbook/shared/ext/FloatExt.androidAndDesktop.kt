package com.techbeloved.hymnbook.shared.ext

public actual fun Float.decimalPlaces(decimalCount: Int): String = "%.2f".format(this)
