package com.fitsuli.glyphextender.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fitsuli.glyphextender.model.Phone2Glyph
import com.fitsuli.glyphextender.ui.components.icons.glyph.GlyphSymbols
import com.fitsuli.glyphextender.ui.components.icons.glyph.GlyphsA1
import com.fitsuli.glyphextender.ui.components.icons.glyph.GlyphsA2
import com.fitsuli.glyphextender.ui.components.icons.glyph.GlyphsB
import com.fitsuli.glyphextender.ui.components.icons.glyph.GlyphsC1
import com.fitsuli.glyphextender.ui.components.icons.glyph.GlyphsC2
import com.fitsuli.glyphextender.ui.components.icons.glyph.GlyphsC3
import com.fitsuli.glyphextender.ui.components.icons.glyph.GlyphsC4
import com.fitsuli.glyphextender.ui.components.icons.glyph.GlyphsC5
import com.fitsuli.glyphextender.ui.components.icons.glyph.GlyphsC6
import com.fitsuli.glyphextender.ui.components.icons.glyph.GlyphsD
import com.fitsuli.glyphextender.ui.components.icons.glyph.GlyphsE

/**
 * Created by Dmitry Danilyuk at 11.12.2024
 */
@Composable
fun GlyphMockup(onBlockClick: (Phone2Glyph) -> Unit) {
    Box(
        modifier = Modifier
            .background(Color.DarkGray, shape = RoundedCornerShape(48.dp))
            .fillMaxSize()
            .aspectRatio(9 / 20f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, bottom = 32.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier
            ) {
                Column {
                    // A1 - Camera Outer Arc
                    Image(
                        imageVector = GlyphSymbols.GlyphsA1,
                        contentDescription = "Camera Outer Arc",
                        modifier = Modifier
                            .size(90.dp)
                            .clickable { onBlockClick(Phone2Glyph.A1) }
                    )

                    // A2 - Camera Inner Arc
                    Image(
                        imageVector = GlyphSymbols.GlyphsA2,
                        contentDescription = "Camera Inner Arc",
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .size(90.dp)
                            .clickable { onBlockClick(Phone2Glyph.A2) }
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                // B1 - Straight Line (Top-right)

                Image(
                    imageVector = GlyphSymbols.GlyphsB,
                    contentDescription = "Top Right Line",
                    modifier = Modifier
                        .size(90.dp)
                        .clickable { onBlockClick(Phone2Glyph.B) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // C2 - Vertical Light Segment
                Image(
                    imageVector = GlyphSymbols.GlyphsC2,
                    contentDescription = "Upper Left Curve",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onBlockClick(Phone2Glyph.C2) }
                )

                Spacer(modifier = Modifier.width(32.dp))

                // C1_1 to C1_16 - Large Circle Arc
                Image(
                    imageVector = GlyphSymbols.GlyphsC1,
                    contentDescription = "Upper Right Curve",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .weight(2.5f)
                        .clickable { onBlockClick(Phone2Glyph.C1) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // C3 - Vertical Light Segment
            Image(
                imageVector = GlyphSymbols.GlyphsC3,
                contentDescription = "Vertical Line",
                alignment = Alignment.CenterStart,
                modifier = Modifier
                    .size(90.dp)
                    .clickable { onBlockClick(Phone2Glyph.C3) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // C6 - Vertical Light Segment
            Image(
                imageVector = GlyphSymbols.GlyphsC6,
                contentDescription = "Right Line",
                alignment = Alignment.CenterEnd,
                modifier = Modifier
                    .align(Alignment.End)
                    .size(90.dp)
                    .clickable { onBlockClick(Phone2Glyph.C6) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // C4 - Horizontal Light Segment
                Image(
                    imageVector = GlyphSymbols.GlyphsC4,
                    contentDescription = "Lower Right Curve",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .weight(2.5f)
                        .clickable { onBlockClick(Phone2Glyph.C4) }
                )

                Spacer(modifier = Modifier.width(32.dp))

                // C5 - Horizontal Light Segment
                Image(
                    imageVector = GlyphSymbols.GlyphsC5,
                    contentDescription = "Lower Left Curve",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onBlockClick(Phone2Glyph.C5) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // D1_1 to D1_8 - Bottom-left Straight Line
            Image(
                imageVector = GlyphSymbols.GlyphsD,
                contentDescription = "Battery Line",
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { onBlockClick(Phone2Glyph.D) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // E1 - Bottom Center Line
            Image(
                imageVector = GlyphSymbols.GlyphsE,
                contentDescription = "USB Dot",
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { onBlockClick(Phone2Glyph.E) }
            )

        }
    }
}

@Preview
@Composable
fun GlyphMockupPreview() {
    GlyphMockup {}
}
