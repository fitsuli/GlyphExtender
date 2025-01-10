package com.fitsuli.glyphextender.ui.components.icons.glyph

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val GlyphSymbols.GlyphsC6: ImageVector
    get() {
        if (_GlyphsC6 != null) {
            return _GlyphsC6!!
        }
        _GlyphsC6 = ImageVector.Builder(
            name = "GlyphsC6",
            defaultWidth = 7.dp,
            defaultHeight = 39.dp,
            viewportWidth = 7f,
            viewportHeight = 39f
        ).apply {
            path(fill = SolidColor(Color(0xFFF0F2F2))) {
                moveTo(3.446f, 0.968f)
                curveTo(1.771f, 0.968f, 0.42f, 2.325f, 0.42f, 3.99f)
                verticalLineTo(35.157f)
                curveTo(0.42f, 36.831f, 1.779f, 38.179f, 3.446f, 38.179f)
                curveTo(5.113f, 38.179f, 6.472f, 36.822f, 6.472f, 35.157f)
                verticalLineTo(3.99f)
                curveTo(6.472f, 2.317f, 5.113f, 0.968f, 3.446f, 0.968f)
                close()
            }
        }.build()

        return _GlyphsC6!!
    }

@Suppress("ObjectPropertyName")
private var _GlyphsC6: ImageVector? = null
