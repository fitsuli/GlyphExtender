package com.fitsuli.glyphextender.utils.prefs

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.map

/**
 * Created by Dmitry Danilyuk at 12.08.2024
 */
class NotificationPreferencesImpl(
    private val dataStore: DataStore<NotificationPreferences>,
) {
    fun getNotificationsFlow() = dataStore.data.map { it.notificationSet }
    suspend fun setNotifications(notifications: Set<NotificationItem>) {
        dataStore.updateData { pref ->
            pref.copy(notificationSet = notifications)
        }
    }

    suspend fun addNotification(notification: NotificationItem) {
        dataStore.updateData { pref ->
            pref.copy(notificationSet = pref.notificationSet + notification)
        }
    }
}
