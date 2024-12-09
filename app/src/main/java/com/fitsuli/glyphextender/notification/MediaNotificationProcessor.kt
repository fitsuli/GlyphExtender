package com.fitsuli.glyphextender.notification

import android.app.Notification
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.service.notification.StatusBarNotification
import com.fitsuli.glyphextender.glyph.GlyphOrchestrator
import com.fitsuli.glyphextender.glyph.GlyphRequest
import com.fitsuli.glyphextender.glyph.GlyphSegment
import com.fitsuli.glyphextender.utils.getMediaSessionToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

private const val MIN_DURATION_PROGRESS_MS = 5000

private const val MEDIA_PROGRESS_TAG = "media_progress"
private const val MEDIA_PAUSE_TAG = "media_pause"

class MediaNotificationProcessor(
    private val orchestrator: GlyphOrchestrator,
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

    override val priority = 1 // Lower priority than taxi notifications, but higher than progress
    override val isActive get() = progressJob != null && currentProgress > 0

    override suspend fun canProcess(sbn: StatusBarNotification): Boolean =
        sbn.notification.extras.containsKey(Notification.EXTRA_MEDIA_SESSION) &&
                !hasHigherPriorityProcessor()

    private fun hasHigherPriorityProcessor() =
        NotificationProcessorRegistry.hasActiveHigherPriorityProcessor(this)

    override suspend fun process(sbn: StatusBarNotification) {
        val token = sbn.getMediaSessionToken() ?: return
        updateMediaSession(sbn.packageName, token)
    }

    override suspend fun onNotificationRemoved(sbn: StatusBarNotification) {
        if (sbn.packageName == currentPackageName) {
            progressJob?.cancelAndJoin()
            orchestrator.remove(MEDIA_PROGRESS_TAG)
            cleanupMediaSession()
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
                orchestrator.remove(MEDIA_PROGRESS_TAG)
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
                orchestrator.display(
                    GlyphRequest.Progress(
                        id = MEDIA_PROGRESS_TAG,
                        priority = priority,
                        progress = progress
                    )
                )
                true to progress
            }
            PlaybackState.STATE_PAUSED -> {
                orchestrator.display(
                    GlyphRequest.Static(
                        id = MEDIA_PAUSE_TAG,
                        priority = priority,
                        segment = GlyphSegment.Pause,
                    )
                )
                delay(1500)
                orchestrator.remove(MEDIA_PAUSE_TAG)
                false to 0
            }
            PlaybackState.STATE_STOPPED,
            PlaybackState.STATE_ERROR,
            PlaybackState.STATE_NONE,
                -> {
                cleanupMediaSession()
                orchestrator.remove(MEDIA_PROGRESS_TAG)
                false to 0
            }

            lastMediaPlaybackState -> {
                Timber.d("Media playback state is the same as last: ${playbackState.state}")
                false to progress
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
