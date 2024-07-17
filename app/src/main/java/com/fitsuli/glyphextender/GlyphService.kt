package com.fitsuli.glyphextender

import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nothing.ketchum.Common
import com.nothing.ketchum.Glyph.Code_22111
import com.nothing.ketchum.GlyphException
import com.nothing.ketchum.GlyphManager

class GlyphService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private val mGM: GlyphManager = GlyphManager.getInstance(this)
    private var mCallback: GlyphManager.Callback = object : GlyphManager.Callback {
        override fun onServiceConnected(componentName: ComponentName) {
            if (Common.is20111()) mGM.register(Common.DEVICE_20111)
            if (Common.is22111()) mGM.register(Common.DEVICE_22111)
            if (Common.is23111()) mGM.register(Common.DEVICE_23111)
            try {
                mGM.openSession()
                A1BlockExample()
            } catch (e: GlyphException) {
                Log.e("FFFF", e.message!!)
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mGM.closeSession()
        }
    }

    private fun A1BlockExample() {
        val builder = mGM.glyphFrameBuilder
        val frame = builder
            .run {
                if (Common.is22111()) {
                    buildChannel(Code_22111.A1, 4000)
                } else if (Common.is23111()) {
                    buildChannel(Code_22111.A1, 4000)
                } else {
                    buildChannel(Code_22111.A1, 4000)
                }
            }
            .build()
//                                .buildInterval()
        mGM.toggle(frame)
    }


    override fun onDestroy() {
        try {
            mGM.closeSession()
        } catch (e: GlyphException) {
            Log.e("FFFF", e.message!!)
        }
        mGM.unInit()
        super.onDestroy()
    }


    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannelCompat.Builder("glyph_extender", NotificationManager.IMPORTANCE_DEFAULT)
            .setName("GlyphExtender")
            .build()
        NotificationManagerCompat.from(this).createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, "glyph_extender")
            .setContentTitle("GlyphExtender")
            .setContentText("GlyphExtender is running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        startForeground(1, notification)
        mGM.init(mCallback)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

}
