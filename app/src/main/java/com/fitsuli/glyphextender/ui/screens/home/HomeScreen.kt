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
    SimpleScaffold(
        headerText = stringResource(id = R.string.home),
    ) {
/*
        val scope = rememberCoroutineScope()
        Button(onClick = {
            scope.launch {
                val builder = mGM.glyphFrameBuilder
                val frame = builder.buildChannelA().build()
                mGM.toggle(frame)
            }
        }) {
            Text(text = "A")
        }
        Button(onClick = {
            scope.launch {
                val builder = mGM.glyphFrameBuilder
                val frame = builder.buildChannelB().buildInterval(7000)
                    .buildCycles(2).buildPeriod(3000).build()
                mGM.animate(frame)
            }
        }) {
            Text(text = "B")
        }
        Button(onClick = {
            scope.launch {
                val builder = mGM.glyphFrameBuilder
                val frame = builder
                    .run {
                        if (Common.is22111()) {
                            buildChannel(Code_22111.A1)
                        } else this
                    }
                    .build()

                mGM.toggle(frame)
            }
        }) {
            Text(text = "C")
        }
        Button(
            onClick = {
                val builder = mGM.glyphFrameBuilder
                val frame = builder.buildChannelC().build()
                scope.launch {
                    repeat(100) { i ->
                        mGM.displayProgress(frame, i)
                        delay(100)
                    }
                }
            }
        ) {
            Text(text = "progress")
        }

*/

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

        /*        Button(
                    onClick = {
                        scope.launch { mGM.turnOff() }
                    }
                ) {
                    Text(text = "turn off")
                }*/
    }
}
