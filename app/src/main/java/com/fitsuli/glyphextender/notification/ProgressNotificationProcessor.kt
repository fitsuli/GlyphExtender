package com.fitsuli.glyphextender.notification

import android.app.Notification
import android.service.notification.StatusBarNotification
import com.fitsuli.glyphextender.glyph.GlyphOrchestrator
import com.fitsuli.glyphextender.glyph.GlyphRequest
import com.fitsuli.glyphextender.glyph.GlyphSessionManager

private const val GENERIC_PROGRESS_TAG = "generic_progress"

class ProgressNotificationProcessor(
    private val sessionManager: GlyphSessionManager,
    private val orchestrator: GlyphOrchestrator,
) : NotificationProcessor {

    override val priority = 0 // lowest priority

    private var currentProgress = 0
    private var currentPackageName: String? = null

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

        orchestrator.display(
            GlyphRequest.Progress(
                id = GENERIC_PROGRESS_TAG,
                priority = priority,
                progress = glyphProgress
            )
        )
    }

    override suspend fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (sbn.packageName == currentPackageName) {
            sessionManager.connectAndAwait()
            orchestrator.remove(GENERIC_PROGRESS_TAG)
            currentProgress = 0
            currentPackageName = null
            sessionManager.disconnect()
        }
    }
}
