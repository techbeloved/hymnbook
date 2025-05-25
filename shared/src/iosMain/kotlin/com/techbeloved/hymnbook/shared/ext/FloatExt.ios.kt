package com.techbeloved.hymnbook.shared.ext

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle

public actual fun Float.decimalPlaces(decimalCount: Int): String {
    // Format the float with the specified number of decimal places
    val numberFormatter = NSNumberFormatter()
    numberFormatter.numberStyle = NSNumberFormatterDecimalStyle
    numberFormatter.minimumFractionDigits = decimalCount.toULong()
    numberFormatter.maximumFractionDigits = decimalCount.toULong()

    // Convert the float to NSNumber
    val number = NSNumber(this.toDouble())
    return numberFormatter.stringFromNumber(number) ?: ""
}
