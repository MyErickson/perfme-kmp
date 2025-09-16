package com.perfme.android

import android.app.Application

/**
 * Application class for PerfMe Android app
 */
class PerfMeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: PerfMeApplication
            private set
    }
}
