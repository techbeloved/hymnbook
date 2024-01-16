package com.techbeloved.hymnbook.shared

public class DesktopPlatform : Platform {
    override val name: String
        get() = "Desktop Platform"

}

public actual fun getPlatform(): Platform  = DesktopPlatform()