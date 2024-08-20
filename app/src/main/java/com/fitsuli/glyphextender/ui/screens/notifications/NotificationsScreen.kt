package com.fitsuli.glyphextender.ui.screens.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitsuli.glyphextender.R
import com.fitsuli.glyphextender.ui.components.SimpleScaffold
import com.fitsuli.glyphextender.utils.transformDate

/**
 * Created by Dmitry Danilyuk at 13.08.2024
 */
@Composable
fun NotificationsScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = viewModel(),
) {
    SimpleScaffold(
        modifier = modifier,
        scrollState = null,
        contentPadding = PaddingValues(0.dp),
        headerText = stringResource(id = R.string.notifications),
    ) {
        val notificationList by viewModel.notificationListFlow.collectAsStateWithLifecycle()
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(items = notificationList, key = { it.toString() }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(text = "ID: ${item.id}", style = MaterialTheme.typography.labelSmall)
                        if (item.title != null) {
                            Text(text = item.title, style = MaterialTheme.typography.titleSmall)
                        }
                        if (item.text != null) {
                            Text(text = item.text, style = MaterialTheme.typography.bodySmall)
                        }
                        val formattedDate = rememberSaveable(item.date) {
                            transformDate(item.date)
                        }
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
