package com.fitsuli.glyphextender.notification

import android.app.Notification
import android.service.notification.StatusBarNotification
import com.fitsuli.glyphextender.extensions.animateProgress
import com.fitsuli.glyphextender.extensions.animateProgressBackToStop
import com.fitsuli.glyphextender.glyph.GlyphSessionManager
import com.nothing.ketchum.GlyphManager

class ProgressNotificationProcessor(
    private val sessionManager: GlyphSessionManager,
    private val glyphManager: GlyphManager,
) : NotificationProcessor {

    override val priority = 0 // lowest priority

    private var currentProgress = 0
    private var currentPackageName: String? = null
    private val glyphFrameC = glyphManager.glyphFrameBuilder.buildChannelC().build()

    override suspend fun canProcess(sbn: StatusBarNotification): Boolean {
        val extras = sbn.notification.extras
        val progress = extras.getInt(Notification.EXTRA_PROGRESS)
        val maxProgress = extras.getInt(Notification.EXTRA_PROGRESS_MAX)
        return progress != 0 && maxProgress != 0
    }

    override suspend fun process(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val progress = extras.getInt(Notification.EXTRA_PROGRESS)
        val maxProgress = extras.getInt(Notification.EXTRA_PROGRESS_MAX)

        currentPackageName = sbn.packageName
        sessionManager.connectAndAwait()

        val glyphProgress = (progress * 100 / maxProgress).coerceIn(0, 100)
        currentProgress = glyphProgress
        glyphManager.animateProgress(frame = glyphFrameC, to = glyphProgress)
    }

    override suspend fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (sbn.packageName == currentPackageName) {
            sessionManager.connectAndAwait()
            glyphManager.animateProgressBackToStop(glyphFrameC, from = currentProgress)
            currentProgress = 0
            currentPackageName = null
            sessionManager.disconnect()
        }
    }
}
