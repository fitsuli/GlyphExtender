package com.fitsuli.glyphextender.ui.screens.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitsuli.glyphextender.R
import com.fitsuli.glyphextender.ui.components.BiggerSwitch
import com.fitsuli.glyphextender.ui.components.LifecycleSubscription
import com.fitsuli.glyphextender.ui.components.SimpleScaffold

/**
 * Created by Dmitry Danilyuk at 04.08.2024
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
) {
    val isListenerEnabled by viewModel.isListenerEnabledFlow.collectAsStateWithLifecycle()
    val workWithScreenOn by viewModel.workWithScreenOnFlow.collectAsStateWithLifecycle()
    
    SimpleScaffold(
        headerText = stringResource(id = R.string.home),
    ) {
        LifecycleSubscription(
            onResume = viewModel::checkNotificationListenerEnabled,
        )

        BiggerSwitch(
            text = stringResource(id = R.string.notification_listener),
            checked = isListenerEnabled,
            onClick = viewModel::openNotificationListenerSettings,
            subtitle = if (!isListenerEnabled) stringResource(id = R.string.notification_listener_explanation) else null,
        )
        Spacer(modifier = Modifier.height(8.dp))

        BiggerSwitch(
            text = stringResource(id = R.string.work_with_screen_on),
            checked = workWithScreenOn,
            onClick = { viewModel.setWorkWithScreenOn(!workWithScreenOn) },
            subtitle = stringResource(id = R.string.work_with_screen_on_explanation),
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}
