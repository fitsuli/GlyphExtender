package com.fitsuli.glyphextender.ui.components.icons.glyph

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GlyphSymbols.GlyphsD: ImageVector
    get() {
        if (_GlyphsD != null) {
            return _GlyphsD!!
        }
        _GlyphsD = ImageVector.Builder(
            name = "GlyphsD",
            defaultWidth = 7.dp,
            defaultHeight = 46.dp,
            viewportWidth = 7f,
            viewportHeight = 46f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F2F2))) {
                moveTo(0.945f, 3.207f)
                verticalLineTo(42.603f)
                curveTo(0.945f, 44.276f, 2.301f, 45.625f, 3.964f, 45.625f)
                curveTo(5.626f, 45.625f, 6.982f, 44.267f, 6.982f, 42.603f)
                verticalLineTo(3.207f)
                curveTo(6.982f, 2.123f, 6.402f, 1.124f, 5.473f, 0.586f)
                curveTo(4.535f, 0.048f, 3.384f, 0.048f, 2.455f, 0.586f)
                curveTo(1.517f, 1.124f, 0.945f, 2.123f, 0.945f, 3.207f)
                close()
            }
        }.build()

        return _GlyphsD!!
    }

@Suppress("ObjectPropertyName")
private var _GlyphsD: ImageVector? = null
