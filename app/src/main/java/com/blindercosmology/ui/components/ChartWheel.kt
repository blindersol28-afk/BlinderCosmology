package com.blindercosmology.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blindercosmology.astro.AspectType
import com.blindercosmology.astro.Body
import com.blindercosmology.astro.Element
import com.blindercosmology.astro.NatalChart
import com.blindercosmology.astro.Sign
import com.blindercosmology.ui.theme.AirCol
import com.blindercosmology.ui.theme.Bone
import com.blindercosmology.ui.theme.Brass
import com.blindercosmology.ui.theme.EarthCol
import com.blindercosmology.ui.theme.FireCol
import com.blindercosmology.ui.theme.InkBlack
import com.blindercosmology.ui.theme.Sepia
import com.blindercosmology.ui.theme.WaterCol
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ChartWheel(chart: NatalChart, modifier: Modifier = Modifier) {
    val measurer = rememberTextMeasurer()
    Canvas(modifier = modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .padding(8.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val outerR = min(size.width, size.height) / 2f * 0.96f
        val zodiacInnerR = outerR * 0.88f
        val houseRingR   = outerR * 0.62f
        val planetR      = outerR * 0.75f
        val aspectR      = outerR * 0.58f

        // Chart is drawn with the Ascendant on the left at 9 o'clock,
        // increasing zodiacal longitude going counter-clockwise (astrological convention).
        val asc = chart.ascendant
        fun lonToAngleDeg(lon: Double): Double = (180.0 - (lon - asc))

        // --- Outer parchment ring ---
        drawCircle(color = InkBlack, radius = outerR, center = center)
        drawCircle(color = Sepia, radius = outerR, center = center, style = Stroke(width = 2f))

        // --- Zodiac slices ---
        // Compose drawArc: 0° = 3 o'clock, positive sweep = clockwise (y axis down).
        // My math convention: 0° = +x, positive = counter-clockwise.
        // Conversion: screenAngle = -mathAngle (mod 360); sweep is signed in screen direction.
        for (i in 0 until 12) {
            val sign = Sign.byIndex(i)
            val color = when (sign.element) {
                Element.Fire -> FireCol; Element.Earth -> EarthCol
                Element.Air -> AirCol;   Element.Water -> WaterCol
            }.copy(alpha = 0.22f)
            val sliceStartLon = i * 30.0
            val mathA0 = lonToAngleDeg(sliceStartLon)
            val mathA1 = lonToAngleDeg(sliceStartLon + 30.0)
            val screenStart = (-mathA0).toFloat()
            val sweep = (mathA0 - mathA1).let { if (it < 0) it + 360 else it }.toFloat()
            drawArc(
                color = color,
                startAngle = screenStart,
                sweepAngle = sweep,
                useCenter = true,
                topLeft = Offset(center.x - outerR, center.y - outerR),
                size = Size(outerR * 2, outerR * 2),
            )
        }
        // Inner cut-out so zodiac becomes a ring
        drawCircle(color = InkBlack, radius = zodiacInnerR, center = center)
        drawCircle(color = Sepia.copy(alpha = 0.6f), radius = zodiacInnerR, center = center, style = Stroke(width = 1f))

        // --- Degree ticks ---
        for (deg in 0 until 360) {
            val len = when {
                deg % 30 == 0 -> 12f
                deg % 10 == 0 -> 8f
                deg % 5  == 0 -> 5f
                else -> 2f
            }
            val a = Math.toRadians(lonToAngleDeg(deg.toDouble()))
            val x1 = center.x + (outerR) * cos(a).toFloat()
            val y1 = center.y - (outerR) * sin(a).toFloat()
            val x2 = center.x + (outerR - len) * cos(a).toFloat()
            val y2 = center.y - (outerR - len) * sin(a).toFloat()
            drawLine(Sepia.copy(alpha = 0.7f), Offset(x1, y1), Offset(x2, y2), strokeWidth = 1f)
        }

        // --- Sign glyphs and division lines ---
        for (i in 0 until 12) {
            val sign = Sign.byIndex(i)
            val midLon = i * 30.0 + 15.0
            val a = Math.toRadians(lonToAngleDeg(midLon))
            val gr = (outerR + zodiacInnerR) / 2f
            val gx = center.x + gr * cos(a).toFloat()
            val gy = center.y - gr * sin(a).toFloat()
            val layout = measurer.measure(
                AnnotatedString(sign.glyph, SpanStyle(color = Bone, fontSize = 18.sp, fontWeight = FontWeight.Bold))
            )
            drawText(layout, topLeft = Offset(gx - layout.size.width / 2f, gy - layout.size.height / 2f))
            // sign dividing line
            val divLon = i * 30.0
            val da = Math.toRadians(lonToAngleDeg(divLon))
            drawLine(
                color = Sepia.copy(alpha = 0.6f),
                start = Offset(center.x + zodiacInnerR * cos(da).toFloat(),
                               center.y - zodiacInnerR * sin(da).toFloat()),
                end   = Offset(center.x + outerR * cos(da).toFloat(),
                               center.y - outerR * sin(da).toFloat()),
                strokeWidth = 1f,
            )
        }

        // --- House cusps ---
        for (i in 0 until 12) {
            val cuspLon = chart.cusps[i]
            val a = Math.toRadians(lonToAngleDeg(cuspLon))
            val isAngular = i == 0 || i == 3 || i == 6 || i == 9
            val stroke = if (isAngular) 2f else 1f
            val color = if (isAngular) Sepia else Brass.copy(alpha = 0.55f)
            drawLine(
                color = color,
                start = Offset(center.x, center.y),
                end = Offset(center.x + zodiacInnerR * cos(a).toFloat(),
                             center.y - zodiacInnerR * sin(a).toFloat()),
                strokeWidth = stroke,
                pathEffect = if (isAngular) null else PathEffect.dashPathEffect(floatArrayOf(4f, 4f)),
            )
            // House number near inner ring
            val mid = (chart.cusps[i] + houseSpanMid(chart.cusps[i], chart.cusps[(i + 1) % 12])) % 360.0
            val ha = Math.toRadians(lonToAngleDeg(mid))
            val hr = houseRingR * 0.85f
            val hx = center.x + hr * cos(ha).toFloat()
            val hy = center.y - hr * sin(ha).toFloat()
            val numLayout = measurer.measure(
                AnnotatedString("${i + 1}", SpanStyle(color = Brass, fontSize = 11.sp))
            )
            drawText(numLayout, topLeft = Offset(hx - numLayout.size.width / 2f, hy - numLayout.size.height / 2f))
        }

        // Inner house ring
        drawCircle(color = Sepia.copy(alpha = 0.4f), radius = houseRingR, center = center, style = Stroke(width = 1f))
        drawCircle(color = Sepia.copy(alpha = 0.3f), radius = aspectR,    center = center, style = Stroke(width = 1f))

        // --- Aspect lines (inside the inner circle) ---
        val placementMap = chart.placements.associate { it.body to it.longitude }
        for (asp in chart.aspects) {
            val a = placementMap[asp.a] ?: continue
            val b = placementMap[asp.b] ?: continue
            val angA = Math.toRadians(lonToAngleDeg(a))
            val angB = Math.toRadians(lonToAngleDeg(b))
            val pA = Offset(center.x + aspectR * cos(angA).toFloat(),
                            center.y - aspectR * sin(angA).toFloat())
            val pB = Offset(center.x + aspectR * cos(angB).toFloat(),
                            center.y - aspectR * sin(angB).toFloat())
            val color = when (asp.type) {
                AspectType.Conjunction -> Sepia
                AspectType.Opposition  -> FireCol
                AspectType.Trine       -> AirCol
                AspectType.Square      -> FireCol.copy(alpha = 0.7f)
                AspectType.Sextile     -> WaterCol
                AspectType.Quincunx    -> EarthCol
            }.copy(alpha = 0.75f)
            drawLine(color = color, start = pA, end = pB, strokeWidth = 1.3f)
        }

        // --- Planet glyphs with simple anti-overlap clustering ---
        val plotted = mutableListOf<Pair<Double, Body>>()
        for (p in chart.placements) {
            if (p.body == Body.Ascendant || p.body == Body.Midheaven) continue
            plotted += p.longitude to p.body
        }
        plotted.sortBy { it.first }
        // clustering: ensure 6° minimum sep along longitude for label placement
        val placedAngles = DoubleArray(plotted.size)
        for ((i, pair) in plotted.withIndex()) {
            var lon = pair.first
            if (i > 0) {
                val prev = placedAngles[i - 1]
                val diff = ((lon - prev) + 360.0) % 360.0
                if (diff < 6.0) lon = prev + 6.0
            }
            placedAngles[i] = lon
        }
        for ((i, pair) in plotted.withIndex()) {
            val labelLon = placedAngles[i]
            val angLabel = Math.toRadians(lonToAngleDeg(labelLon))
            val angTrue  = Math.toRadians(lonToAngleDeg(pair.first))
            // marker at the true position on the inner zodiac edge
            val markerR = zodiacInnerR - 4f
            val mx = center.x + markerR * cos(angTrue).toFloat()
            val my = center.y - markerR * sin(angTrue).toFloat()
            drawCircle(color = Bone, radius = 2.5f, center = Offset(mx, my))
            // tether line from marker to label position
            val lx = center.x + planetR * cos(angLabel).toFloat()
            val ly = center.y - planetR * sin(angLabel).toFloat()
            drawLine(color = Sepia.copy(alpha = 0.5f), start = Offset(mx, my), end = Offset(lx, ly), strokeWidth = 0.8f)
            // glyph
            val layout = measurer.measure(
                AnnotatedString(
                    pair.second.glyph,
                    SpanStyle(color = Bone, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                              fontFamily = FontFamily.SansSerif)
                )
            )
            drawText(layout, topLeft = Offset(lx - layout.size.width / 2f, ly - layout.size.height / 2f))
        }

        // --- Angle labels (ASC, MC, DSC, IC) ---
        labelAngle(measurer, center, outerR, lonToAngleDeg(chart.ascendant), "ASC")
        labelAngle(measurer, center, outerR, lonToAngleDeg(chart.midheaven), "MC")
        labelAngle(measurer, center, outerR, lonToAngleDeg(chart.ascendant + 180.0), "DSC")
        labelAngle(measurer, center, outerR, lonToAngleDeg(chart.midheaven + 180.0), "IC")
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.labelAngle(
    measurer: androidx.compose.ui.text.TextMeasurer,
    center: Offset,
    outerR: Float,
    angleDeg: Double,
    text: String,
) {
    val a = Math.toRadians(angleDeg)
    val r = outerR + 14f
    val x = center.x + r * cos(a).toFloat()
    val y = center.y - r * sin(a).toFloat()
    val layout = measurer.measure(
        AnnotatedString(text, SpanStyle(color = Sepia, fontSize = 10.sp, fontWeight = FontWeight.Bold))
    )
    drawText(layout, topLeft = Offset(x - layout.size.width / 2f, y - layout.size.height / 2f))
}

private fun houseSpanMid(a: Double, b: Double): Double {
    val span = ((b - a) + 360.0) % 360.0
    return span / 2.0
}
