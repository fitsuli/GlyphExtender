package com.fitsuli.glyphextender

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.fitsuli.glyphextender.ui.theme.GlyphExtenderTheme
import com.nothing.ketchum.Common
import com.nothing.ketchum.Glyph.Code_22111
import com.nothing.ketchum.GlyphException
import com.nothing.ketchum.GlyphManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : ComponentActivity() {
    private val mGM: GlyphManager by lazy {
        GlyphManager.getInstance(applicationContext)
    }
    private var mCallback: GlyphManager.Callback = object : GlyphManager.Callback {
        override fun onServiceConnected(componentName: ComponentName) {
            if (Common.is20111()) mGM.register(Common.DEVICE_20111)
            if (Common.is22111()) mGM.register(Common.DEVICE_22111)
            if (Common.is23111()) mGM.register(Common.DEVICE_23111)
            try {
                mGM.openSession()
            } catch (e: GlyphException) {
                Timber.e(e)
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mGM.closeSession()
        }
    }

    public override fun onDestroy() {
        try {
            mGM.closeSession()
        } catch (e: GlyphException) {
            Timber.e(e)
        }
        mGM.unInit()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //mGM.init(mCallback)
        setContent {
            GlyphExtenderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TestUi(mGM)
                }
            }
        }
    }

}

@Composable
private fun TestUi(mGM: GlyphManager) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .systemBarsPadding()
    ) {
        Button(onClick = {
            val builder = mGM.glyphFrameBuilder
            val frame = builder.buildChannelA().build()
            mGM.toggle(frame)
        }) {
            Text(text = "A")
        }
        Button(onClick = {
            val builder = mGM.glyphFrameBuilder
            val frame = builder.buildChannelB().buildInterval(7000)
                .buildCycles(2).buildPeriod(3000).build()
            mGM.animate(frame)
        }) {
            Text(text = "B")
        }
        Button(onClick = {
            val builder = mGM.glyphFrameBuilder
            val frame = builder
                .run {
                    if (Common.is22111()) {
                        buildChannel(Code_22111.A1, 4000)
                    } else this
                }
                .build()
            //                                .buildInterval()
            mGM.toggle(frame)
        }) {
            Text(text = "C")
        }
        val scope = rememberCoroutineScope()
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
                mGM.turnOff()
            }
        ) {
            Text(text = "turn off")
        }
    }
}

