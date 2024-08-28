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
import com.fitsuli.glyphextender.extensions.animateProgress
import com.fitsuli.glyphextender.extensions.animateProgressBackToStop
import com.fitsuli.glyphextender.extensions.showPauseBlink
import com.fitsuli.glyphextender.utils.getMediaSessionToken
import com.fitsuli.glyphextender.utils.getTitleAny
import com.fitsuli.glyphextender.utils.prefs.NotificationItem
import com.nothing.ketchum.Common
import com.nothing.ketchum.Glyph.Code_20111
import com.nothing.ketchum.Glyph.Code_22111
import com.nothing.ketchum.Glyph.Code_23111
import com.nothing.ketchum.GlyphCallback
import com.nothing.ketchum.GlyphException
import com.nothing.ketchum.GlyphManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.File


private const val SERVICE_CHANNEL_ID = "glyph_extender"
private const val MIN_DURATION_PROGRESS_MS = 5000

class GlyphService : NotificationListenerService() {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val notificationPreferences = App.appModule.notificationPrefsRepo

    private var isGlyphSessionOpen = false
    private val serviceConnectionState =
        MutableStateFlow<ServiceConnectionState>(ServiceConnectionState.NotConnected)
    private val mGM: GlyphManager = GlyphManager.getInstance()
    private var mCallback: GlyphCallback = object : GlyphCallback {
        override fun onServiceConnected(componentName: ComponentName) {
            serviceConnectionState.update { ServiceConnectionState.Connected }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            serviceConnectionState.update { ServiceConnectionState.NotConnected }
        }
    }

    private val serviceConnectedFlow = serviceConnectionState
        .onEach { connectServiceWhenNotConnected() }
        .mapNotNull { it as? ServiceConnectionState.Connected }
        .onEach { Timber.d("serviceConnectionFlow: connected") }
        .take(1)
        .onEach {
            when {
                Common.is20111() -> mGM.register(applicationContext, Common.DEVICE_20111)
                Common.is22111() -> mGM.register(applicationContext, Common.DEVICE_22111)
                Common.is23111() -> mGM.register(applicationContext, Common.DEVICE_23111)
                else -> return@onEach
            }

            try {
                mGM.openSession()
                Timber.d("onServiceConnected!")
            } catch (e: GlyphException) {
                Timber.e(e)
            }
        }

    private suspend fun connectServiceAndAwait() {
        if (serviceConnectionState.value is ServiceConnectionState.Connected) {
            Timber.d("connectServiceAndAwait: already connected")
            return
        }

        serviceConnectedFlow.first()
    }

    private val serviceDisconnectedFlow = serviceConnectionState
        .onEach { state ->
            if (state is ServiceConnectionState.Connected) {
                mGM.closeSession()
                if (serviceConnectionState.compareAndSet(
                        ServiceConnectionState.Connected,
                        ServiceConnectionState.NotConnected
                    )
                ) {
                    disconnectService()
                }
            }
        }
        .mapNotNull { it as? ServiceConnectionState.NotConnected }
        .onEach { Timber.d("serviceConnectionFlow: onServiceDisconnected") }
        .take(1)

    private suspend fun disconnectServiceAndAwait() = serviceDisconnectedFlow.first()


    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    private fun connectService() {
        Timber.d("reconnectServiceIfNecessary: glyph session is not open, reconnecting...")
        runCatching {
            mGM.init(applicationContext, mCallback)
        }.onFailure {
            Timber.e(it)
        }
    }

    private fun disconnectService() {
        Timber.d("disconnectService: glyph session is open, disconnecting...")
        runCatching {
            mGM.unInit(applicationContext)
        }.onFailure {
            Timber.e(it)
        }
    }

    private fun connectServiceWhenNotConnected() {
        Timber.d("connectServiceWhenNotConnected: ${serviceConnectionState.value}")
        if (serviceConnectionState.compareAndSet(
                ServiceConnectionState.NotConnected,
                ServiceConnectionState.Connecting
            )
        ) {
            connectService()
        }
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
            runBlocking { disconnectServiceAndAwait() }
        } catch (e: GlyphException) {
            Timber.e(e)
        }
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
        notificationListenerFlow.launchIn(scope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_REDELIVER_INTENT
    }

    private var currentProgress = 0
    private var currentMediaToken: MediaSession.Token? = null
    private var currentMediaController: MediaController? = null
    private var currentPackageName: String? = null
    private var currentMediaDurationMs: Long = 0

    private val notificationQueue = MutableStateFlow<List<StatusBarNotification>>(emptyList())
    private var lastMediaPlaybackState: Int = PlaybackState.STATE_NONE
    private val glyphFrameC = mGM.glyphFrameBuilder.buildChannelC().build()

    private var progressJob: Job? = null
    private val allowedYandexChannelIds = setOf("order", "order_low")
    private var taxiActivityMaxProgress: Int? = null

