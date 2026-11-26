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
            drawCornerFlourishes(kind)
            when (kind) {
                DetailsTabKind.DAY -> drawSunWithCloud()
                DetailsTabKind.ASTRO -> { drawStarField(); drawMoon() }
                DetailsTabKind.SOLAR -> drawSunClockBook()
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
    // corner ornaments
    val pts = listOf(
        Offset(inset + 4 * dp, inset + 4 * dp), Offset(size.width - inset - 4 * dp, inset + 4 * dp),
        Offset(inset + 4 * dp, size.height - inset - 4 * dp), Offset(size.width - inset - 4 * dp, size.height - inset - 4 * dp)
    )
    pts.forEach { drawCircle(soft, 1.8f * dp, it) }
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
    drawCircle(Color(0xFF8DB8FF).copy(alpha = 0.25f), r * 2.1f, c)
    rays(c, r * 1.25f, r * 1.75f, 12, Color(0xFFE3E8F5), r * 0.22f)
    drawCircle(Brush.radialGradient(listOf(Color.White, Color(0xFFBFC8DD)), c, r), r, c)
    // cloud
    val cc = Offset(size.width * 0.56f, size.height * 0.40f)
    val u = r * 0.9f
    val cloud = Color(0xFFF4F7FF)
    drawCircle(cloud, u * 0.8f, Offset(cc.x - u * 0.9f, cc.y + u * 0.2f))
    drawCircle(cloud, u * 1.1f, Offset(cc.x, cc.y - u * 0.1f))
    drawCircle(cloud, u * 0.85f, Offset(cc.x + u * 1.0f, cc.y + u * 0.15f))
    drawRoundRect(cloud, Offset(cc.x - u * 1.7f, cc.y + u * 0.1f), Size(u * 3.4f, u * 0.9f), CornerRadius(u * 0.45f))
}

