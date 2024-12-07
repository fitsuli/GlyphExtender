package com.fitsuli.glyphextender.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun Context.observeScreenState() = callbackFlow {
    val filter = IntentFilter().apply {
        addAction(Intent.ACTION_SCREEN_ON)
        addAction(Intent.ACTION_SCREEN_OFF)
    }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> trySend(true)
                Intent.ACTION_SCREEN_OFF -> trySend(false)
            }
        }
    }

    registerReceiver(receiver, filter)
    trySend(true) // Initial state - assume screen is on

    awaitClose {
        unregisterReceiver(receiver)
    }
}
