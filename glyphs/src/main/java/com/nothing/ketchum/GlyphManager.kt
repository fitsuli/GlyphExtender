package com.nothing.ketchum

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import com.nothing.ketchum.Glyph.Code_20111
import com.nothing.ketchum.Glyph.Code_22111
import com.nothing.ketchum.Glyph.Code_23111
import com.nothing.thirdparty.IGlyphService
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/* loaded from: KetchumSDK_Community_20240307.jar:com/nothing/ketchum/GlyphManager.class */
class GlyphManager private constructor(private val mContext: Context) {
    private var mService: IGlyphService? = null
    private var mCallback: Callback? = null
    private val mConnection = RemoteServiceConnection()
    private var mHasAuthorized = false
    private val isTaskOn = false
    private var mTask: Future<*>? = null
    private var mDevice: String? = null
    private val mExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    /* loaded from: KetchumSDK_Community_20240307.jar:com/nothing/ketchum/GlyphManager$Callback.class */
    interface Callback {
        fun onServiceConnected(componentName: ComponentName)

        fun onServiceDisconnected(componentName: ComponentName)
    }

    fun init(callback: Callback?) {
        this.mCallback = callback
        val launchService = Intent()
        launchService.setPackage("com.nothing.thirdparty")
        launchService.setAction("com.nothing.thirdparty.bind_glyphservice")
        launchService.setComponent(
            ComponentName(
                "com.nothing.thirdparty",
                "com.nothing.thirdparty.GlyphService"
            )
        )
        mContext.bindService(launchService, this.mConnection, GLYPH_C)
    }

    fun unInit() {
        mContext.unbindService(this.mConnection)
    }

    fun register(): Boolean {
        try {
            this.mDevice = Common.DEVICE_22111
            val key = Common.getAppKey(this.mContext)
            this.mHasAuthorized = mService!!.registerSDK(key, Common.DEVICE_22111)
        } catch (e: RemoteException) {
            Log.e(TAG, e.message!!)
        } catch (e2: Exception) {
            Log.e(TAG, e2.message, e2)
        }
        Log.w(TAG, "You are targeting " + Common.DEVICE_22111 + " as your device.")
        return this.mHasAuthorized
    }

    fun register(targetDevice: String): Boolean {
        try {
            this.mDevice = targetDevice
            val key = Common.getAppKey(this.mContext)
            this.mHasAuthorized =
                mService!!.registerSDK(key, targetDevice)
        } catch (e: RemoteException) {
            Log.e(TAG, e.message!!)
        } catch (e2: Exception) {
            Log.e(TAG, e2.message, e2)
        }
        Log.w(TAG, "You are targeting $targetDevice as your device.")
        return this.mHasAuthorized
    }

    val glyphFrameBuilder: GlyphFrame.Builder
        get() {
            if (this.mDevice == null) {
                return GlyphFrame.Builder()
            }
            return GlyphFrame.Builder(this.mDevice)
        }

    @Throws(GlyphException::class)
    fun openSession() {
        if (!this.mHasAuthorized) {
            Log.d(TAG, "Non registed")
        } else {
            if (this.mService == null) {
                throw GlyphException("Please use it after service connected.")
            }
            try {
                mService!!.openSession()
            } catch (e: RemoteException) {
                Log.d(TAG, e.message!!)
            }
        }
    }

    @Throws(GlyphException::class)
    fun closeSession() {
        if (!this.mHasAuthorized) {
            Log.d(TAG, "Non registed")
            return
        }
        if (this.mService == null) {
            throw GlyphException("Please use it after service connected.")
        }
        try {
            stopCurrentTask()
            mService!!.closeSession()
        } catch (e: RemoteException) {
            Log.d(TAG, e.message!!)
        }
    }

    @Throws(GlyphException::class)
    fun setFrameColors(colors: IntArray?) {
        if (!this.mHasAuthorized) {
            Log.d(TAG, "Non registed")
            return
        }
        if (this.mService == null) {
            throw GlyphException("Please use it after service connected.")
        }
        try {
            stopCurrentTask()
            mService!!.setFrameColors(colors)
        } catch (e: RemoteException) {
            Log.e(TAG, e.message!!)
        }
    }

