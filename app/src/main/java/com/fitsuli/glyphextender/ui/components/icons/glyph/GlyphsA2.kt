package com.fitsuli.glyphextender.ui.components.icons.glyph

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GlyphSymbols.GlyphsA2: ImageVector
    get() {
        if (_GlyphsA2 != null) {
            return _GlyphsA2!!
        }
        _GlyphsA2 = ImageVector.Builder(
            name = "GlyphsA2",
            defaultWidth = 38.dp,
            defaultHeight = 43.dp,
            viewportWidth = 38f,
            viewportHeight = 43f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F2F2))) {
                moveTo(31.451f, 1.769f)
                curveTo(31.451f, 1.103f, 31.989f, 0.557f, 32.665f, 0.557f)
                horizontalLineTo(36.298f)
                curveTo(36.965f, 0.557f, 37.512f, 1.095f, 37.512f, 1.769f)
                verticalLineTo(19.372f)
                curveTo(37.512f, 27.926f, 32.707f, 35.763f, 25.074f, 39.638f)
                curveTo(17.44f, 43.522f, 8.267f, 42.797f, 1.343f, 37.76f)
                curveTo(1.069f, 37.564f, 0.899f, 37.265f, 0.856f, 36.941f)
                curveTo(0.813f, 36.616f, 0.907f, 36.275f, 1.121f, 36.019f)
                lineTo(3.429f, 33.202f)
                curveTo(3.831f, 32.706f, 4.549f, 32.612f, 5.07f, 32.98f)
                curveTo(10.174f, 36.599f, 16.867f, 37.069f, 22.424f, 34.2f)
                curveTo(27.98f, 31.332f, 31.468f, 25.612f, 31.468f, 19.364f)
                verticalLineTo(1.761f)
                lineTo(31.451f, 1.769f)
                close()
            }
        }.build()

        return _GlyphsA2!!
    }

@Suppress("ObjectPropertyName")
private var _GlyphsA2: ImageVector? = null
