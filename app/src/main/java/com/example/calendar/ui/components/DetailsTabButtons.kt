package com.example.calendar.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class DetailsTabKind { DAY, ASTRO, SOLAR }

/** A tab button drawn entirely in code (no bitmap): ornate frame, small illustration and a label. */
@Composable
fun DetailsTabButton(kind: DetailsTabKind, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bg = when (kind) {
        DetailsTabKind.DAY -> Brush.verticalGradient(listOf(Color(0xFF0A1F5C), Color(0xFF04103A)))
        DetailsTabKind.ASTRO -> Brush.verticalGradient(listOf(Color(0xFF1A0B3D), Color(0xFF080620)))
        DetailsTabKind.SOLAR -> Brush.verticalGradient(listOf(Color(0xFF2A2724), Color(0xFF121110)))
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .clickable(onClick = onClick)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawBackdrop(kind)
            drawFrame(kind)
            when (kind) {
                DetailsTabKind.DAY -> drawCalendarIcon()
                DetailsTabKind.ASTRO -> { drawStarField(); drawTelescope() }
                DetailsTabKind.SOLAR -> drawSunWithCloud()
            }
        }
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 11.dp, start = 6.dp, end = 6.dp)
        )
    }
}

private fun DrawScope.drawFrame(kind: DetailsTabKind) {
    val main: Color
    val soft: Color
    when (kind) {
        DetailsTabKind.DAY -> { main = Color(0xFF2F7BFF); soft = Color(0xFF8DB8FF) }
        DetailsTabKind.ASTRO -> { main = Color(0xFF9B4DFF); soft = Color(0xFFD2A8FF) }
        DetailsTabKind.SOLAR -> { main = Color(0xFFD4A017); soft = Color(0xFFFFE08A) }
    }
    val dp = density
    val outerR = CornerRadius(14 * dp)
    // soft glow + outer border
    drawRoundRect(main.copy(alpha = 0.22f), Offset(2 * dp, 2 * dp), Size(size.width - 4 * dp, size.height - 4 * dp), outerR, style = Stroke(6 * dp))
    drawRoundRect(
        Brush.verticalGradient(listOf(soft, main, soft)),
        Offset(1.5f * dp, 1.5f * dp), Size(size.width - 3 * dp, size.height - 3 * dp), outerR, style = Stroke(2.2f * dp)
    )
    // inner thin frame
    val inset = 8 * dp
    drawRoundRect(main.copy(alpha = 0.65f), Offset(inset, inset), Size(size.width - 2 * inset, size.height - 2 * inset), CornerRadius(10 * dp), style = Stroke(1f * dp))
}

