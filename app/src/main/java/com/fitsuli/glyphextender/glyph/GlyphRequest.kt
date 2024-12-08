package com.fitsuli.glyphextender.glyph

sealed class GlyphRequest(
    open val id: String,
    open val priority: Int,
    open val segment: GlyphSegment,
) {
    data class Static(
        override val id: String,
        override val priority: Int,
        override val segment: GlyphSegment,
        val brightness: Int = 4095,
    ) : GlyphRequest(id, priority, segment)

    data class Progress(
        override val id: String,
        override val priority: Int,
        override val segment: GlyphSegment = GlyphSegment.Progress,
        val progress: Int,
    ) : GlyphRequest(id, priority, segment)
}
