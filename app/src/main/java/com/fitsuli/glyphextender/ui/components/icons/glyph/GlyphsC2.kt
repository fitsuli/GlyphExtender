package com.fitsuli.glyphextender.ui.components.icons.glyph

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GlyphSymbols.GlyphsC2: ImageVector
    get() {
        if (_GlyphsC2 != null) {
            return _GlyphsC2!!
        }
        _GlyphsC2 = ImageVector.Builder(
            name = "GlyphsC2",
            defaultWidth = 43.dp,
            defaultHeight = 35.dp,
            viewportWidth = 43f,
            viewportHeight = 35f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F2F2))) {
                moveTo(38.477f, 0.557f)
                curveTo(39.46f, 0.105f, 40.614f, 0.216f, 41.494f, 0.847f)
                curveTo(42.375f, 1.47f, 42.853f, 2.521f, 42.751f, 3.596f)
                curveTo(42.648f, 4.672f, 41.981f, 5.611f, 40.99f, 6.063f)
                curveTo(27.398f, 12.252f, 15.464f, 21.574f, 6.18f, 33.261f)
                curveTo(5.505f, 34.106f, 4.437f, 34.533f, 3.359f, 34.371f)
                curveTo(2.291f, 34.209f, 1.385f, 33.492f, 0.983f, 32.493f)
                curveTo(0.581f, 31.486f, 0.752f, 30.35f, 1.428f, 29.505f)
                curveTo(11.309f, 17.067f, 24.004f, 7.156f, 38.468f, 0.566f)
                lineTo(38.477f, 0.557f)
                close()
            }
        }.build()

        return _GlyphsC2!!
    }

@Suppress("ObjectPropertyName")
private var _GlyphsC2: ImageVector? = null