    fun turnOff() {
        if (!this.mHasAuthorized) {
            Log.d(TAG, "Non registed")
            return
        }
        stopCurrentTask()
        // from class: com.nothing.ketchum.GlyphManager.1
        // java.lang.Runnable
        val task = Runnable {
            try {
                val builder = this@GlyphManager.glyphFrameBuilder
                val emptyFrame = builder.build()
                mService!!.setFrameColors(emptyFrame.channel)
            } catch (e: RemoteException) {
                Log.e(TAG, e.message!!)
            }
        }
        executeFrame(task)
    }

    fun toggle(frame: GlyphFrame) {
        if (!this.mHasAuthorized) {
            Log.d(TAG, "Non registed")
            return
        }
        stopCurrentTask()
        // from class: com.nothing.ketchum.GlyphManager.2
        // java.lang.Runnable
        val task = Runnable {
            try {
                mService!!.setFrameColors(frame.channel)
            } catch (e: RemoteException) {
                Log.e(TAG, e.message!!)
            }
        }
        executeFrame(task)
    }

    fun animate(frame: GlyphFrame) {
        if (!this.mHasAuthorized) {
            Log.d(TAG, "Non registed")
            return
        }
        stopCurrentTask()
        // from class: com.nothing.ketchum.GlyphManager.3
        // java.lang.Runnable
        val task = Runnable {
            var light: Int
            val period = frame.period
            val interval = frame.interval
            val emptyFrame = GlyphFrame.Builder().build()
            var cycle = NO_LIGHT
            while (cycle < frame.cycles) {
                val startOn = System.currentTimeMillis()
                while (System.currentTimeMillis() - startOn < period) {
                    try {
                        val colors = frame.channel
                        light = if (System.currentTimeMillis() - startOn < period / GLYPH_D) {
                            NO_LIGHT + ((DEFAULT_MAX_LIGHT / (period / GLYPH_D)) * ((System.currentTimeMillis() - startOn).toInt()))
                        } else {
                            DEFAULT_MAX_LIGHT - ((DEFAULT_MAX_LIGHT / (period / GLYPH_D)) * (((System.currentTimeMillis() - startOn) - (period / GLYPH_D)).toInt()))
                        }
                        var i = NO_LIGHT
                        while (i < colors.size) {
                            if (colors[i] != 0) {
                                colors[i] = light
                            }
                            i += GLYPH_C
                        }
                        mService!!.setFrameColors(colors)
                    } catch (e: RemoteException) {
                        Log.e(TAG, e.message!!)
                    }
                    this@GlyphManager.pauseAWhile(10L)
                }
                val startOff = System.currentTimeMillis()
                while (System.currentTimeMillis() - startOff < interval) {
                    try {
                        mService!!.setFrameColors(emptyFrame.channel)
                    } catch (e2: RemoteException) {
                        Log.e(TAG, e2.message!!)
                    }
                    this@GlyphManager.pauseAWhile(100L)
                }
                cycle += GLYPH_C
            }
        }
        executeFrame(task)
    }

    @Throws(GlyphException::class)
    fun displayProgressAndToggle(frame: GlyphFrame, progress: Int, isReverse: Boolean) {
        displayProgress(frame, progress, isReverse, true)
    }

    @Throws(GlyphException::class)
    fun displayProgress(frame: GlyphFrame, progress: Int) {
        displayProgress(frame, progress, false, false)
    }

    @Throws(GlyphException::class)
    fun displayProgress(frame: GlyphFrame, progress: Int, isReverse: Boolean) {
        displayProgress(frame, progress, isReverse, false)
    }

