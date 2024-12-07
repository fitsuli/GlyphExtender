package com.fitsuli.glyphextender

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.IBinder
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fitsuli.glyphextender.glyph.GlyphSessionManager
import com.fitsuli.glyphextender.notification.MediaNotificationProcessor
import com.fitsuli.glyphextender.notification.ProgressNotificationProcessor
import com.fitsuli.glyphextender.notification.YandexNotificationProcessor
import com.fitsuli.glyphextender.utils.prefs.NotificationItem
import com.nothing.ketchum.GlyphManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val SERVICE_CHANNEL_ID = "glyph_extender"

class GlyphService : NotificationListenerService() {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val glyphSessionManager by lazy(LazyThreadSafetyMode.NONE) {
        GlyphSessionManager(applicationContext)
    }
    private val notificationProcessors by lazy(LazyThreadSafetyMode.NONE) {
        listOf(
            MediaNotificationProcessor(
                sessionManager = glyphSessionManager,
                glyphManager = GlyphManager.getInstance(),
                scope = scope,
                context = applicationContext
            ),
            YandexNotificationProcessor(
                sessionManager = glyphSessionManager,
                glyphManager = GlyphManager.getInstance(),
                context = applicationContext
            ),
            ProgressNotificationProcessor(
                sessionManager = glyphSessionManager,
                glyphManager = GlyphManager.getInstance()
            )
        )
    }
    private val notificationFlow = MutableSharedFlow<StatusBarNotification>()
    private val notificationPreferences = App.appModule.notificationPrefsRepo

    private val notificationProcessingFlow = notificationFlow
        .onEach { sbn ->
            val notificationItem = NotificationItem(
                id = sbn.id,
                title = sbn.notification.extras.getCharSequence(Notification.EXTRA_TITLE)
                    ?.toString(),
                text = sbn.notification.extras.getCharSequence(Notification.EXTRA_TEXT)?.toString(),
                date = sbn.postTime,
            )
            notificationPreferences.addNotification(notificationItem)

            notificationProcessors
                .firstOrNull { it.canProcess(sbn) }
                ?.process(sbn)
        }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createServiceNotification())
        notificationProcessingFlow.launchIn(scope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        scope.launch {
            notificationFlow.emit(sbn)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        scope.launch {
            notificationProcessors.forEach { it.onNotificationRemoved(sbn) }
        }
    }

    override fun onDestroy() {
        runBlocking {
            glyphSessionManager.disconnect()
            scope.cancel()
        }
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannelCompat.Builder(
            SERVICE_CHANNEL_ID,
            NotificationManager.IMPORTANCE_LOW
        )
            .setName("GlyphExtender")
            .build()
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
    }

    private fun createServiceNotification() = NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
        .setContentTitle(getString(R.string.app_name))
        .setContentText("GlyphExtender is running. Tap to turn the notification off.")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        .build()
}
