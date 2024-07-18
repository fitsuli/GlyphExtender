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
import com.fitsuli.glyphextender.extensions.animateProgressBack
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

    private val mGM: GlyphManager = GlyphManager.getInstance(this)
    private var mCallback: GlyphManager.Callback = object : GlyphManager.Callback {
        override fun onServiceConnected(componentName: ComponentName) {
            if (Common.is20111()) mGM.register(Common.DEVICE_20111)
            if (Common.is22111()) mGM.register(Common.DEVICE_22111)
            if (Common.is23111()) mGM.register(Common.DEVICE_23111)
            try {
                mGM.openSession()
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

    private fun A1BlockExample() {
        val builder = mGM.glyphFrameBuilder
        val frame = builder
            .run {
                buildChannel(
                    when {
                        Common.is22111() -> Code_22111.A1
                        Common.is23111() -> Code_23111.A
                        else -> Code_20111.A1
                    },
                    4000
                )
            }
            .build()

        mGM.toggle(frame)
    }


    override fun onDestroy() {
        try {
            mGM.closeSession()
        } catch (e: GlyphException) {
            Timber.e(e)
        }
        mGM.unInit()
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
            .setContentTitle("GlyphExtender")
            .setContentText("GlyphExtender is running. Tap to turn the notification off.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        mGM.init(mCallback)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private var runningMediaToken: MediaSession.Token? = null
    private var progressJob: Job? = null

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        scope.launch {
            val keySet = sbn.notification.extras.keySet()
            val extrasData = keySet
                .joinToString("\n") { key -> "$key: ${sbn.notification.extras[key]}" }
            Timber.d(
                "onNotificationPosted: " +
                        "\n${sbn.packageName}" +
                        "\n${sbn.notification}" +
                        "\n${extrasData}"
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
                val mediaSessionToken = sbn.notification.extras.getParcelable(
                    Notification.EXTRA_MEDIA_SESSION,
                    MediaSession.Token::class.java
                ) ?: return@launch
                if (mediaSessionToken == runningMediaToken) {
                    Timber.d("Media session is already running")
                    return@launch
                }
                Timber.d("New media session token: $mediaSessionToken")
                runningMediaToken = mediaSessionToken
                progressJob?.cancelAndJoin()
                progressJob = startMediaNotificationProgress(mediaSessionToken)
            }
        }
    }

    // get music player notification and convert it to glyph frame with progress
    private fun startMediaNotificationProgress(mediaSessionToken: MediaSession.Token): Job? {
        val mediaController = MediaController(this, mediaSessionToken)
        val metadata = mediaController.metadata ?: return null
        if (!metadata.containsKey(MediaMetadata.METADATA_KEY_DURATION)) {
            return null
        }

        return scope.launch {
            val duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)
            val tick = (duration / 100).coerceAtMost(12000) // 1% of duration or 12s
            Timber.d("Media playback duration: $duration, tick: $tick")
            val builder = mGM.glyphFrameBuilder
            val frame = builder.buildChannelC().build()

            var progress = 0
            while (isActive) {
                val playbackState = mediaController.playbackState ?: return@launch
                if (playbackState.state != PlaybackState.STATE_PLAYING) {
                    Timber.d("Media playback state stop at: ${playbackState.state}")
                    break
                }
                progress = (playbackState.position.toInt() * 100 / duration.toInt())
                    .coerceIn(0, 100)
                Timber.d("Media playback progress: $progress")
                mGM.displayProgress(frame, progress)
                delay(tick)
            }
            mGM.animateProgressBack(frame, progress)
        }.apply {
            invokeOnCompletion {
                Timber.d(it, "Media playback progress coroutine completed")
                runningMediaToken = null
                progressJob = null
            }
        }
    }
}