    @Throws(GlyphException::class)
    private fun displayProgress(
        frame: GlyphFrame,
        progress: Int,
        isReverse: Boolean,
        isToggle: Boolean,
    ) {
        if (!this.mHasAuthorized) {
            Log.d(TAG, "Non registed")
            return
        }
        stopCurrentTask()
        val channel = frame.channel
        var light = NO_LIGHT
        if (Common.isTargetDevice20111(this.mDevice)) {
            if (channel[Code_20111.D1_1] == 0) {
                throw GlyphException("Please choose D1_1 while using display progress in 20111.")
            }
            light = GLYPH_D
        }
        if (Common.isTargetDevice22111(this.mDevice)) {
            if ((channel[Code_22111.C1_1] == 0 && channel[Code_22111.D1_1] == 0) || (channel[Code_22111.C1_1] > 0 && channel[Code_22111.D1_1] > 0)) {
                throw GlyphException("Please choose C1 or D1 while using display progress in 22111")
            }
            light = if (channel[Code_22111.C1_1] == 0) GLYPH_D else GLYPH_C
        }
        if (Common.isTargetDevice23111(this.mDevice)) {
            if (channel[Code_23111.C_1] == 0) {
                throw GlyphException("Please choose C_1 while using display progress in 23111.")
            }
            light = GLYPH_C
        }
        val finalLight = light
        // from class: com.nothing.ketchum.GlyphManager.4
        // java.lang.Runnable
        val task = Runnable {
            var sum = NO_LIGHT
            var start = NO_LIGHT
            var end = NO_LIGHT
            var length = NO_LIGHT
            if (isReverse) {
                if (finalLight == GLYPH_C) {
                    if (Common.isTargetDevice22111(this@GlyphManager.mDevice)) {
                        sum = progress * 700
                        start = Code_22111.C1_16
                        end = Code_22111.C1_1
                        length = 16
                    }
                    if (Common.isTargetDevice23111(this@GlyphManager.mDevice)) {
                        sum = progress * 1000
                        start = Code_23111.C_24
                        end = Code_23111.C_1
                        length = 24
                    }
                }
                if (finalLight == GLYPH_D) {
                    sum = progress * 400
                    if (Common.isTargetDevice20111(this@GlyphManager.mDevice)) {
                        start = Code_20111.D1_8
                        end = Code_20111.D1_1
                    }
                    if (Common.isTargetDevice22111(this@GlyphManager.mDevice)) {
                        start = Code_22111.D1_8
                        end = Code_22111.D1_1
                    }
                    length = 8
                }
            } else {
                if (finalLight == GLYPH_C) {
                    if (Common.isTargetDevice22111(this@GlyphManager.mDevice)) {
                        sum = progress * 700
                        start = Code_22111.C1_1
                        end = Code_22111.C1_16
                        length = 16
                    }
                    if (Common.isTargetDevice23111(this@GlyphManager.mDevice)) {
                        sum = progress * 1000
                        start = Code_23111.C_1
                        end = Code_23111.C_24
                        length = 24
                    }
                }
                if (finalLight == GLYPH_D) {
                    sum = progress * 400
                    if (Common.isTargetDevice20111(this@GlyphManager.mDevice)) {
                        start = Code_20111.D1_1
                        end = Code_20111.D1_8
                    }
                    if (Common.isTargetDevice22111(this@GlyphManager.mDevice)) {
                        start = Code_22111.D1_1
                        end = Code_22111.D1_8
                    }
                    length = 8
                }
            }
            if (sum == 0) {
                return@Runnable
            }
            val progressLight = sum / DEFAULT_MAX_LIGHT
            val remain = sum % DEFAULT_MAX_LIGHT
            val builder = if (Common.isTargetDevice20111(this@GlyphManager.mDevice)) {
                GlyphFrame.Builder(Common.DEVICE_20111)
            } else if (Common.isTargetDevice22111(this@GlyphManager.mDevice)) {
                GlyphFrame.Builder(Common.DEVICE_22111)
            } else if (Common.isTargetDevice23111(this@GlyphManager.mDevice)) {
                GlyphFrame.Builder(Common.DEVICE_23111)
            } else {
                GlyphFrame.Builder()
            }
            if (isReverse) {
                var i = NO_LIGHT
                while (i < progressLight && i <= length && end <= start) {
                    builder.buildChannel(start)
                    start--
                    i += GLYPH_C
                }
                if (end <= start) {
                    if (remain < DEFAULT_MIN_LIGHT) {
                        builder.buildChannel(start, DEFAULT_MIN_LIGHT)
                    } else {
                        builder.buildChannel(start, remain)
                    }
                }
            } else {
                var i2 = NO_LIGHT
                while (i2 < progressLight && i2 <= length && start <= end) {
                    builder.buildChannel(start)
                    start += GLYPH_C
                    i2 += GLYPH_C
                }
                if (start <= end) {
                    if (remain < DEFAULT_MIN_LIGHT) {
                        builder.buildChannel(start, DEFAULT_MIN_LIGHT)
                    } else {
                        builder.buildChannel(start, remain)
                    }
                }
            }
            if (isToggle) {
                if (Common.isTargetDevice20111(this@GlyphManager.mDevice)) {
                    if (this@GlyphManager.hasLight(channel[Code_20111.A1])) {
                        builder.buildChannelA()
                    }
                    if (this@GlyphManager.hasLight(channel[Code_20111.B1])) {
                        builder.buildChannelB()
                    }
                    if (this@GlyphManager.hasLight(channel[Code_20111.C1])) {
                        builder.buildChannel(Code_20111.C1)
                    }
                    if (this@GlyphManager.hasLight(channel[Code_20111.C2])) {
                        builder.buildChannel(Code_20111.C2)
                    }
                    if (this@GlyphManager.hasLight(channel[Code_20111.C3])) {
                        builder.buildChannel(Code_20111.C3)
                    }
                    if (this@GlyphManager.hasLight(channel[Code_20111.C4])) {
                        builder.buildChannel(Code_20111.C4)
                    }
                    if (this@GlyphManager.hasLight(channel[Code_20111.E1])) {
                        builder.buildChannelE()
                    }
                }
                if (Common.isTargetDevice22111(this@GlyphManager.mDevice)) {
                    if (this@GlyphManager.hasLight(channel[Code_22111.A1])) {
                        builder.buildChannel(Code_22111.A1)
                    }
                    if (this@GlyphManager.hasLight(channel[Code_22111.A2])) {
                        builder.buildChannel(Code_22111.A2)
                    }
                    if (this@GlyphManager.hasLight(channel[Code_22111.B1])) {
                        builder.buildChannelB()
                    }
                    if (this@GlyphManager.hasLight(channel[Code_22111.C2])) {
                        builder.buildChannel(Code_22111.C2)
                    }
                    if (this@GlyphManager.hasLight(channel[Code_22111.C3])) {
                        builder.buildChannel(Code_22111.C3)
                    }
                    if (this@GlyphManager.hasLight(channel[Code_22111.C4])) {
                        builder.buildChannel(Code_22111.C4)
                    }
                    if (this@GlyphManager.hasLight(channel[Code_22111.C5])) {
                        builder.buildChannel(Code_22111.C5)
                    }
                    if (this@GlyphManager.hasLight(channel[Code_22111.C6])) {
                        builder.buildChannel(Code_22111.C6)
                    }
                    if (this@GlyphManager.hasLight(channel[Code_22111.E1])) {
                        builder.buildChannelE()
                    }
                }
                if (Common.isTargetDevice23111(this@GlyphManager.mDevice)) {
                    if (this@GlyphManager.hasLight(channel[Code_23111.A])) {
                        builder.buildChannelA()
                    }
                    if (this@GlyphManager.hasLight(channel[Code_23111.B])) {
                        builder.buildChannelB()
                    }
                }
            }
            try {
                mService!!.setFrameColors(builder.build().channel)
            } catch (e: RemoteException) {
                Log.e(TAG, e.message!!)
            }
        }
        executeFrame(task)
    }

