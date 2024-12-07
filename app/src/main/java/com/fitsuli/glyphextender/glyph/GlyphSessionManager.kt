package com.fitsuli.glyphextender.glyph

import android.content.ComponentName
import android.content.Context
import com.fitsuli.glyphextender.ServiceConnectionState
import com.nothing.ketchum.Common
import com.nothing.ketchum.GlyphCallback
import com.nothing.ketchum.GlyphManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update

class GlyphSessionManager(
    private val context: Context,
    private val glyphManager: GlyphManager = GlyphManager.getInstance(),
) {
    private val serviceConnectionState = MutableStateFlow<ServiceConnectionState>(
        ServiceConnectionState.NotConnected
    )
    private val callback = object : GlyphCallback {
        override fun onServiceConnected(componentName: ComponentName) {
            serviceConnectionState.update { ServiceConnectionState.Connected }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            serviceConnectionState.update { ServiceConnectionState.NotConnected }
        }
    }

    suspend fun connectAndAwait() {
        if (serviceConnectionState.value is ServiceConnectionState.Connected) {
            return
        }

        serviceConnectionState
            .onEach { connectServiceWhenNotConnected() }
            .mapNotNull { it as? ServiceConnectionState.Connected }
            .take(1)
            .onEach { initializeGlyphManager() }
            .first()
    }

    private fun connectServiceWhenNotConnected() {
        if (serviceConnectionState.compareAndSet(
                ServiceConnectionState.NotConnected,
                ServiceConnectionState.Connecting
            )
        ) {
            glyphManager.init(context, callback)
        }
    }

    private fun initializeGlyphManager() {
        when {
            Common.is20111() -> glyphManager.register(context, Common.DEVICE_20111)
            Common.is22111() -> glyphManager.register(context, Common.DEVICE_22111)
            Common.is23111() -> glyphManager.register(context, Common.DEVICE_23111)
        }
        glyphManager.openSession()
    }

    suspend fun disconnect() {
        glyphManager.turnOff()
        glyphManager.closeSession()
        if (serviceConnectionState.compareAndSet(
                ServiceConnectionState.Connected,
                ServiceConnectionState.NotConnected
            )
        ) {
            glyphManager.unInit(context)
        }
    }
}
