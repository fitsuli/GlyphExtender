package com.fitsuli.glyphextender.ui.components.icons.glyph

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GlyphSymbols.GlyphsC4: ImageVector
    get() {
        if (_GlyphsC4 != null) {
            return _GlyphsC4!!
        }
        _GlyphsC4 = ImageVector.Builder(
            name = "GlyphsC4",
            defaultWidth = 98.dp,
            defaultHeight = 44.dp,
            viewportWidth = 98f,
            viewportHeight = 44f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F2F2))) {
                moveTo(6.18f, 1.675f)
                curveTo(27.158f, 28.062f, 60.626f, 41.285f, 93.999f, 36.385f)
                curveTo(95.067f, 36.231f, 96.145f, 36.65f, 96.811f, 37.503f)
                curveTo(97.487f, 38.348f, 97.649f, 39.492f, 97.256f, 40.5f)
                curveTo(96.854f, 41.507f, 95.948f, 42.224f, 94.879f, 42.378f)
                curveTo(59.36f, 47.593f, 23.756f, 33.525f, 1.428f, 5.448f)
                curveTo(0.752f, 4.603f, 0.581f, 3.459f, 0.983f, 2.46f)
                curveTo(1.385f, 1.453f, 2.282f, 0.735f, 3.359f, 0.582f)
                curveTo(4.428f, 0.42f, 5.505f, 0.846f, 6.18f, 1.692f)
                verticalLineTo(1.675f)
                close()
            }
        }.build()

        return _GlyphsC4!!
    }

@Suppress("ObjectPropertyName")
private var _GlyphsC4: ImageVector? = null
