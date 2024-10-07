package com.fitsuli.glyphextender.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Created by Dmitry Danilyuk at 07.10.2024
 */
@Composable
fun LifecycleSubscription(
    onCreate: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onDispose: () -> Unit = {},
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnCreate by rememberUpdatedState(onCreate)
    val currentOnResume by rememberUpdatedState(onResume)
    val currentOnPause by rememberUpdatedState(onPause)
    val currentOnDispose by rememberUpdatedState(onDispose)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> currentOnCreate()
                Lifecycle.Event.ON_RESUME -> currentOnResume()
                Lifecycle.Event.ON_PAUSE -> currentOnPause()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            currentOnDispose()
        }
    }
}
