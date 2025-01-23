package com.techbeloved.hymnbook

class JvmPlatform {
    val name: String = "Java ${System.getProperty("java.version")}"
}

fun getPlatform() = JvmPlatform()
