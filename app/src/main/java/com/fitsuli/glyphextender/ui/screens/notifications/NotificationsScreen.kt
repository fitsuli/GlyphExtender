package com.fitsuli.glyphextender.ui.screens.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Created by Dmitry Danilyuk at 13.08.2024
 */
@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = viewModel(),
) {
    Column(
        modifier = modifier.padding(16.dp)
    ) {
        val notificationList by viewModel.notificationListFlow.collectAsStateWithLifecycle()
        LazyColumn {
            items(items = notificationList, key = { it.toString() }) {
                Text(text = it.toString())
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
