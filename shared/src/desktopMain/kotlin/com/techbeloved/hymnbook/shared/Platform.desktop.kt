package com.techbeloved.hymnbook.shared

class DesktopPlatform : Platform {
    override val name: String
        get() = "Desktop Platform"

}

actual fun getPlatform(): Platform  = DesktopPlatform()