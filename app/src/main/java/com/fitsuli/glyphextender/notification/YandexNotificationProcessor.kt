package com.fitsuli.glyphextender.notification

import android.service.notification.StatusBarNotification
import com.fitsuli.glyphextender.glyph.GlyphOrchestrator
import com.fitsuli.glyphextender.glyph.GlyphRequest
import com.fitsuli.glyphextender.utils.getTitleAny
import timber.log.Timber

private const val TAXI_PROGRESS_TAG = "taxi_progress"

class YandexNotificationProcessor(
    private val orchestrator: GlyphOrchestrator,
) : NotificationProcessor {
    private var currentProgress = 0
    private var taxiActivityMaxProgress: Int? = null

    private val allowedChannelIds = setOf("order", "order_low")

    override val priority = 2 // Higher than media but lower than critical notifications
    override val isActive get() = currentProgress > 0

    override suspend fun canProcess(sbn: StatusBarNotification): Boolean =
        sbn.packageName == "ru.yandex.taxi" && sbn.notification.channelId in allowedChannelIds

    override suspend fun process(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        Timber.d(
            "onNotificationPosted: ${sbn.packageName}\n${sbn.notification}\n" +
                    extras.keySet().joinToString("\n") { key -> "$key: ${extras[key]}" }
        )

        if (!extras.containsKey("android.contains.customView")) {
            return
        }

        Timber.d(
            "Custom view found in Yandex notification! It's likely a progress bar, so attempt to parse the text\n"
        )

        val notificationText = extras.getTitleAny() ?: return
        val minutesLeft = notificationText.filter { it.isDigit() }.toIntOrNull()

        if (minutesLeft == null) {
            orchestrator.remove(TAXI_PROGRESS_TAG)

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
        )

        orchestrator.display(
            GlyphRequest.Progress(
                id = TAXI_PROGRESS_TAG,
                priority = priority,
                progress = currentProgress
            )
        )
    }

    override suspend fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (canProcess(sbn)) {
            orchestrator.remove(TAXI_PROGRESS_TAG)
        }
    }
}
