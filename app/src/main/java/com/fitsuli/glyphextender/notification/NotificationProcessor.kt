package com.fitsuli.glyphextender.notification

import android.service.notification.StatusBarNotification

interface NotificationProcessor {
    suspend fun canProcess(sbn: StatusBarNotification): Boolean
    suspend fun process(sbn: StatusBarNotification)
    suspend fun onNotificationRemoved(sbn: StatusBarNotification)

    /**
     * Priority determines which processor takes precedence when multiple can handle the notification
     * Higher number = higher priority
     */
    val priority: Int get() = 0

    /**
     * Whether this processor is currently active and handling notifications
     */
    val isActive: Boolean get() = false

    suspend fun onScreenStateChanged(isScreenOn: Boolean) {
        // Optional callback for screen state changes
    }
}

