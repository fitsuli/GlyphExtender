package com.fitsuli.glyphextender.glyph

import com.nothing.ketchum.GlyphManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

class GlyphOrchestrator(
    private val glyphManager: GlyphManager = GlyphManager.getInstance(),
) {
    private val activeRequests = MutableStateFlow<Map<String, GlyphRequest>>(emptyMap())
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val animations = mutableMapOf<String, Job>()

    suspend fun display(request: GlyphRequest) {
        when (request) {
            is GlyphRequest.Progress -> {
                animations[request.id]?.cancel()
                animations[request.id] = scope.launch {
                    val currentProgress =
                        (activeRequests.value[request.id] as? GlyphRequest.Progress)?.progress ?: 0
                    if (currentProgress != request.progress) {
                        Timber.d("Orchestrator: updating progress $currentProgress -> ${request.progress}")
                        animateProgress(currentProgress, request)
                    }
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
        val step = if (from < request.progress) 5 else -5
        val range = if (from < request.progress) {
            from..request.progress step step
        } else {
            from downTo request.progress step -step
        }

        for (progress in range) {
            activeRequests.update { current ->
                current + (request.id to request.copy(progress = progress))
            }
            updateFrame()
            delay(10)
        }

        // Ensure final value is set
        activeRequests.update { current ->
            current + (request.id to request)
        }
        updateFrame()
    }

    suspend fun remove(requestId: String) {
        val request = activeRequests.value[requestId]
        if (request is GlyphRequest.Progress) {
            // Animate progress back to 0 before removing
            display(request.copy(progress = 0))
            delay(300) // Let animation complete
        }
        activeRequests.update { current ->
            current - requestId
        }
        updateFrame()
    }

    suspend fun clear() {
        Timber.d("Orchestrator: plain clearing all requests")
        activeRequests.update { emptyMap() }
        glyphManager.turnOff()
    }

    suspend fun clearAnimations() {
        Timber.d("Orchestrator: clearing animations gracefully")
        activeRequests.value.values
            .forEach { remove(it.id) }
        animations.clear()
    }

    private suspend fun updateFrame() {
        val requests = activeRequests.value
        Timber.d("Orchestrator: updating frame with ${requests.size} requests")
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
}