    private fun executeFrame(task: Runnable) {
        this.mTask = mExecutor.submit(task)
    }

    private fun hasLight(light: Int): Boolean {
        return light > 0
    }

    private fun stopCurrentTask() {
        if (this.mTask != null) {
            mTask!!.cancel(true)
        }
    }

    private fun pauseAWhile(time: Long) {
        try {
            Thread.sleep(time)
        } catch (e: InterruptedException) {
            Log.e(TAG, e.message!!)
        }
    }

    /* loaded from: KetchumSDK_Community_20240307.jar:com/nothing/ketchum/GlyphManager$RemoteServiceConnection.class */
    private inner class RemoteServiceConnection : ServiceConnection {
        // android.content.ServiceConnection
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "Service connected")
            this@GlyphManager.mService = IGlyphService.Stub.asInterface(service)
            if (this@GlyphManager.mCallback != null) {
                mCallback!!.onServiceConnected(name)
            }
        }

        // android.content.ServiceConnection
        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "Service disconnected")
            mService = null
            if (mCallback != null) {
                mCallback!!.onServiceDisconnected(name)
            }
        }
    }

    companion object {
        private const val TAG = "GlyphManager"
        private const val GLYPH_C = 1
        private const val GLYPH_D = 2
        private const val DEFAULT_MAX_LIGHT = 4096
        private const val DEFAULT_MIN_LIGHT = 800
        private const val NO_LIGHT = 0
        private var mInstance: GlyphManager? = null
        fun getInstance(context: Context): GlyphManager {
            if (mInstance == null) {
                mInstance = GlyphManager(context)
            }
            return mInstance!!
        }
    }
}
