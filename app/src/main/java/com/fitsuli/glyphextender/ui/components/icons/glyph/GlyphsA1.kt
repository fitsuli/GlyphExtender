package com.fitsuli.glyphextender.ui.components.icons.glyph

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GlyphSymbols.GlyphsA1: ImageVector
    get() {
        if (_GlyphsA1 != null) {
            return _GlyphsA1!!
        }
        _GlyphsA1 = ImageVector.Builder(
            name = "GlyphsA1",
            defaultWidth = 47.dp,
            defaultHeight = 49.dp,
            viewportWidth = 47f,
            viewportHeight = 49f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F2F2))) {
                moveTo(7.009f, 47.567f)
                verticalLineTo(22.751f)
                curveTo(7.009f, 13.753f, 14.156f, 6.369f, 23.166f, 6.061f)
                curveTo(32.176f, 5.754f, 39.801f, 12.643f, 40.417f, 21.624f)
                curveTo(40.459f, 22.256f, 40.989f, 22.751f, 41.622f, 22.751f)
                horizontalLineTo(45.264f)
                curveTo(45.597f, 22.751f, 45.913f, 22.614f, 46.144f, 22.375f)
                curveTo(46.375f, 22.136f, 46.494f, 21.812f, 46.477f, 21.479f)
                curveTo(45.776f, 9.177f, 35.416f, -0.333f, 23.089f, 0.009f)
                curveTo(10.762f, 0.359f, 0.948f, 10.432f, 0.948f, 22.751f)
                verticalLineTo(47.567f)
                curveTo(0.948f, 48.233f, 1.487f, 48.779f, 2.162f, 48.779f)
                horizontalLineTo(5.795f)
                curveTo(6.462f, 48.779f, 7.009f, 48.241f, 7.009f, 47.567f)
                close()
            }
        }.build()

        return _GlyphsA1!!
    }

@Suppress("ObjectPropertyName")
private var _GlyphsA1: ImageVector? = null
