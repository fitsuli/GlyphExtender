package com.fitsuli.glyphextender.ui.components.icons.glyph

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GlyphSymbols.GlyphsC3: ImageVector
    get() {
        if (_GlyphsC3 != null) {
            return _GlyphsC3!!
        }
        _GlyphsC3 = ImageVector.Builder(
            name = "GlyphsC3",
            defaultWidth = 7.dp,
            defaultHeight = 48.dp,
            viewportWidth = 7f,
            viewportHeight = 48f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F2F2))) {
                moveTo(6.052f, 44.373f)
                curveTo(6.052f, 46.046f, 4.693f, 47.395f, 3.026f, 47.395f)
                curveTo(1.359f, 47.395f, 0f, 46.037f, 0f, 44.373f)
                verticalLineTo(3.328f)
                curveTo(0f, 2.244f, 0.581f, 1.245f, 1.513f, 0.708f)
                curveTo(2.453f, 0.17f, 3.607f, 0.17f, 4.539f, 0.708f)
                curveTo(5.48f, 1.245f, 6.052f, 2.244f, 6.052f, 3.328f)
                verticalLineTo(44.373f)
                close()
            }
        }.build()

        return _GlyphsC3!!
    }

@Suppress("ObjectPropertyName")
private var _GlyphsC3: ImageVector? = null