private fun DrawScope.rays(center: Offset, r1: Float, r2: Float, count: Int, color: Color, width: Float) {
    for (i in 0 until count) {
        val a = (2 * PI * i / count).toFloat()
        drawLine(
            color,
            Offset(center.x + cos(a) * r1, center.y + sin(a) * r1),
            Offset(center.x + cos(a) * r2, center.y + sin(a) * r2),
            strokeWidth = width, cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawSunWithCloud() {
    val r = size.height * 0.11f
    val c = Offset(size.width * 0.46f, size.height * 0.30f)
    drawCircle(Color(0xFFFFC53D).copy(alpha = 0.25f), r * 2.1f, c)
    rays(c, r * 1.25f, r * 1.75f, 12, Color(0xFFFFD45A), r * 0.22f)
    drawCircle(Brush.radialGradient(listOf(Color(0xFFFFF3B0), Color(0xFFFFB300)), c, r), r, c)
    // cloud
    val cc = Offset(size.width * 0.56f, size.height * 0.40f)
    val u = r * 0.9f
    val cloud = Color(0xFFF4F7FF)
    drawCircle(cloud, u * 0.8f, Offset(cc.x - u * 0.9f, cc.y + u * 0.2f))
    drawCircle(cloud, u * 1.1f, Offset(cc.x, cc.y - u * 0.1f))
    drawCircle(cloud, u * 0.85f, Offset(cc.x + u * 1.0f, cc.y + u * 0.15f))
    drawRoundRect(cloud, Offset(cc.x - u * 1.7f, cc.y + u * 0.1f), Size(u * 3.4f, u * 0.9f), CornerRadius(u * 0.45f))
}

private val STAR_POINTS = listOf(
    0.12f to 0.20f, 0.20f to 0.62f, 0.30f to 0.14f, 0.72f to 0.18f, 0.84f to 0.34f, 0.90f to 0.12f,
    0.78f to 0.56f, 0.14f to 0.40f, 0.36f to 0.52f, 0.66f to 0.46f, 0.88f to 0.60f, 0.50f to 0.10f
)

private fun DrawScope.drawStarField() {
    STAR_POINTS.forEachIndexed { i, (fx, fy) ->
        val rr = (if (i % 3 == 0) 1.6f else 1f) * density
        drawCircle(Color.White.copy(alpha = if (i % 2 == 0) 0.9f else 0.55f), rr, Offset(size.width * fx, size.height * fy))
    }
}


private fun DrawScope.drawCalendarIcon() {
    val h = size.height
    val w = h * 0.30f
    val bh = h * 0.27f
    val left = size.width * 0.5f - w / 2
    val top = h * 0.17f
    val accent = Color(0xFF2F7BFF)
    drawCircle(Color(0xFF3D8BFF).copy(alpha = 0.25f), w * 0.95f, Offset(size.width / 2, top + bh / 2))
    // body
    drawRoundRect(Color(0xFFF2F6FF), Offset(left, top), Size(w, bh), CornerRadius(h * 0.035f))
    // header band
    drawRoundRect(accent, Offset(left, top), Size(w, bh * 0.30f), CornerRadius(h * 0.035f))
    drawRect(accent, Offset(left, top + bh * 0.15f), Size(w, bh * 0.15f))
    // binding rings
    listOf(0.28f, 0.72f).forEach { fx ->
        drawRoundRect(Color(0xFFC9D6F2), Offset(left + w * fx - h * 0.012f, top - h * 0.04f), Size(h * 0.024f, h * 0.075f), CornerRadius(h * 0.012f))
    }
    // day grid
    val cols = 4
    val rows = 2
    val gx = left + w * 0.12f
    val gy = top + bh * 0.42f
    val cw = (w * 0.76f) / cols
    val ch = (bh * 0.46f) / rows
    for (r in 0 until rows) for (cI in 0 until cols) {
        val hi = (r == 1 && cI == 2)
        drawRoundRect(
            if (hi) Color(0xFFFF5A5A) else Color(0xFF5B73A8).copy(alpha = 0.75f),
            Offset(gx + cI * cw + cw * 0.15f, gy + r * ch + ch * 0.15f), Size(cw * 0.7f, ch * 0.62f), CornerRadius(h * 0.01f)
        )
    }
}

private fun DrawScope.drawTelescope() {
    val h = size.height
    val c = Offset(size.width * 0.5f, h * 0.29f)
    val metal = Brush.linearGradient(listOf(Color(0xFFF1EAFB), Color(0xFFA990D8)), Offset(c.x - h * 0.2f, c.y), Offset(c.x + h * 0.2f, c.y))
    drawCircle(Color(0xFFB98CFF).copy(alpha = 0.22f), h * 0.22f, c)
    // tripod
    val leg = Color(0xFFCDB8F2)
    val base = Offset(c.x, c.y + h * 0.03f)
    drawLine(leg, base, Offset(c.x - h * 0.11f, h * 0.60f), h * 0.018f, StrokeCap.Round)
    drawLine(leg, base, Offset(c.x + h * 0.11f, h * 0.60f), h * 0.018f, StrokeCap.Round)
    drawLine(leg, base, Offset(c.x, h * 0.60f), h * 0.018f, StrokeCap.Round)
    // tube (tilted up to the right)
    rotate(-28f, c) {
        val tl = h * 0.36f
        val th = h * 0.085f
        // main tube
        drawRoundRect(metal, Offset(c.x - tl * 0.45f, c.y - th / 2), Size(tl * 0.8f, th), CornerRadius(th * 0.3f))
        // objective flare
        drawRoundRect(Color(0xFFE6DDF7), Offset(c.x + tl * 0.30f, c.y - th * 0.75f), Size(tl * 0.28f, th * 1.5f), CornerRadius(th * 0.25f))
        drawOval(Color(0xFF6C4BC4), Offset(c.x + tl * 0.50f, c.y - th * 0.6f), Size(th * 0.45f, th * 1.2f))
        // eyepiece
        drawRoundRect(Color(0xFFBFA9E6), Offset(c.x - tl * 0.62f, c.y - th * 0.28f), Size(tl * 0.18f, th * 0.56f), CornerRadius(th * 0.15f))
        // bands
        drawRect(Color(0xFF7D5BD0), Offset(c.x - tl * 0.1f, c.y - th / 2), Size(th * 0.25f, th))
    }
    // sparkle
    val sp = Offset(c.x + h * 0.21f, c.y - h * 0.17f)
    drawLine(Color.White, Offset(sp.x - h * 0.03f, sp.y), Offset(sp.x + h * 0.03f, sp.y), h * 0.008f, StrokeCap.Round)
    drawLine(Color.White, Offset(sp.x, sp.y - h * 0.03f), Offset(sp.x, sp.y + h * 0.03f), h * 0.008f, StrokeCap.Round)
}

/** Soft decorative background: light glow (day), galaxies (astro), mandala arcs (solar). */
private fun DrawScope.drawBackdrop(kind: DetailsTabKind) {
    val dp = density
    when (kind) {
        DetailsTabKind.DAY -> {
            val c = Offset(size.width / 2, size.height * 0.35f)
            drawCircle(Brush.radialGradient(listOf(Color(0xFF3D8BFF).copy(alpha = 0.35f), Color.Transparent), c, size.width * 0.55f), size.width * 0.55f, c)
        }
        DetailsTabKind.ASTRO -> {
            listOf(Offset(size.width * 0.22f, size.height * 0.30f) to 0.07f, Offset(size.width * 0.80f, size.height * 0.42f) to 0.06f).forEach { (c, k) ->
                val rr = size.width * k
                drawOval(Color(0xFFFFC9A0).copy(alpha = 0.55f), Offset(c.x - rr, c.y - rr * 0.38f), Size(rr * 2, rr * 0.76f))
                drawOval(Color(0xFF8F6BFF).copy(alpha = 0.35f), Offset(c.x - rr * 1.5f, c.y - rr * 0.6f), Size(rr * 3, rr * 1.2f), style = Stroke(1f * dp))
            }
        }
        DetailsTabKind.SOLAR -> {
            val c = Offset(size.width / 2, size.height * 0.5f)
            listOf(0.26f, 0.38f, 0.5f).forEach { k ->
                drawCircle(Color(0xFFD4A017).copy(alpha = 0.16f), size.height * k, c, style = Stroke(1f * dp))
            }
        }
    }
}
