package com.fitsuli.glyphextender.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.MultiProcessDataStoreFactory
import androidx.datastore.dataStoreFile
import com.fitsuli.glyphextender.App
import com.fitsuli.glyphextender.utils.prefs.NotificationPreferences
import com.fitsuli.glyphextender.utils.prefs.NotificationPreferencesImpl
import com.fitsuli.glyphextender.utils.prefs.NotificationPreferencesSerializer

/**
 * Created by Dmitry Danilyuk at 12.08.2024
 */
private const val DATASTORE_NAME = "prefs"

class AppModuleImpl(
    private val appContext: App,
) : AppModule {
    private val dataStore: DataStore<NotificationPreferences> = MultiProcessDataStoreFactory.create(
        serializer = NotificationPreferencesSerializer,
        produceFile = { appContext.dataStoreFile(DATASTORE_NAME) }
    )
    override val notificationPrefsRepo: NotificationPreferencesImpl by lazy {
        NotificationPreferencesImpl(dataStore)
    }
}
