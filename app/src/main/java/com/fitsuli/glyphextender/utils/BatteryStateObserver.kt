package com.fitsuli.glyphextender.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

data class BatteryStatus(
    val level: Int,
    val isCharging: Boolean,
)

fun Context.observeBatteryStatus() = callbackFlow {
    val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != Intent.ACTION_BATTERY_CHANGED) return

            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

            val batteryPct = (level * 100 / scale.coerceAtLeast(1)).coerceIn(0, 100)
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

            trySend(BatteryStatus(batteryPct, isCharging))
        }
    }

    registerReceiver(receiver, filter)

    awaitClose {
        unregisterReceiver(receiver)
    }
}
