package com.fitsuli.glyphextender.notification

import android.service.notification.StatusBarNotification

interface NotificationProcessor {
    suspend fun canProcess(sbn: StatusBarNotification): Boolean
    suspend fun process(sbn: StatusBarNotification)
    suspend fun onNotificationRemoved(sbn: StatusBarNotification)
}

