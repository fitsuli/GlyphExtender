package com.fitsuli.glyphextender.di

import com.fitsuli.glyphextender.utils.prefs.NotificationPreferencesImpl

/**
 * Created by Dmitry Danilyuk at 12.08.2024
 */
interface AppModule {
    val notificationPrefsRepo: NotificationPreferencesImpl
}
