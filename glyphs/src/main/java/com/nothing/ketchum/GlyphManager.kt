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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/* loaded from: KetchumSDK_Community_20240307.jar:com/nothing/ketchum/GlyphManager.class */
class GlyphManager {
    private var mService: IGlyphService? = null
    private var mCallback: GlyphCallback? = null
    private val mConnection = RemoteServiceConnection()
    private var mHasAuthorized = false
    private val isTaskOn = false
    private var job: Job? = null
    private var mDevice: String? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun init(mContext: Context, callback: GlyphCallback?) {
        mCallback = callback
        val launchService = Intent().apply {
            action = "com.nothing.thirdparty.bind_glyphservice"
            `package` = "com.nothing.thirdparty"
            component = ComponentName(
                "com.nothing.thirdparty",
                "com.nothing.thirdparty.GlyphService"
            )
        }

        mContext.bindService(launchService, mConnection, Context.BIND_AUTO_CREATE)
    }

    fun unInit(mContext: Context) {
        mContext.unbindService(mConnection)
    }

    fun register(mContext: Context): Boolean {
        try {
            mDevice = Common.DEVICE_22111
            val key = Common.getAppKey(mContext)
            mHasAuthorized = mService!!.registerSDK(key, Common.DEVICE_22111)
        } catch (e: RemoteException) {
            Log.e(TAG, e.message!!)
        } catch (e2: Exception) {
            Log.e(TAG, e2.message, e2)
        }
        Log.w(TAG, "You are targeting " + Common.DEVICE_22111 + " as your device.")
        return mHasAuthorized
    }

    fun register(mContext: Context, targetDevice: String): Boolean {
        try {
            mDevice = targetDevice
            val key = Common.getAppKey(mContext)
            mHasAuthorized = mService!!.registerSDK(key, targetDevice)
        } catch (e: RemoteException) {
            Log.e(TAG, e.message!!)
        } catch (e2: Exception) {
            Log.e(TAG, e2.message, e2)
        }
        Log.w(TAG, "You are targeting $targetDevice as your device.")
        return mHasAuthorized
    }

    val glyphFrameBuilder: GlyphFrame.Builder
        get() {
            if (mDevice == null) {
                return GlyphFrame.Builder()
            }
            return GlyphFrame.Builder(mDevice)
        }

