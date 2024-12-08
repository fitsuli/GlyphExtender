package com.fitsuli.glyphextender.glyph

import android.content.ComponentName
import android.content.Context
import com.fitsuli.glyphextender.ServiceConnectionState
import com.nothing.ketchum.Common
import com.nothing.ketchum.GlyphCallback
import com.nothing.ketchum.GlyphManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class GlyphSessionManager(
    private val context: Context,
    private val glyphManager: GlyphManager = GlyphManager.getInstance(),
    private val orchestrator: GlyphOrchestrator,
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var disconnectJob: Job? = null
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

    private var isScreenOn = true

    suspend fun connectAndAwait() {
        if (isScreenOn) {
            // Do not connect when the screen is on, glyphs are not visible
            return
        }

        // Cancel any pending disconnect when connecting
        disconnectJob?.cancel()
        disconnectJob = null

        if (serviceConnectionState.value is ServiceConnectionState.SessionActive) {
            // Already connected and active
            return
        }

        serviceConnectionState
            .onEach { state ->
                when (state) {
                    is ServiceConnectionState.NotConnected -> connectServiceWhenNotConnected()
                    is ServiceConnectionState.Connected -> initializeGlyphManager()
                    else -> Unit
                }
            }
//            .mapNotNull { it as? ServiceConnectionState.SessionActive }
            //.take(1)
            .first { it is ServiceConnectionState.SessionActive }
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
        serviceConnectionState.update { ServiceConnectionState.SessionActive }
    }

    fun disconnect() {
        if (serviceConnectionState.value !is ServiceConnectionState.SessionActive) {
            return
        }

        disconnectJob?.cancel()
        disconnectJob = scope.launch {
            Timber.d("Waiting 5 seconds before disconnecting glyph session")
            delay(5000)
            if (serviceConnectionState.value is ServiceConnectionState.SessionActive) {
                Timber.d("Disconnecting glyph session completely")
                glyphManager.turnOff()
                glyphManager.closeSession()
                if (serviceConnectionState.compareAndSet(
                    ServiceConnectionState.SessionActive,
                    ServiceConnectionState.NotConnected
                    )
                ) {
                    glyphManager.unInit(context)
                }
            }
            disconnectJob = null
        }
    }

    suspend fun notifyScreenStateChanged(isScreenOn: Boolean) {
        this.isScreenOn = isScreenOn
        if (isScreenOn) {
            orchestrator.clearAnimations()
            disconnect()
        }
    }
}
