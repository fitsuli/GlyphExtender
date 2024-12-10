package com.fitsuli.glyphextender.notification

import android.content.Context
import android.service.notification.StatusBarNotification
import com.fitsuli.glyphextender.glyph.GlyphOrchestrator
import com.fitsuli.glyphextender.glyph.GlyphRequest
import com.fitsuli.glyphextender.glyph.GlyphSegment
import com.fitsuli.glyphextender.utils.observeBatteryStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

private const val BATTERY_PROGRESS_TAG = "battery_progress"
private const val BATTERY_DOT_TAG = "battery_dot"

class BatteryNotificationProcessor(
    private val context: Context,
    private val orchestrator: GlyphOrchestrator,
    private val scope: CoroutineScope,
) : NotificationProcessor {

    private var isCharging = false
    private var currentLevel = 0
    private var batteryJob: Job? = null

    override val priority = 0
    override val isActive get() = isCharging

    init {
        startBatteryMonitoring()
    }

    private fun startBatteryMonitoring() {
        batteryJob = context.observeBatteryStatus()
            .distinctUntilChanged()
            .onEach { status ->
                isCharging = status.isCharging
                currentLevel = status.level
                updateBatteryDisplay()
            }
            .launchIn(scope)
    }

    private suspend fun updateBatteryDisplay() {
        Timber.d("Battery status: charging = $isCharging, level = $currentLevel")
        if (isCharging) {
            orchestrator.display(
                GlyphRequest.Static(
                    id = BATTERY_DOT_TAG,
                    priority = priority,
                    segment = GlyphSegment.BatteryDot
                )
            )
            orchestrator.display(
                GlyphRequest.Progress(
                    id = BATTERY_PROGRESS_TAG,
                    priority = priority,
                    segment = GlyphSegment.Battery,
                    progress = currentLevel
                )
            )
        } else {
            orchestrator.remove(BATTERY_PROGRESS_TAG)
            orchestrator.remove(BATTERY_DOT_TAG)
        }
    }

    override suspend fun canProcess(sbn: StatusBarNotification) = false
    override suspend fun process(sbn: StatusBarNotification) = Unit
    override suspend fun onNotificationRemoved(sbn: StatusBarNotification) = Unit

    override suspend fun onScreenStateChanged(isScreenOn: Boolean) {
        // TODO: no way around?
        if (!isScreenOn && isCharging) {
            updateBatteryDisplay()
        }
    }

    override suspend fun dispose() {
        batteryJob?.cancel()
        batteryJob = null
        orchestrator.remove(BATTERY_PROGRESS_TAG)
    }
}
