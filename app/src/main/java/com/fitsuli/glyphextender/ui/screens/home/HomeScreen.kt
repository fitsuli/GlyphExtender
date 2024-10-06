package com.fitsuli.glyphextender.ui.screens.home

import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.NotificationManagerCompat
import com.fitsuli.glyphextender.BuildConfig
import com.fitsuli.glyphextender.GlyphService
import com.fitsuli.glyphextender.R
import com.fitsuli.glyphextender.ui.components.SimpleScaffold

/**
 * Created by Dmitry Danilyuk at 04.08.2024
 */
@Composable
fun HomeScreen() {
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
        Button(
            onClick = {
                context.startForegroundService(Intent(context, GlyphService::class.java))
            }
        ) {
            Text(text = "run the service manually (!!!)")
        }
*/

        val context = LocalContext.current
        Button(onClick = {
            if (!NotificationManagerCompat.getEnabledListenerPackages(context)
                    .contains(BuildConfig.APPLICATION_ID)
            ) {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
                    .putExtra(
                        Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
                        ComponentName(
                            BuildConfig.APPLICATION_ID,
                            GlyphService::class.java.name
                        ).flattenToString()
                    )

                runCatching {
                    context.startActivity(intent)
                }
            }
        }) {
            Text(text = "request notification access when not granted")
        }
        /*        Button(
                    onClick = {
                        scope.launch { mGM.turnOff() }
                    }
                ) {
                    Text(text = "turn off")
                }*/
    }
}
