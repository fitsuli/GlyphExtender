package com.fitsuli.glyphextender.glyph

import com.nothing.ketchum.GlyphManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

class GlyphOrchestrator(
    private val glyphManager: GlyphManager = GlyphManager.getInstance(),
    private val sessionManager: GlyphSessionManager,
) {
    private val activeRequests = MutableStateFlow<Map<String, GlyphRequest>>(emptyMap())
    private var isScreenOn = true

    suspend fun display(request: GlyphRequest, forceStart: Boolean = false) {
        sessionManager.connectAndAwait()
        when (request) {
            is GlyphRequest.Progress -> {
                // TODO: should be a cleaner way to handle force start from zero
                val currentProgress = if (forceStart) {
                    0
                } else {
                    (activeRequests.value[request.id] as? GlyphRequest.Progress)?.progress ?: 0
                }
                if (currentProgress != request.progress) {
                    Timber.d("Orchestrator: updating progress $currentProgress -> ${request.progress}")
                    animateProgress(currentProgress, request)
                }
            }

            is GlyphRequest.Static -> {
                activeRequests.update { current ->
                    current + (request.id to request)
                }
                updateFrame()
            }
        }
    }

    private suspend fun animateProgress(from: Int, request: GlyphRequest.Progress) {
        val distance = request.progress - from

        // Skip animation for small changes
        val absDistance = abs(distance)
        if (absDistance <= 3) {
            activeRequests.update { current ->
                current + (request.id to request)
            }
            updateFrame()
            return
        }

        val isIncreasing = distance > 0
        // Reduced base duration and max duration cap
        val duration = (100 + (absDistance * 1.5f)).coerceAtMost(250f)
        val steps = 15 // Slightly fewer steps for faster updates

        val stepDelay = duration / steps

        // Generate smooth acceleration curve
        for (i in 0..steps) {
            // Smooth easing using sine curve (accelerate-decelerate)
            val progress = (sin(PI * (i.toFloat() / steps - 0.5)) + 1) / 2

            val currentValue = if (isIncreasing) {
                from + (distance * progress).toInt()
            } else {
                from - (absDistance * progress).toInt()
            }.coerceIn(0, 100) // Ensure progress stays within bounds

            activeRequests.update { current ->
                current + (request.id to request.copy(progress = currentValue))
            }
            updateFrame()
            delay(stepDelay.toLong())
        }

        // Ensure final value is set exactly
        activeRequests.update { current ->
            current + (request.id to request)
        }
        updateFrame()
    }

    suspend fun remove(requestId: String) {
        sessionManager.connectAndAwait()
        val request = activeRequests.value[requestId]
        if (request is GlyphRequest.Progress) {
            // Animate progress back to 0 before removing
            display(request.copy(progress = 0))
        }
        activeRequests.update { current ->
            current - requestId
        }
        updateFrame()
        sessionManager.disconnect()
    }

    suspend fun clear() {
        sessionManager.connectAndAwait()
        Timber.d("Orchestrator: plain clearing all requests")
        activeRequests.update { emptyMap() }
        glyphManager.turnOff()
        sessionManager.disconnect()
    }

    suspend fun clearAnimations() {
        sessionManager.connectAndAwait()
        Timber.d("Orchestrator: hiding animations gracefully")

        activeRequests.value.forEach { (_, request) ->
            // Animate zeroed progresses all back to 0
            if (request is GlyphRequest.Progress) {
                Timber.d("Orchestrator: hiding progress ${request.id} ${request.progress} (actually passing 0)")
                display(request.copy(progress = 0))
            }
        }

        glyphManager.turnOff()
        sessionManager.disconnect()
    }

    suspend fun restoreAnimations() {
        sessionManager.connectAndAwait()
        val requests = activeRequests.value
        Timber.d("Orchestrator: restoring ${requests.size} animations")

        requests.values.forEach { request ->
            when (request) {
                is GlyphRequest.Static -> updateFrame()
                is GlyphRequest.Progress -> {
                    display(
                        request,
                        forceStart = true
                    ) // Re-trigger animation with original progress
                }
            }
        }
    }

    private suspend fun updateFrame() {
        if (isScreenOn) {
            Timber.v("Orchestrator: screen is on, skipping frame update")
            return
        }

        val requests = activeRequests.value
        Timber.v("Orchestrator: updating frame with ${requests.size} requests")
        if (requests.isEmpty()) {
            // glyphManager.turnOff()
            return
        }

        val frame = glyphManager.glyphFrameBuilder.apply {
            requests.values
                .sortedByDescending { it.priority }
                .forEach { request ->
                    val segments = request.segment.getSegments()
                    if (segments.isEmpty()) return@forEach

                    when (request) {
                        is GlyphRequest.Static -> {
                            segments.forEach { segment ->
                                buildChannel(segment, request.brightness)
                            }
                        }

                        is GlyphRequest.Progress -> {
                            // Calculate how many segments should be fully lit
                            val progressSegments = (request.progress / 100.0) * segments.size

                            // Calculate partial brightness for the transition segment
                            val remainingProgress = progressSegments % 1
                            val partialBrightness = (remainingProgress * 4095).toInt()

                            val fullSegments = progressSegments.toInt()

                            segments.forEachIndexed { index, segment ->
                                when {
                                    // Fully light up segments before the progress point
                                    index < fullSegments -> buildChannel(segment, 4095)
                                    // Partially light up the transition segment
                                    index == fullSegments -> buildChannel(
                                        segment,
                                        partialBrightness
                                    )
                                    // Leave remaining segments off
                                    else -> Unit
                                }
                            }
                        }
                    }
                }
        }.build()

        glyphManager.toggle(frame)
    }

    suspend fun notifyScreenStateChanged(isScreenOn: Boolean) {
        if (isScreenOn) {
            Timber.d("Session: screen turned on, clearing animations and disconnecting")
            clearAnimations()
            this.isScreenOn = true
        } else {
            Timber.d("Session: screen turned off, reconnecting and restoring animations")
            this.isScreenOn = false
            restoreAnimations()
        }
    }
}
