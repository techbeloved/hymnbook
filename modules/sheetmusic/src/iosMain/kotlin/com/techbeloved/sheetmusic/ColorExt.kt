@file:OptIn(ExperimentalForeignApi::class)

package com.techbeloved.sheetmusic

import androidx.compose.ui.graphics.Color
import kotlinx.cinterop.DoubleVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.set
import platform.CoreGraphics.CGColorCreate
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.UIKit.UIColor

public fun Color.toUiColor(): UIColor {
    val colorSpace = CGColorSpaceCreateDeviceRGB()
    val components = nativeHeap.allocArray<DoubleVar>(4)
    components[0] = red.toDouble()
    components[1] = green.toDouble()
    components[2] = blue.toDouble()
    components[3] = alpha.toDouble()
    val cgColor = CGColorCreate(colorSpace, components)
    return UIColor.colorWithCGColor(cgColor)
}
