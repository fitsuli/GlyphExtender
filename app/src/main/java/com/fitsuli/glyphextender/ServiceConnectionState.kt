package com.fitsuli.glyphextender

/**
 * Created by Dmitry Danilyuk at 17.08.2024
 */
sealed class ServiceConnectionState {
    data object NotConnected : ServiceConnectionState()
    data object Connecting : ServiceConnectionState()
    data object Connected : ServiceConnectionState()
}
