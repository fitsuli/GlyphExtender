package com.fitsuli.glyphextender.notification

import android.service.notification.StatusBarNotification

interface NotificationProcessor {
    val priority: Int
    val isActive: Boolean

    suspend fun canProcess(sbn: StatusBarNotification): Boolean
    suspend fun process(sbn: StatusBarNotification)
    suspend fun onNotificationRemoved(sbn: StatusBarNotification)
    suspend fun onScreenStateChanged(isScreenOn: Boolean) {}
    suspend fun dispose() {}
}

