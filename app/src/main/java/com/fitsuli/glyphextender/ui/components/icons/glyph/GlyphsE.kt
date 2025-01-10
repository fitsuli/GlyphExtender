package com.fitsuli.glyphextender.ui.components.icons.glyph

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GlyphSymbols.GlyphsE: ImageVector
    get() {
        if (_GlyphsE != null) {
            return _GlyphsE!!
        }
        _GlyphsE = ImageVector.Builder(
            name = "GlyphsE",
            defaultWidth = 7.dp,
            defaultHeight = 10.dp,
            viewportWidth = 7f,
            viewportHeight = 10f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F2F2))) {
                moveTo(3.964f, 0.755f)
                curveTo(2.293f, 0.755f, 0.945f, 2.112f, 0.945f, 3.777f)
                verticalLineTo(6.969f)
                curveTo(0.945f, 8.643f, 2.301f, 9.991f, 3.964f, 9.991f)
                curveTo(5.626f, 9.991f, 6.982f, 8.634f, 6.982f, 6.969f)
                verticalLineTo(3.777f)
                curveTo(6.982f, 2.104f, 5.626f, 0.755f, 3.964f, 0.755f)
                close()
            }
        }.build()

        return _GlyphsE!!
    }

@Suppress("ObjectPropertyName")
private var _GlyphsE: ImageVector? = null
