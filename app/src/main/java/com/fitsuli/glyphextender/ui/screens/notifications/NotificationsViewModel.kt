package com.fitsuli.glyphextender.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitsuli.glyphextender.App
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Created by Dmitry Danilyuk at 13.08.2024
 */
class NotificationsViewModel : ViewModel() {

    private val notificationStore = App.appModule.notificationPrefsRepo
    val notificationListFlow = notificationStore.getNotificationsFlow()
        .map { items ->
            items.sortedByDescending { it.date }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

}
