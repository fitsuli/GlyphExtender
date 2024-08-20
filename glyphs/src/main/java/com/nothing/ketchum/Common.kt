package com.nothing.ketchum

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

/* loaded from: KetchumSDK_Community_20240307.jar:com/nothing/ketchum/Common.class */
object Common {
    private const val TAG = "GlyphManager"
    const val sDKVersion: Int = 140101
    private val mVerifyTragetDevice: ArrayList<*> =
        ArrayList<Any?>(mutableListOf<String?>("A063", "A065", "AIN065"))
    const val DEVICE_20111: String = "A063"
    const val DEVICE_22111: String = "A065"
    const val DEVICE_23111: String = "A142"

    fun is20111(): Boolean {
        return Glyph.DEVICE_20111 == Build.MODEL
    }

    fun is22111(): Boolean {
        return Glyph.DEVICE_22111 == Build.MODEL || Glyph.DEVICE_22111I == Build.MODEL
    }

    fun is23111(): Boolean {
        return Glyph.DEVICE_23111 == Build.MODEL
    }

    fun isSDKAvaliable(context: Context): Boolean {
        val appTargetSdkVerion = getAppTargetSDKVersion(context)
        return appTargetSdkVerion < sDKVersion
    }

    fun getAppTargetSDKVersion(context: Context): Int {
        return context.applicationInfo.metaData.getInt("targetNothingSDKVersion")
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    fun getAppKey(context: Context): String? {
        var result: String? = null
        try {
            result = context.packageManager.getApplicationInfo(
                context.packageName,
                128
            ).metaData.getString("NothingKey")
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, e.message!!)
        }
        return result
    }

    fun getTargetDevice(context: Context): String? {
        var result: String? = null
        try {
            result = context.packageManager.getApplicationInfo(
                context.packageName,
                128
            ).metaData.getString("TargetDevice")
            if (!isCorrectTargetDevice(result)) {
                result = null
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, e.message!!)
        }
        return result
    }

    private fun isCorrectTargetDevice(value: String?): Boolean {
        return mVerifyTragetDevice.contains(value)
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    fun getTargetDeviceGlyphChannelSize(targetDevice: String?): Int {
        if (isTargetDevice20111(targetDevice)) {
            return Glyph.DEVICE_20111_SIZE
        }
        if (isTargetDevice22111(targetDevice)) {
            return Glyph.DEVICE_22111_SIZE
        }
        if (isTargetDevice23111(targetDevice)) {
            return Glyph.DEVICE_23111_SIZE
        }
        return Glyph.DEVICE_23111_SIZE
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    fun isTargetDevice20111(targetDevice: String?): Boolean {
        return Glyph.DEVICE_20111 == targetDevice
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    fun isTargetDevice22111(targetDevice: String?): Boolean {
        return Glyph.DEVICE_22111 == targetDevice
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    fun isTargetDevice23111(targetDevice: String?): Boolean {
        return Glyph.DEVICE_23111 == targetDevice
    }
}
