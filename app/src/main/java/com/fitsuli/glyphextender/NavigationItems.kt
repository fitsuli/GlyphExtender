package com.fitsuli.glyphextender

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.ui.graphics.vector.ImageVector
import com.fitsuli.glyphextender.ui.navigation.Destinations

enum class NavigationItems(
    val route: Any,
    val imageVector: ImageVector,
    @StringRes val resourceId: Int,
) {
    Home(Destinations.Home, Icons.Rounded.Home, R.string.home),
    Progress(Destinations.Progress, Icons.Rounded.Timeline, R.string.progress),
    Notifications(Destinations.Notifications, Icons.Rounded.Notifications, R.string.notifications),
    Settings(Destinations.Settings, Icons.Rounded.Settings, R.string.settings),
}
