package com.techbeloved.hymnbook.shared.di

import android.app.Application

internal object AndroidInjector {
    private var app: Application? = null
    val application
        get() = app ?: throw IllegalStateException("Application not initialized")


    fun init(application: Application) {
        this.app = application
    }
}
