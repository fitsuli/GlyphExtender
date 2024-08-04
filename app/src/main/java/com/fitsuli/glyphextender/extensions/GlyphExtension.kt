package com.fitsuli.glyphextender.extensions

import com.nothing.ketchum.Common
import com.nothing.ketchum.Glyph.Code_22111
import com.nothing.ketchum.GlyphFrame
import com.nothing.ketchum.GlyphManager
import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * Created by Dmitry Danilyuk at 18.07.2024
 */
suspend fun GlyphManager.animateProgressBackToStop(frame: GlyphFrame, from: Int) {
    for (index in from downTo 0 step 10) {
        displayProgress(frame, index)
        Timber.d("Media playback progress animate back to stop: $index")
        delay(10)
    }
    turnOff()
}

suspend fun GlyphManager.animateProgressForward(frame: GlyphFrame, to: Int) {
    for (index in 0..to step 10) {
        displayProgress(frame, index)
        Timber.d("Media playback progress animate forward: $index")
        delay(10)
    }
}

suspend fun GlyphManager.showPauseBlink() {
    Timber.d("Media playback show Pause blink!")
    val frame = glyphFrameBuilder.run {
        if (Common.is22111()) {
            buildChannel(Code_22111.C2)
        } else this
    }
        .buildCycles(1)
        .buildInterval(750)
        .buildPeriod(1000)
        .build()

    animate(frame)
}
