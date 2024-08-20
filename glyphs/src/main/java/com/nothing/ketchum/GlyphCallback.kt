package com.nothing.ketchum

import android.content.ComponentName

interface GlyphCallback {
    fun onServiceConnected(componentName: ComponentName)

    fun onServiceDisconnected(componentName: ComponentName)
}