    private val notificationListenerFlow = notificationQueue
        .mapNotNull { it.firstOrNull() }
        .onEach { Timber.d("notificationListenerFlow: ${it.packageName}") }
        .onEach { processStatusBarNotification(it) }
        .onEach { notificationQueue.update { it.drop(1) } }


    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        scope.launch {
            notificationQueue.update { it + sbn }
        }
    }

    private suspend fun processStatusBarNotification(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras ?: return
        val extrasData = extras.keySet().joinToString("\n") { key ->
            "$key: ${extras[key]}"
        }
        Timber.d(
            "onNotificationPosted: ${sbn.packageName}" + "\n${sbn.notification}"
            //                        + "\n$extrasData"
        )
        val notificationItem = NotificationItem(
            id = sbn.id,
            title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString(),
            text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString(),
            date = sbn.postTime,
        )

        notificationPreferences.addNotification(notificationItem)

        if (sbn.packageName == "ru.yandex.taxi" && sbn.notification.channelId in allowedYandexChannelIds) {
            val file = File(getExternalFilesDir(null), "notifications.txt").apply {
                parentFile?.mkdirs()
            }
            Timber.d(
                "onNotificationPosted: ${sbn.packageName}\n${sbn.notification}\n${extrasData}\n\n"
                    .also { file.appendText(it) }
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
                Timber.d(
                    "Taxi arrived or unable to parse the minutes left: '$notificationText' \n"
                        .also { file.appendText(it) }
                )
                connectServiceAndAwait()
                mGM.animateProgressBackToStop(glyphFrameC, from = currentProgress)
                currentProgress = 0
                taxiActivityMaxProgress = null
                disconnectServiceAndAwait()
                return
            }

            if (taxiActivityMaxProgress == null && minutesLeft > 0) {
                // first time we see the notification with minutes left
                taxiActivityMaxProgress = minutesLeft
            }
            val progress =
                taxiActivityMaxProgress?.let { (minutesLeft * 100 / it).coerceIn(0, 100) }
                    ?: return
            currentProgress = 100 - progress
            Timber.d(
                "Taxi activity progress: $progress, real progress: $currentProgress"
                    .also { file.appendText(it) }
            )
            connectServiceAndAwait()
            mGM.animateProgress(frame = glyphFrameC, to = currentProgress)
            return
        }

        // handle simple notification progress
        val progress = extras.getInt(Notification.EXTRA_PROGRESS)
        val maxProgress = extras.getInt(Notification.EXTRA_PROGRESS_MAX)
        if (progress != 0 && maxProgress != 0) {
            currentPackageName = sbn.packageName
            connectServiceAndAwait()
            val glyphProgress = (progress * 100 / maxProgress).coerceIn(0, 100)
            Timber.d("Progress: $progress, max: $maxProgress, final glyphProgress: $glyphProgress")
            mGM.animateProgress(frame = glyphFrameC, to = glyphProgress)
            return
        }

        if (extras.containsKey(Notification.EXTRA_MEDIA_SESSION)) {
            connectServiceAndAwait()
            val mediaSessionToken = sbn.getMediaSessionToken() ?: return
            updateMediaSession(
                packageName = sbn.packageName,
                token = mediaSessionToken
            )
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
//        val keySet = sbn.notification.extras.keySet()
//        val extrasData = keySet
//            .joinToString("\n") { key -> "$key: ${sbn.notification.extras[key]}" }
        Timber.d("onNotificationRemoved: ${sbn.packageName}, ${sbn.notification}")
        scope.launch {
            // current media session is removed
            if (sbn.packageName == currentPackageName) {
                connectServiceAndAwait()
                progressJob?.cancelAndJoin()
                cleanupMediaSession()
                mGM.animateProgressBackToStop(glyphFrameC, from = currentProgress)
                disconnectServiceAndAwait()
            }
        }
    }

    private suspend fun updateMediaSession(token: MediaSession.Token, packageName: String) {
        Timber.d("Updating media session with token: $token")
        progressJob?.cancelAndJoin()
        currentPackageName = packageName
        currentMediaToken = token
        currentMediaController = MediaController(this@GlyphService, token)
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
            if (duration <= MIN_DURATION_PROGRESS_MS) {
                Timber.d("Media playback duration is too short: $duration")
                return@launch
            }

            val tick = (duration / 100).coerceIn(300L..12000L) // 1% of duration or 12s
            Timber.d("Media playback duration: $duration, tick: $tick")

            while (isActive) {
                val (shouldContinue, progress) = checkAndShowProgressWithGlyph()
                currentProgress = progress
                if (!shouldContinue) break

                delay(tick)
            }

            Timber.d(
                "Media playback progress finishing, last progress: ${currentProgress}\n"
                        + "Cleaning up media session and waiting for a few seconds to disconnect"
            )
            cleanupMediaSession()
            delay(5000)
            if (isActive) {
                mGM.animateProgressBackToStop(frame = glyphFrameC, from = currentProgress)
                disconnectServiceAndAwait()
                Timber.d("Media playback job finished, glyph session closed")
            }
        }
    }

    private fun cleanupMediaSession() {
        Timber.d("Cleaning up media session!")
        currentMediaToken = null
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
                mGM.animateProgress(frame = frame, to = progress)
                true to progress
            }

            lastMediaPlaybackState -> {
                // make sure to run the animations only once
                Timber.d("Media playback state is the same as last: $state")
                false to progress
            }

            PlaybackState.STATE_PAUSED -> {
                mGM.showPauseBlink()
                delay(1500)
                mGM.animateProgress(frame = frame, to = 0, slower = true)
                false to 0
            }

            else -> false to progress
        }.also { lastMediaPlaybackState = state }
    }
}
