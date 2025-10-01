package com.techbeloved.hymnbook.shared.swiftinterop

import com.techbeloved.hymnbook.shared.SwiftInterop
import com.techbeloved.hymnbook.shared.di.IosInjector

internal fun swiftInteropProvider(): SwiftInterop = IosInjector.swiftInterop
