package com.fitsuli.glyphextender.ui.components.icons.glyph

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GlyphSymbols.GlyphsC5: ImageVector
    get() {
        if (_GlyphsC5 != null) {
            return _GlyphsC5!!
        }
        _GlyphsC5 = ImageVector.Builder(
            name = "GlyphsC5",
            defaultWidth = 43.dp,
            defaultHeight = 35.dp,
            viewportWidth = 43f,
            viewportHeight = 35f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F2F2))) {
                moveTo(42.481f, 2.443f)
                curveTo(42.883f, 3.45f, 42.712f, 4.586f, 42.036f, 5.431f)
                curveTo(32.154f, 17.869f, 19.46f, 27.78f, 4.996f, 34.37f)
                curveTo(4.013f, 34.823f, 2.859f, 34.712f, 1.978f, 34.08f)
                curveTo(1.098f, 33.448f, 0.619f, 32.407f, 0.722f, 31.331f)
                curveTo(0.824f, 30.256f, 1.491f, 29.317f, 2.483f, 28.864f)
                curveTo(16.075f, 22.675f, 28.008f, 13.353f, 37.292f, 1.666f)
                curveTo(37.967f, 0.821f, 39.036f, 0.394f, 40.113f, 0.556f)
                curveTo(41.182f, 0.719f, 42.088f, 1.436f, 42.489f, 2.434f)
                lineTo(42.481f, 2.443f)
                close()
            }
        }.build()

        return _GlyphsC5!!
    }

@Suppress("ObjectPropertyName")
private var _GlyphsC5: ImageVector? = null
