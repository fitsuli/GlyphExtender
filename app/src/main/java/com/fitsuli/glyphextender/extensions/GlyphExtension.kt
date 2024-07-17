package com.fitsuli.glyphextender.extensions

import com.nothing.ketchum.GlyphFrame
import com.nothing.ketchum.GlyphManager
import kotlinx.coroutines.delay
import kotlin.text.Typography.times

/**
 * Created by Dmitry Danilyuk at 18.07.2024
 */
suspend fun GlyphManager.animateProgressBack(frame: GlyphFrame, from: Int) {
    for (index in from downTo  0 step 10) {
        displayProgress(frame, index)
        delay(10)
    }
    turnOff()
}
