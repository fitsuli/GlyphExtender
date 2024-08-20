package com.fitsuli.glyphextender.utils.prefs

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
 * Created by Dmitry Danilyuk at 12.08.2024
 */
@Immutable
@Serializable
data class NotificationPreferences(
    val notificationSet: Set<NotificationItem> = setOf(),
)

@Serializable
data class NotificationItem(
    val id: Int,
    val title: String?,
    val text: String?,
    val date: Long,
)
