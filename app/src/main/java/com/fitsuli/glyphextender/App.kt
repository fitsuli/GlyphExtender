package com.fitsuli.glyphextender

import android.app.Application
import timber.log.Timber

/**
 * Created by Dmitry Danilyuk at 17.07.2024
 */
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
