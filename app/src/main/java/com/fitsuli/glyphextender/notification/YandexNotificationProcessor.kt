package com.fitsuli.glyphextender.notification

import android.content.Context
import android.service.notification.StatusBarNotification
import com.fitsuli.glyphextender.extensions.animateProgress
import com.fitsuli.glyphextender.extensions.animateProgressBackToStop
import com.fitsuli.glyphextender.glyph.GlyphSessionManager
import com.fitsuli.glyphextender.utils.getTitleAny
import com.nothing.ketchum.GlyphManager
import timber.log.Timber
import java.io.File

class YandexNotificationProcessor(
    private val sessionManager: GlyphSessionManager,
    private val glyphManager: GlyphManager,
    private val context: Context,
) : NotificationProcessor {
    private var currentProgress = 0
    private var taxiActivityMaxProgress: Int? = null
    private val glyphFrameC = glyphManager.glyphFrameBuilder.buildChannelC().build()
    private val allowedChannelIds = setOf("order", "order_low")

    override val priority = 2 // Higher than media but lower than critical notifications
    override val isActive get() = currentProgress > 0

    override suspend fun canProcess(sbn: StatusBarNotification): Boolean =
        sbn.packageName == "ru.yandex.taxi" && sbn.notification.channelId in allowedChannelIds

    override suspend fun process(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val file = File(context.getExternalFilesDir(null), "notifications.txt").apply {
            parentFile?.mkdirs()
        }

        Timber.d(
            "onNotificationPosted: ${sbn.packageName}\n${sbn.notification}\n" +
                    extras.keySet().joinToString("\n") { key -> "$key: ${extras[key]}" }
                        .also { file.appendText("$it\n\n") }
        )

        if (!extras.containsKey("android.contains.customView")) {
            return
        }

        Timber.d(
            "Custom view found in Yandex notification! It's likely a progress bar, so attempt to parse the text\n"
                .also { file.appendText(it) }
        )

        val notificationText = extras.getTitleAny() ?: return
        val minutesLeft = notificationText.filter { it.isDigit() }.toIntOrNull()

        if (minutesLeft == null) {
            sessionManager.connectAndAwait()
            glyphManager.animateProgressBackToStop(glyphFrameC, from = currentProgress)
            currentProgress = 0
            taxiActivityMaxProgress = null
            return
        }

        if (taxiActivityMaxProgress == null && minutesLeft > 0) {
            taxiActivityMaxProgress = minutesLeft
        }

        val progress =
            taxiActivityMaxProgress?.let { (minutesLeft * 100 / it).coerceIn(0, 100) } ?: return
        currentProgress = 100 - progress

        Timber.d(
            "Taxi activity progress: $progress, real progress: $currentProgress"
                .also { file.appendText(it) }
        )

        sessionManager.connectAndAwait()
        glyphManager.animateProgress(frame = glyphFrameC, to = currentProgress)
    }

    override suspend fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (canProcess(sbn)) {
            sessionManager.connectAndAwait()
            glyphManager.animateProgressBackToStop(glyphFrameC, from = currentProgress)
            currentProgress = 0
            taxiActivityMaxProgress = null
            sessionManager.disconnect()
        }
    }
}
