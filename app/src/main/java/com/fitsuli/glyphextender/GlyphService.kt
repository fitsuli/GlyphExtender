package com.fitsuli.glyphextender

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.IBinder
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fitsuli.glyphextender.extensions.animateProgressBackToStop
import com.fitsuli.glyphextender.extensions.showPauseBlink
import com.fitsuli.glyphextender.utils.getMediaSessionToken
import com.nothing.ketchum.Common
import com.nothing.ketchum.Glyph.Code_20111
import com.nothing.ketchum.Glyph.Code_22111
import com.nothing.ketchum.Glyph.Code_23111
import com.nothing.ketchum.GlyphException
import com.nothing.ketchum.GlyphManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

private const val SERVICE_CHANNEL_ID = "glyph_extender"

class GlyphService : NotificationListenerService() {

    private val scope = CoroutineScope(Dispatchers.Default)

    private var isGlyphSessionOpen = false
    private val mGM: GlyphManager = GlyphManager.getInstance()
    private var mCallback: GlyphManager.Callback = object : GlyphManager.Callback {
        override fun onServiceConnected(componentName: ComponentName) {
            when {
                Common.is20111() -> mGM.register(applicationContext, Common.DEVICE_20111)
                Common.is22111() -> mGM.register(applicationContext, Common.DEVICE_22111)
                Common.is23111() -> mGM.register(applicationContext, Common.DEVICE_23111)
                else -> return
            }

            try {
                mGM.openSession()
                isGlyphSessionOpen = true
                Timber.d("onServiceConnected!")
            } catch (e: GlyphException) {
                Timber.e(e)
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mGM.closeSession()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    private suspend fun A1BlockExample() {
        val builder = mGM.glyphFrameBuilder
        val frame = builder
            .run {
                buildChannel(
                    when {
                        Common.is22111() -> Code_22111.A1
                        Common.is23111() -> Code_23111.A
                        else -> Code_20111.A1
                    }
                )
            }
            .build()

        mGM.toggle(frame)
    }


    override fun onDestroy() {
        try {
            mGM.closeSession()
            isGlyphSessionOpen = false
        } catch (e: GlyphException) {
            Timber.e(e)
        }
        mGM.unInit(applicationContext)
        super.onDestroy()
    }


    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannelCompat.Builder(
            SERVICE_CHANNEL_ID,
            NotificationManager.IMPORTANCE_LOW
        )
            .setName("GlyphExtender")
            .build()
        NotificationManagerCompat.from(this).createNotificationChannel(channel)

        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("GlyphExtender is running. Tap to turn the notification off.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        mGM.init(applicationContext, mCallback)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_REDELIVER_INTENT
    }

    private var currentProgress = 0
    private var currentMediaToken: MediaSession.Token? = null
    private var currentMediaController: MediaController? = null
    private var currentPackageName: String? = null
    private var currentMediaDurationMs: Long = 0

    private var lastMediaPlaybackState: Int = PlaybackState.STATE_NONE
    private val glyphFrameC = mGM.glyphFrameBuilder.buildChannelC().build()

    private var progressJob: Job? = null

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        if (!isGlyphSessionOpen) return
        scope.launch {
            val keySet = sbn.notification.extras.keySet()
            val extrasData = keySet
                .joinToString("\n") { key -> "$key: ${sbn.notification.extras[key]}" }
            Timber.d(
                "onNotificationPosted: ${sbn.packageName}"
//                        + "\n${sbn.notification}" + "\n${extrasData}"
            )

            /*
                        if (sbn.packageName == "ru.yandex.taxi") {
                            // get car progress from notification and convert it to glyph progress
                        val carProgress = sbn.notification.extras["car_progress"] as Int
                        val glyphProgress = carProgress * 100 / 255
                        val frame = mGM.glyphFrameBuilder.buildChannelA().buildProgress(glyphProgress).build()
                        }
            */

            if (keySet.contains(Notification.EXTRA_MEDIA_SESSION)) {
                val mediaSessionToken = sbn.getMediaSessionToken() ?: return@launch
                updateMediaSession(
                    packageName = sbn.packageName,
                    token = mediaSessionToken
                )
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        if (!isGlyphSessionOpen) return
//        val keySet = sbn.notification.extras.keySet()
//        val extrasData = keySet
//            .joinToString("\n") { key -> "$key: ${sbn.notification.extras[key]}" }
        Timber.d("onNotificationRemoved: ${sbn.packageName}, ${sbn.notification}")
        scope.launch {
            // current media session is removed
            if (sbn.packageName == currentPackageName) {
                cleanupMediaSession()
                mGM.animateProgressBackToStop(glyphFrameC, from = currentProgress)
//                mGM.turnOff()
            }
        }
    }

    private suspend fun updateMediaSession(token: MediaSession.Token, packageName: String) {
        Timber.d("Updating media session with token: $token")
        currentPackageName = packageName
        currentMediaToken = token
        currentMediaController = MediaController(this@GlyphService, token)
        progressJob?.cancelAndJoin()
        progressJob = startMediaNotificationProgress()
    }

    // get music player notification and convert it to glyph frame with progress
    private fun startMediaNotificationProgress(): Job? {
        val metadata = currentMediaController?.metadata ?: return null
        if (!metadata.containsKey(MediaMetadata.METADATA_KEY_DURATION)) {
            return null
        }

        val duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION).also {
            currentMediaDurationMs = it
        }
        return scope.launch {
            Timber.d("Media playback job started")
            val tick = (duration / 100).coerceIn(300L..12000L) // 1% of duration or 12s
            Timber.d("Media playback duration: $duration, tick: $tick")

            while (isActive) {
                val (shouldContinue, progress) = checkAndShowProgressWithGlyph()
                currentProgress = progress
                if (!shouldContinue) break

                delay(tick)
            }

            Timber.d("Media playback progress coroutine finishing")
//            mGM.animateProgressBackToStop(glyphFrameC, lastProgress)
            cleanupMediaSession()
        }
    }

    private fun cleanupMediaSession() {
        Timber.d("Cleaning up media session!")
        currentMediaToken = null
        progressJob = null
        currentMediaController = null
        currentPackageName = null
        currentMediaDurationMs = 0
        lastMediaPlaybackState = PlaybackState.STATE_NONE
    }

    /**
     * @return true + progress if should continue, false + progress if should stop
     * */
    private suspend fun checkAndShowProgressWithGlyph(): Pair<Boolean, Int> {
        val duration = currentMediaDurationMs
        val mediaController = currentMediaController
        val frame = glyphFrameC

        val playbackState = mediaController?.playbackState ?: return false to 0
        val progress = (playbackState.position * 100 / duration.coerceAtLeast(1L))
            .toInt()
            .coerceIn(0, 100)
        val state = playbackState.state
        Timber.d("Media playback progress: $progress, state: $state")

        return when (state) {
            PlaybackState.STATE_PLAYING -> {
                mGM.displayProgress(frame, progress)
                true to progress
            }

            lastMediaPlaybackState -> {
                // make sure to run the animations only once
                Timber.d("Media playback state is the same as last: $state")
                false to progress
            }

            PlaybackState.STATE_SKIPPING_TO_NEXT, PlaybackState.STATE_SKIPPING_TO_PREVIOUS, PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM, PlaybackState.STATE_PAUSED, PlaybackState.STATE_STOPPED, PlaybackState.STATE_NONE -> {
                handleNonPlayingStates(state = state, progress = progress)
                false to progress
            }

            else -> true to progress
        }.also { lastMediaPlaybackState = state }
    }

    private suspend fun handleNonPlayingStates(state: Int, progress: Int) {
        when (state) {
            PlaybackState.STATE_PAUSED -> mGM.showPauseBlink()
            PlaybackState.STATE_SKIPPING_TO_NEXT -> {
//                mGM.animateProgressForward(glyphFrameC, to = 100)
            }

            // just quit and it will animate back to stop in the outer loop
            PlaybackState.STATE_SKIPPING_TO_PREVIOUS, PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM -> {
//                mGM.animateProgressBackToStop(glyphFrameC, from = progress)
            }

            PlaybackState.STATE_STOPPED, PlaybackState.STATE_NONE -> {
//                mGM.animateProgressBackToStop(glyphFrameC, from = progress)
            }
        }
        Timber.d("Handled non-playing media playback state: $state")
    }
}
