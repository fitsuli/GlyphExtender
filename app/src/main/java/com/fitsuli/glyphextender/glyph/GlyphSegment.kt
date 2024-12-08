package com.fitsuli.glyphextender.glyph

import com.nothing.ketchum.Common
import com.nothing.ketchum.Glyph
import com.nothing.ketchum.Glyph.Code_20111
import com.nothing.ketchum.Glyph.Code_22111
import com.nothing.ketchum.Glyph.Code_23111

sealed class GlyphSegment {
    object Progress : GlyphSegment()
    object Camera : GlyphSegment()
    object Notification : GlyphSegment()
    object Pause : GlyphSegment()

    private var cachedSegments: List<Int>? = null

    fun getSegments(): List<Int> {
        if (cachedSegments == null) {
            cachedSegments = when (this) {
                Progress -> Glyph.getProgressSegments()
                Camera -> when {
                    Common.is20111() -> listOf(Code_20111.A1)
                    Common.is22111() -> listOf(Code_22111.A1)
                    Common.is23111() -> listOf(Code_23111.A)
                    else -> emptyList()
                }

                Notification -> when {
                    Common.is20111() -> listOf(Code_20111.E1)
                    Common.is22111() -> listOf(Code_22111.E1)
                    Common.is23111() -> listOf(Code_23111.B)
                    else -> emptyList()
                }

                Pause -> when {
                    Common.is20111() -> listOf(Code_20111.C2)
                    Common.is22111() -> listOf(Code_22111.C2)
                    Common.is23111() -> listOf(Code_23111.C_12)
                    else -> emptyList()
                }
            }
        }
        return cachedSegments!!
    }
}
