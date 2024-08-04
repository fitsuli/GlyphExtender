package com.fitsuli.glyphextender.utils

import android.app.Notification
import android.media.session.MediaSession
import android.service.notification.StatusBarNotification

/**
 * Created by Dmitry Danilyuk at 04.08.2024
 */
fun StatusBarNotification.getMediaSessionToken(): MediaSession.Token? {
    return notification.extras.getParcelable(
        Notification.EXTRA_MEDIA_SESSION,
        MediaSession.Token::class.java
    )
}
