package com.fitsuli.glyphextender.notification

import android.app.Notification
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.service.notification.StatusBarNotification
import com.fitsuli.glyphextender.extensions.animateProgress
import com.fitsuli.glyphextender.extensions.animateProgressBackToStop
import com.fitsuli.glyphextender.extensions.showPauseBlink
import com.fitsuli.glyphextender.glyph.GlyphSessionManager
import com.fitsuli.glyphextender.utils.getMediaSessionToken
import com.nothing.ketchum.GlyphManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

private const val MIN_DURATION_PROGRESS_MS = 5000

class MediaNotificationProcessor(
    private val sessionManager: GlyphSessionManager,
    private val glyphManager: GlyphManager,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val context: Context,
) : NotificationProcessor {
    private var currentMediaToken: MediaSession.Token? = null
    private var currentMediaController: MediaController? = null
    private var currentPackageName: String? = null
    private var currentMediaDurationMs: Long = 0
    private var lastMediaPlaybackState: Int = PlaybackState.STATE_NONE
    private var currentProgress: Int = 0
    private var progressJob: Job? = null

    private val glyphFrameC = glyphManager.glyphFrameBuilder.buildChannelC().build()

    override suspend fun canProcess(sbn: StatusBarNotification): Boolean =
        sbn.notification.extras.containsKey(Notification.EXTRA_MEDIA_SESSION)

    override suspend fun process(sbn: StatusBarNotification) {
        sessionManager.connectAndAwait()
        val token = sbn.getMediaSessionToken() ?: return
        updateMediaSession(sbn.packageName, token)
    }

    override suspend fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (sbn.packageName == currentPackageName) {
            sessionManager.connectAndAwait()
            progressJob?.cancelAndJoin()
            cleanupMediaSession()
            glyphManager.animateProgressBackToStop(glyphFrameC, from = currentProgress)
        }
    }

    private suspend fun updateMediaSession(packageName: String, token: MediaSession.Token) {
        Timber.d("Updating media session with token: $token")
        progressJob?.cancelAndJoin()
        currentPackageName = packageName
        currentMediaToken = token
        currentMediaController = MediaController(context, token)
        progressJob = startMediaNotificationProgress()
    }

    private fun startMediaNotificationProgress(): Job? {
        val metadata = currentMediaController?.metadata ?: return null
        if (!metadata.containsKey(MediaMetadata.METADATA_KEY_DURATION)) return null

        val duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION).also {
            currentMediaDurationMs = it
        }

        return scope.launch {
            Timber.d("Media playback job started")
            if (duration <= MIN_DURATION_PROGRESS_MS) {
                Timber.d("Media playback duration too short: $duration")
                return@launch
            }

            val tick = (duration / 100).coerceIn(300L..12000L)
            Timber.d("Media playback duration: $duration, tick: $tick")

            while (isActive) {
                val (shouldContinue, progress) = checkAndShowProgressWithGlyph()
                currentProgress = progress
                if (!shouldContinue) break
                delay(tick)
            }

            Timber.d(
                "Media playback progress finishing, last progress: $currentProgress\n" +
                        "Cleaning up media session and waiting for a few seconds to disconnect"
            )
            cleanupMediaSession()
            delay(5000)
            if (isActive) {
                glyphManager.animateProgressBackToStop(glyphFrameC, from = currentProgress)
            }
            Timber.d("Media playback job finished, glyph session closed")
        }
    }

    private suspend fun checkAndShowProgressWithGlyph(): Pair<Boolean, Int> {
        val duration = currentMediaDurationMs
        val mediaController = currentMediaController
        val playbackState = mediaController?.playbackState ?: return false to 0

        val progress = (playbackState.position * 100 / duration.coerceAtLeast(1L))
            .toInt()
            .coerceIn(0, 100)

        Timber.d("Media playback progress: $progress, state: ${playbackState.state}")

        return when (playbackState.state) {
            PlaybackState.STATE_PLAYING -> {
                glyphManager.animateProgress(frame = glyphFrameC, to = progress)
                true to progress
            }

            lastMediaPlaybackState -> {
                Timber.d("Media playback state is the same as last: ${playbackState.state}")
                false to progress
            }

            PlaybackState.STATE_PAUSED -> {
                glyphManager.showPauseBlink()
                delay(1500)
                glyphManager.animateProgress(frame = glyphFrameC, to = 0, slower = true)
                false to 0
            }

            else -> false to progress
        }.also { lastMediaPlaybackState = playbackState.state }
    }

    private fun cleanupMediaSession() {
        Timber.d("Cleaning up media session!")
        currentMediaToken = null
        currentMediaController = null
        currentPackageName = null
        currentMediaDurationMs = 0
        lastMediaPlaybackState = PlaybackState.STATE_NONE
    }
}
