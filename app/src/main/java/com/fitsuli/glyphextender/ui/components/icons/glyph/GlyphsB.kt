package com.fitsuli.glyphextender.ui.components.icons.glyph

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GlyphSymbols.GlyphsB: ImageVector
    get() {
        if (_GlyphsB != null) {
            return _GlyphsB!!
        }
        _GlyphsB = ImageVector.Builder(
            name = "GlyphsB",
            defaultWidth = 40.dp,
            defaultHeight = 46.dp,
            viewportWidth = 40f,
            viewportHeight = 46f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F2F2))) {
                moveTo(34.588f, 1.15f)
                curveTo(35.665f, -0.131f, 37.572f, -0.293f, 38.854f, 0.774f)
                curveTo(40.136f, 1.841f, 40.299f, 3.753f, 39.23f, 5.034f)
                lineTo(6.267f, 44.26f)
                curveTo(5.754f, 44.875f, 5.011f, 45.259f, 4.207f, 45.327f)
                curveTo(3.403f, 45.395f, 2.608f, 45.148f, 1.993f, 44.627f)
                lineTo(0.6f, 43.458f)
                curveTo(0.352f, 43.253f, 0.198f, 42.954f, 0.172f, 42.638f)
                curveTo(0.146f, 42.322f, 0.24f, 41.998f, 0.454f, 41.759f)
                lineTo(34.58f, 1.141f)
                lineTo(34.588f, 1.15f)
                close()
            }
        }.build()

        return _GlyphsB!!
    }

@Suppress("ObjectPropertyName")
private var _GlyphsB: ImageVector? = null
