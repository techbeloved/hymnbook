package com.techbeloved.hymnbook.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform