package com.techbeloved.hymnbook.shared.di

import android.app.Application

public object AndroidInjector {
    private var app: Application? = null
    internal val application
        get() = checkNotNull(app) { "Application not initialized" }

    public fun init(application: Application) {
        this.app = application
    }
}
