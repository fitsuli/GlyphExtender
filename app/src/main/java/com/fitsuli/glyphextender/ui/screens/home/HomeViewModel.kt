package com.fitsuli.glyphextender.ui.screens.home

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitsuli.glyphextender.BuildConfig
import com.fitsuli.glyphextender.GlyphService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by Dmitry Danilyuk at 07.10.2024
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val isListenerEnabledFlow = MutableStateFlow(false)

    fun checkNotificationListenerEnabled() {
        viewModelScope.launch(Dispatchers.IO) {
            val isEnabled = NotificationManagerCompat.getEnabledListenerPackages(getApplication())
                .contains(BuildConfig.APPLICATION_ID)
            isListenerEnabledFlow.update { isEnabled }
        }
    }

    fun openNotificationListenerSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
            .putExtra(
                Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
                ComponentName(
                    BuildConfig.APPLICATION_ID,
                    GlyphService::class.java.name
                ).flattenToString()
            )
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)


        runCatching {
            getApplication<Application>().startActivity(intent)
        }
    }

    init {
        checkNotificationListenerEnabled()
    }
}
