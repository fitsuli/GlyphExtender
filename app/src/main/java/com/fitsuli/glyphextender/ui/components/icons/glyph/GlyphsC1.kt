package com.fitsuli.glyphextender.ui.components.icons.glyph

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GlyphSymbols.GlyphsC1: ImageVector
    get() {
        if (_GlyphsC1 != null) {
            return _GlyphsC1!!
        }
        _GlyphsC1 = ImageVector.Builder(
            name = "GlyphsC1",
            defaultWidth = 98.dp,
            defaultHeight = 44.dp,
            viewportWidth = 98f,
            viewportHeight = 44f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F2F2))) {
                moveTo(3.358f, 1.564f)
                curveTo(38.786f, -3.652f, 74.299f, 10.417f, 96.57f, 38.493f)
                curveTo(97.611f, 39.799f, 97.389f, 41.703f, 96.084f, 42.744f)
                curveTo(94.78f, 43.786f, 92.878f, 43.564f, 91.838f, 42.258f)
                curveTo(70.914f, 15.871f, 37.532f, 2.648f, 4.245f, 7.548f)
                curveTo(3.179f, 7.702f, 2.105f, 7.284f, 1.439f, 6.43f)
                curveTo(0.774f, 5.576f, 0.604f, 4.441f, 0.996f, 3.434f)
                curveTo(1.397f, 2.426f, 2.301f, 1.709f, 3.366f, 1.556f)
                lineTo(3.358f, 1.564f)
                close()
            }
        }.build()

        return _GlyphsC1!!
    }

@Suppress("ObjectPropertyName")
private var _GlyphsC1: ImageVector? = null
