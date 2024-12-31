package com.techbeloved.media

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform