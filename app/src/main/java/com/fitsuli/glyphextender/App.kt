package com.fitsuli.glyphextender

import android.app.Application
import com.fitsuli.glyphextender.di.AppModule
import com.fitsuli.glyphextender.di.AppModuleImpl
import timber.log.Timber

/**
 * Created by Dmitry Danilyuk at 17.07.2024
 */
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(appContext = this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        lateinit var appModule: AppModule
    }
}
