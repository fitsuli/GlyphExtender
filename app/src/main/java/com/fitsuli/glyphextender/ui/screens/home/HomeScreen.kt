package com.fitsuli.glyphextender.ui.screens.home

import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.fitsuli.glyphextender.BuildConfig
import com.fitsuli.glyphextender.GlyphService
import com.nothing.ketchum.Common
import com.nothing.ketchum.Glyph.Code_22111
import com.nothing.ketchum.GlyphManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Dmitry Danilyuk at 04.08.2024
 */
private val mGM: GlyphManager by lazy {
    GlyphManager.getInstance()
}

@Composable
fun HomeScreen() {
    TestUi()
}

@Composable
private fun TestUi() {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .systemBarsPadding()
    ) {
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
        val context = LocalContext.current
        Button(
            onClick = {
                context.startForegroundService(Intent(context, GlyphService::class.java))
            }
        ) {
            Text(text = "run the service manually (!!!)")
        }

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
        Button(
            onClick = {
                scope.launch { mGM.turnOff() }
            }
        ) {
            Text(text = "turn off")
        }
    }
}
