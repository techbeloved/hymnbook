package com.techbeloved.hymnbook.shared

public interface Platform {
    public val name: String
}

public expect fun getPlatform(): Platform