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

private const val Index3 = 3
private const val Index2 = 2
private const val Index1 = 1
private const val Index0 = 0
public fun Color.toUiColor(): UIColor {
    val colorSpace = CGColorSpaceCreateDeviceRGB()
    val components = nativeHeap.allocArray<DoubleVar>(length = 4)
    components[Index0] = red.toDouble()
    components[Index1] = green.toDouble()
    components[Index2] = blue.toDouble()
    components[Index3] = alpha.toDouble()
    val cgColor = CGColorCreate(colorSpace, components)
    return UIColor.colorWithCGColor(cgColor)
}
