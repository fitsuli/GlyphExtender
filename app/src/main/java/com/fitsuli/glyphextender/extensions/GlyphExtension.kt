package com.fitsuli.glyphextender.extensions

import com.nothing.ketchum.Common
import com.nothing.ketchum.Glyph.Code_22111
import com.nothing.ketchum.GlyphFrame
import com.nothing.ketchum.GlyphManager
import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.math.abs

/**
 * Created by Dmitry Danilyuk at 18.07.2024
 */
private var lastProgress = 0
private var isAnimating = false

suspend fun GlyphManager.animateProgressBackToStop(frame: GlyphFrame, from: Int) {
    animateProgress(frame, from, 0)
}

suspend fun GlyphManager.animateProgressForward(frame: GlyphFrame, to: Int) {
    animateProgress(frame, lastProgress, to)
}

private const val HIGH_STEP = 10
private const val LOW_STEP = 5

suspend fun GlyphManager.animateProgress(
    frame: GlyphFrame,
    from: Int = lastProgress,
    to: Int,
    slower: Boolean = false,
) {
    if (from == to || from < 0 || to < 0 || isAnimating) return
    if (abs(from - to) < HIGH_STEP) {
        displayProgress(frame, to)
        lastProgress = to
        return
    }

    isAnimating = true
    val step = if (slower) LOW_STEP else HIGH_STEP
    val range = if (from < to) from..to else from downTo to
    for (index in range step step) {
        displayProgress(frame, index)
        Timber.d("Media playback progress animate in $range: $index")
        delay(10)
    }
    if (to == 0) {
        Timber.d("Media playback progress animate in $range: turnOff")
        turnOff()
    }
    isAnimating = false
    lastProgress = to
}

suspend fun GlyphManager.showPauseBlink() {
    Timber.d("Media playback show Pause blink!")
    /*
        val frame = glyphFrameBuilder.run {
            if (Common.is22111()) {
                buildChannel(Code_22111.C2)
            } else this
        }
            .buildCycles(1)
            .buildInterval(750)
            .buildPeriod(1000)
            .build()
    */

    val frame = glyphFrameBuilder.run {
        if (Common.is22111()) {
            buildChannel(Code_22111.C2)
        } else this
    }.build()

    toggle(frame)
}