private fun DrawScope.drawMoon() {
    val r = size.height * 0.15f
    val c = Offset(size.width * 0.47f, size.height * 0.31f)
    val a = Path().apply { addOval(androidx.compose.ui.geometry.Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
    val b = Path().apply {
        val o = Offset(c.x + r * 0.5f, c.y - r * 0.15f)
        addOval(androidx.compose.ui.geometry.Rect(o.x - r * 0.9f, o.y - r * 0.9f, o.x + r * 0.9f, o.y + r * 0.9f))
    }
    val crescent = Path.combine(PathOperation.Difference, a, b)
    drawCircle(Color(0xFFB98CFF).copy(alpha = 0.22f), r * 1.6f, c)
    drawPath(crescent, Brush.linearGradient(listOf(Color(0xFFF1EAFB), Color(0xFFBFA9E6)), Offset(c.x - r, c.y - r), Offset(c.x + r, c.y + r)))
    // small star
    val s = Offset(size.width * 0.60f, size.height * 0.25f)
    val sr = r * 0.34f
    val star = Path().apply {
        for (i in 0 until 10) {
            val ang = (-PI / 2 + i * PI / 5).toFloat()
            val rr = if (i % 2 == 0) sr else sr * 0.45f
            val p = Offset(s.x + cos(ang) * rr, s.y + sin(ang) * rr)
            if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }
    drawPath(star, Color(0xFFD9C2FF))
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

private fun DrawScope.drawSunClockBook() {
    val gold = Color(0xFFE9B93A)
    val light = Color(0xFFFFE08A)
    val r = size.height * 0.095f
    val c = Offset(size.width * 0.40f, size.height * 0.30f)
    rays(c, r * 1.2f, r * 1.75f, 14, gold, r * 0.2f)
    drawCircle(Brush.radialGradient(listOf(light, gold), c, r), r, c)
    drawCircle(Color(0xFF3A2A06), r * 0.78f, c)
    drawCircle(gold, r * 0.78f, c, style = Stroke(r * 0.1f))
    drawLine(light, c, Offset(c.x, c.y - r * 0.55f), r * 0.1f, StrokeCap.Round)
    drawLine(light, c, Offset(c.x + r * 0.4f, c.y + r * 0.15f), r * 0.1f, StrokeCap.Round)
    // open book
    val bx = size.width * 0.58f
    val by = size.height * 0.31f
    val bw = size.height * 0.16f
    val bh = size.height * 0.15f
    val left = Path().apply {
        moveTo(bx, by - bh * 0.4f); lineTo(bx - bw, by - bh * 0.6f); lineTo(bx - bw, by + bh * 0.5f); lineTo(bx, by + bh * 0.7f); close()
    }
    val right = Path().apply {
        moveTo(bx, by - bh * 0.4f); lineTo(bx + bw, by - bh * 0.6f); lineTo(bx + bw, by + bh * 0.5f); lineTo(bx, by + bh * 0.7f); close()
    }
    drawPath(left, Brush.linearGradient(listOf(light, gold)))
    drawPath(right, Brush.linearGradient(listOf(gold, light)))
    drawLine(Color(0xFF3A2A06), Offset(bx, by - bh * 0.4f), Offset(bx, by + bh * 0.7f), size.height * 0.012f)
    for (i in 1..3) {
        val y = by - bh * 0.3f + i * bh * 0.22f
        drawLine(Color(0xFF3A2A06).copy(alpha = 0.7f), Offset(bx - bw * 0.85f, y - bh * 0.1f), Offset(bx - bw * 0.15f, y), size.height * 0.008f)
        drawLine(Color(0xFF3A2A06).copy(alpha = 0.7f), Offset(bx + bw * 0.15f, y), Offset(bx + bw * 0.85f, y - bh * 0.1f), size.height * 0.008f)
    }
}

private fun frameColors(kind: DetailsTabKind): Pair<Color, Color> = when (kind) {
    DetailsTabKind.DAY -> Color(0xFF2F7BFF) to Color(0xFF8DB8FF)
    DetailsTabKind.ASTRO -> Color(0xFF9B4DFF) to Color(0xFFD2A8FF)
    DetailsTabKind.SOLAR -> Color(0xFFD4A017) to Color(0xFFFFE08A)
}

/** Soft decorative background: light glow (day), galaxies (astro), mandala arcs (solar). */
private fun DrawScope.drawBackdrop(kind: DetailsTabKind) {
    val dp = density
    when (kind) {
        DetailsTabKind.DAY -> {
            drawCircle(
                Brush.radialGradient(listOf(Color(0xFF3D8BFF).copy(alpha = 0.35f), Color.Transparent), Offset(size.width / 2, size.height * 0.35f), size.width * 0.55f),
                size.width * 0.55f, Offset(size.width / 2, size.height * 0.35f)
            )
            // glowing tech lines on both sides
            listOf(0.18f, 0.82f).forEach { fx ->
                drawLine(Color(0xFF6FA8FF).copy(alpha = 0.55f), Offset(size.width * fx, size.height * 0.18f), Offset(size.width * fx, size.height * 0.62f), 1.2f * dp, StrokeCap.Round)
            }
        }
        DetailsTabKind.ASTRO -> {
            // two small galaxies
            listOf(Offset(size.width * 0.22f, size.height * 0.30f) to 0.07f, Offset(size.width * 0.80f, size.height * 0.42f) to 0.06f).forEach { (c, k) ->
                val rr = size.width * k
                drawOval(Color(0xFFFFC9A0).copy(alpha = 0.55f), Offset(c.x - rr, c.y - rr * 0.38f), Size(rr * 2, rr * 0.76f))
                drawOval(Color(0xFF8F6BFF).copy(alpha = 0.35f), Offset(c.x - rr * 1.5f, c.y - rr * 0.6f), Size(rr * 3, rr * 1.2f), style = Stroke(1f * dp))
                drawCircle(Color.White.copy(alpha = 0.85f), 1.4f * dp, c)
            }
        }
        DetailsTabKind.SOLAR -> {
            val c = Offset(size.width / 2, size.height * 0.5f)
            listOf(0.26f, 0.38f, 0.5f).forEach { k ->
                drawCircle(Color(0xFFD4A017).copy(alpha = 0.16f), size.height * k, c, style = Stroke(1f * dp))
            }
            // faint constellation
            val pts = listOf(Offset(0.12f, 0.28f), Offset(0.2f, 0.18f), Offset(0.27f, 0.30f), Offset(0.34f, 0.2f))
            pts.zipWithNext().forEach { (a, b) ->
                drawLine(Color(0xFFFFE08A).copy(alpha = 0.35f), Offset(size.width * a.x, size.height * a.y), Offset(size.width * b.x, size.height * b.y), 0.8f * dp)
            }
            pts.forEach { drawCircle(Color(0xFFFFE08A).copy(alpha = 0.7f), 1.3f * dp, Offset(size.width * it.x, size.height * it.y)) }
        }
    }
}

/** Curly ornament in each corner of the inner frame. */
private fun DrawScope.drawCornerFlourishes(kind: DetailsTabKind) {
    val (main, soft) = frameColors(kind)
    val dp = density
    val inset = 8 * dp
    val r = 9 * dp
    val corners = listOf(
        Triple(Offset(inset, inset), 0f, 1f to 1f), Triple(Offset(size.width - inset, inset), 90f, -1f to 1f),
        Triple(Offset(inset, size.height - inset), 270f, 1f to -1f), Triple(Offset(size.width - inset, size.height - inset), 180f, -1f to -1f)
    )
    corners.forEach { (p, startAngle, dir) ->
        val cx = p.x + dir.first * r * 0.9f
        val cy = p.y + dir.second * r * 0.9f
        drawArc(
            soft.copy(alpha = 0.9f), startAngle, 90f, false,
            Offset(cx - r, cy - r), Size(r * 2, r * 2), style = Stroke(1.3f * dp, cap = StrokeCap.Round)
        )
        drawCircle(main, 1.6f * dp, Offset(p.x + dir.first * r * 1.9f, p.y + dir.second * r * 1.9f))
    }
}
