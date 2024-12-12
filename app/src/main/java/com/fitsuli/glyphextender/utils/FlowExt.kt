package com.fitsuli.glyphextender.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

fun <T> Flow<T>.stateInWhileSubscribed(
    scope: CoroutineScope,
    initialValue: T,
    stopTimeoutMillis: Long = 5000,
): StateFlow<T> = stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
    initialValue = initialValue
)