    @Throws(GlyphException::class)
    fun openSession() {
        if (!mHasAuthorized) {
            Log.d(TAG, "Non registed")
        } else {
            if (mService == null) {
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
        if (!mHasAuthorized) {
            Log.d(TAG, "Non registed")
            return
        }
        if (mService == null) {
            throw GlyphException("Please use it after service connected.")
        }
        try {
            runBlocking { stopCurrentTask() }
            mService!!.closeSession()
        } catch (e: RemoteException) {
            Log.d(TAG, e.message!!)
        }
    }

    @Throws(GlyphException::class)
    fun setFrameColors(colors: IntArray?) {
        if (!mHasAuthorized) {
            Log.d(TAG, "Non registed")
            return
        }
        if (mService == null) {
            throw GlyphException("Please use it after service connected.")
        }
        try {
            runBlocking { stopCurrentTask() }
            mService!!.setFrameColors(colors)
        } catch (e: RemoteException) {
            Log.e(TAG, e.message!!)
        }
    }

    suspend fun turnOff() {
        if (!mHasAuthorized) {
            Log.d(TAG, "Non registed")
            return
        }
        stopCurrentTask()
        // from class: com.nothing.ketchum.GlyphManager.1
        // java.lang.Runnable
        executeFrame {
            try {
                val builder = glyphFrameBuilder
                val emptyFrame = builder.build()
                mService!!.setFrameColors(emptyFrame.channel)
            } catch (e: RemoteException) {
                Log.e(TAG, e.message!!)
            }
        }
    }

    suspend fun toggle(frame: GlyphFrame) {
        if (!mHasAuthorized) {
            Log.d(TAG, "Non registed")
            return
        }
        stopCurrentTask()
        // from class: com.nothing.ketchum.GlyphManager.2
        // java.lang.Runnable
        executeFrame {
            try {
                mService!!.setFrameColors(frame.channel)
            } catch (e: RemoteException) {
                Log.e(TAG, e.message!!)
            }
        }
    }

    suspend fun animate(frame: GlyphFrame) {
        if (!mHasAuthorized) {
            Log.d(TAG, "Non registed")
            return
        }
        stopCurrentTask()
        // from class: com.nothing.ketchum.GlyphManager.3
        // java.lang.Runnable
        executeFrame {
            var light: Int
            val period = frame.period
            val interval = frame.interval
            val emptyFrame = GlyphFrame.Builder().build()
            repeat(frame.cycles) {
                val startOn = System.currentTimeMillis()
                while (System.currentTimeMillis() - startOn < period) {
                    try {
                        val colors = frame.channel
                        light = if (System.currentTimeMillis() - startOn < period / GLYPH_D) {
                            NO_LIGHT + ((DEFAULT_MAX_LIGHT / (period / GLYPH_D)) * ((System.currentTimeMillis() - startOn).toInt()))
                        } else {
                            DEFAULT_MAX_LIGHT - ((DEFAULT_MAX_LIGHT / (period / GLYPH_D)) * (((System.currentTimeMillis() - startOn) - (period / GLYPH_D)).toInt()))
                        }
                        for (i in colors.indices) {
                            if (colors[i] != 0) {
                                colors[i] = light
                            }
                        }
                        mService!!.setFrameColors(colors)
                    } catch (e: RemoteException) {
                        Log.e(TAG, e.message!!)
                    }
                    delay(10L)
                }
                val startOff = System.currentTimeMillis()
                while (System.currentTimeMillis() - startOff < interval) {
                    try {
                        mService!!.setFrameColors(emptyFrame.channel)
                    } catch (e2: RemoteException) {
                        Log.e(TAG, e2.message!!)
                    }
                    delay(100L)
                }
            }
        }
    }

    @Throws(GlyphException::class)
    suspend fun displayProgressAndToggle(frame: GlyphFrame, progress: Int, isReverse: Boolean) {
        displayProgress(frame, progress, isReverse, true)
    }

    @Throws(GlyphException::class)
    suspend fun displayProgress(frame: GlyphFrame, progress: Int) {
        displayProgress(frame, progress, false, false)
    }

    @Throws(GlyphException::class)
    suspend fun displayProgress(frame: GlyphFrame, progress: Int, isReverse: Boolean) {
        displayProgress(frame, progress, isReverse, false)
    }

    @Throws(GlyphException::class)
    private suspend fun displayProgress(
        frame: GlyphFrame,
        progress: Int,
        isReverse: Boolean,
        isToggle: Boolean,
    ) {
        if (!mHasAuthorized) {
            Log.d(TAG, "Non registed")
            return
        }
        stopCurrentTask()
        val channel = frame.channel
        var light = NO_LIGHT
        if (Common.isTargetDevice20111(mDevice)) {
            if (channel[Code_20111.D1_1] == 0) {
                throw GlyphException("Please choose D1_1 while using display progress in 20111.")
            }
            light = GLYPH_D
        } else if (Common.isTargetDevice22111(mDevice)) {
            if ((channel[Code_22111.C1_1] == 0 && channel[Code_22111.D1_1] == 0) || (channel[Code_22111.C1_1] > 0 && channel[Code_22111.D1_1] > 0)) {
                throw GlyphException("Please choose C1 or D1 while using display progress in 22111")
            }
            light = if (channel[Code_22111.C1_1] == 0) GLYPH_D else GLYPH_C
        } else if (Common.isTargetDevice23111(mDevice)) {
            if (channel[Code_23111.C_1] == 0) {
                throw GlyphException("Please choose C_1 while using display progress in 23111.")
            }
            light = GLYPH_C
        }
        // from class: com.nothing.ketchum.GlyphManager.4
        // java.lang.Runnable
        executeFrame {
            var sum = NO_LIGHT
            var start = NO_LIGHT
            var end = NO_LIGHT
            var length = NO_LIGHT
            if (isReverse) {
                if (light == GLYPH_C) {
                    if (Common.isTargetDevice22111(mDevice)) {
                        sum = progress * 700
                        start = Code_22111.C1_16
                        end = Code_22111.C1_1
                        length = 16
                    } else if (Common.isTargetDevice23111(mDevice)) {
                        sum = progress * 1000
                        start = Code_23111.C_24
                        end = Code_23111.C_1
                        length = 24
                    }
                } else if (light == GLYPH_D) {
                    sum = progress * 400
                    if (Common.isTargetDevice20111(mDevice)) {
                        start = Code_20111.D1_8
                        end = Code_20111.D1_1
                    } else if (Common.isTargetDevice22111(mDevice)) {
                        start = Code_22111.D1_8
                        end = Code_22111.D1_1
                    }
                    length = 8
                }
            } else {
                if (light == GLYPH_C) {
                    if (Common.isTargetDevice22111(mDevice)) {
                        sum = progress * 700
                        start = Code_22111.C1_1
                        end = Code_22111.C1_16
                        length = 16
                    } else if (Common.isTargetDevice23111(mDevice)) {
                        sum = progress * 1000
                        start = Code_23111.C_1
                        end = Code_23111.C_24
                        length = 24
                    }
                } else if (light == GLYPH_D) {
                    sum = progress * 400
                    if (Common.isTargetDevice20111(mDevice)) {
                        start = Code_20111.D1_1
                        end = Code_20111.D1_8
                    } else if (Common.isTargetDevice22111(mDevice)) {
                        start = Code_22111.D1_1
                        end = Code_22111.D1_8
                    }
                    length = 8
                }
            }
            if (sum == 0) {
                return@executeFrame
            }
            val progressLight = sum / DEFAULT_MAX_LIGHT
            val remain = sum % DEFAULT_MAX_LIGHT
            val builder = if (Common.isTargetDevice20111(mDevice)) {
                GlyphFrame.Builder(Common.DEVICE_20111)
            } else if (Common.isTargetDevice22111(mDevice)) {
                GlyphFrame.Builder(Common.DEVICE_22111)
            } else if (Common.isTargetDevice23111(mDevice)) {
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
                    builder.buildChannel(start, remain.coerceAtLeast(DEFAULT_MIN_LIGHT))
                }
            } else {
                var i2 = NO_LIGHT
                while (i2 < progressLight && i2 <= length && start <= end) {
                    builder.buildChannel(start)
                    start += GLYPH_C
                    i2 += GLYPH_C
                }
                if (start <= end) {
                    builder.buildChannel(start, remain.coerceAtLeast(DEFAULT_MIN_LIGHT))
                }
            }
            if (isToggle) {
                if (Common.isTargetDevice20111(mDevice)) {
                    if (hasLight(channel[Code_20111.A1])) {
                        builder.buildChannelA()
                    }
                    if (hasLight(channel[Code_20111.B1])) {
                        builder.buildChannelB()
                    }
                    if (hasLight(channel[Code_20111.C1])) {
                        builder.buildChannel(Code_20111.C1)
                    }
                    if (hasLight(channel[Code_20111.C2])) {
                        builder.buildChannel(Code_20111.C2)
                    }
                    if (hasLight(channel[Code_20111.C3])) {
                        builder.buildChannel(Code_20111.C3)
                    }
                    if (hasLight(channel[Code_20111.C4])) {
                        builder.buildChannel(Code_20111.C4)
                    }
                    if (hasLight(channel[Code_20111.E1])) {
                        builder.buildChannelE()
                    }
                } else if (Common.isTargetDevice22111(mDevice)) {
                    if (hasLight(channel[Code_22111.A1])) {
                        builder.buildChannel(Code_22111.A1)
                    }
                    if (hasLight(channel[Code_22111.A2])) {
                        builder.buildChannel(Code_22111.A2)
                    }
                    if (hasLight(channel[Code_22111.B1])) {
                        builder.buildChannelB()
                    }
                    if (hasLight(channel[Code_22111.C2])) {
                        builder.buildChannel(Code_22111.C2)
                    }
                    if (hasLight(channel[Code_22111.C3])) {
                        builder.buildChannel(Code_22111.C3)
                    }
                    if (hasLight(channel[Code_22111.C4])) {
                        builder.buildChannel(Code_22111.C4)
                    }
                    if (hasLight(channel[Code_22111.C5])) {
                        builder.buildChannel(Code_22111.C5)
                    }
                    if (hasLight(channel[Code_22111.C6])) {
                        builder.buildChannel(Code_22111.C6)
                    }
                    if (hasLight(channel[Code_22111.E1])) {
                        builder.buildChannelE()
                    }
                } else if (Common.isTargetDevice23111(mDevice)) {
                    if (hasLight(channel[Code_23111.A])) {
                        builder.buildChannelA()
                    }
                    if (hasLight(channel[Code_23111.B])) {
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
    }

    private suspend fun executeFrame(task: suspend () -> Unit) {
        job = scope.launch {
            task()
        }
        job?.join()
    }

    private fun hasLight(light: Int): Boolean {
        return light > 0
    }

    private suspend fun stopCurrentTask() {
        job?.cancelAndJoin()
    }

    /* loaded from: KetchumSDK_Community_20240307.jar:com/nothing/ketchum/GlyphManager$RemoteServiceConnection.class */
    private inner class RemoteServiceConnection : ServiceConnection {
        // android.content.ServiceConnection
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "Service connected")
            mService = IGlyphService.Stub.asInterface(service)
            mCallback?.onServiceConnected(name)
        }

        // android.content.ServiceConnection
        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "Service disconnected")
            mService = null
            mCallback?.onServiceDisconnected(name)
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
        fun getInstance(): GlyphManager {
            if (mInstance == null) {
                mInstance = GlyphManager()
            }
            return mInstance!!
        }
    }
}
