package com.example.calendar.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calendar.core.AstronomicalCalculator
import com.example.calendar.core.CalendarManager
import com.example.calendar.core.IslamicCalendar
import com.example.calendar.core.JalaliCalendar
import com.example.calendar.model.EventType
import com.example.calendar.model.FullDayInfo
import com.example.ui.theme.AstroGold
import com.example.ui.theme.HolidayPurple
import com.example.ui.theme.HolidayPurpleContainer
import com.example.ui.theme.ScorpioAlert
import kotlinx.coroutines.delay
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Ephemeral spotlight overlay that:
 * 1. Highlights the indicated day cell (where the selection indicator is) with a pulsing dashed circle and dashed arrow.
 * 2. Displays a compact, space-efficient card with times, official holidays, and occasions.
 * 3. Features an Instagram story style filling progress line at the bottom (filling smoothly across 3 seconds).
 * 4. Hold-to-pause: When the user presses & holds their finger on the section, timer and progress freeze so it doesn't fade.
 * 5. Features a "محو نشو" (Keep open) button for permanent pinning.
 */
@Composable
fun TodaySpotlightOverlay(
    dayInfo: FullDayInfo,
    targetCenter: Offset?,
    targetRadius: Float,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isFa: Boolean = true
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var isHolding by remember { mutableStateOf(false) }
    val totalDurationMs = 3000L

    // Reset progress whenever becoming visible
    LaunchedEffect(visible) {
        if (visible) {
            progress = 0f
            isHolding = false
        }
    }

    // Instagram story 3-second auto-dismiss progress ticker
    // Pauses immediately when user holds finger (isHolding)
    LaunchedEffect(visible, isHolding) {
        if (visible && !isHolding) {
            val intervalMs = 16L
            val increment = intervalMs.toFloat() / totalDurationMs.toFloat()
            while (progress < 1f && !isHolding) {
                delay(intervalMs)
                if (!isHolding) {
                    progress = (progress + increment).coerceAtMost(1f)
                }
            }
            if (!isHolding && progress >= 1f) {
                onDismiss()
            }
        }
    }

    // Pulsing circle animation
    val infiniteTransition = rememberInfiniteTransition(label = "spotlight_pulse")
    val pulseOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_radius"
    )

    // Animated dashed line phase (flowing dash effect)
    val dashPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dash_phase"
    )

    var cardPositionInOverlay by remember { mutableStateOf<Offset?>(null) }
    var cardHeightPx by remember { mutableFloatStateOf(0f) }
    var cardWidthPx by remember { mutableFloatStateOf(0f) }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.96f),
        exit = fadeOut(tween(400)) + scaleOut(tween(400), targetScale = 0.96f),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.22f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        // Clicking backdrop dismisses only if not holding
                        if (!isHolding) onDismiss()
                    }
                )
                .testTag("today_spotlight_overlay")
        ) {
            // -----------------------------------------------------------
            // CANVAS: Pulsing Dashed Circle + Dashed Arrow to Indicated Day
            // -----------------------------------------------------------
            if (targetCenter != null && targetRadius > 0f) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = targetRadius + 4.dp.toPx() + pulseOffset

                    // 1. Highlight circle around the indicated day cell
                    drawCircle(
                        color = HolidayPurple.copy(alpha = 0.18f),
                        center = targetCenter,
                        radius = radius,
                        style = Fill
                    )

                    drawCircle(
                        color = HolidayPurple,
                        center = targetCenter,
                        radius = radius,
                        style = Stroke(
                            width = 2.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 7f), dashPhase)
                        )
                    )

                    // Secondary subtle golden glow ring
                    drawCircle(
                        color = AstroGold.copy(alpha = 0.6f),
                        center = targetCenter,
                        radius = radius + 3.dp.toPx(),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), -dashPhase)
                        )
                    )

                    // 2. Dashed Arrow connecting Card to Indicated Day's Circle
                    val cardPos = cardPositionInOverlay
                    if (cardPos != null && cardHeightPx > 0f) {
                        val isCardAbove = cardPos.y < targetCenter.y

                        val startX = (cardPos.x + cardWidthPx / 2f).coerceIn(40f, size.width - 40f)
                        val startY = if (isCardAbove) cardPos.y + cardHeightPx else cardPos.y

                        val endTargetY = if (isCardAbove) targetCenter.y - radius else targetCenter.y + radius
                        val endTargetX = targetCenter.x

                        val start = Offset(startX, startY)
                        val end = Offset(endTargetX, endTargetY)

                        val dx = end.x - start.x
                        val dy = end.y - start.y

                        val control1 = Offset(start.x + dx * 0.15f, start.y + dy * 0.65f)
                        val control2 = Offset(start.x + dx * 0.85f, start.y + dy * 0.35f)

                        val arrowPath = Path().apply {
                            moveTo(start.x, start.y)
                            cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y)
                        }

                        drawPath(
                            path = arrowPath,
                            color = HolidayPurple,
                            style = Stroke(
                                width = 2.6.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 7f), dashPhase)
                            )
                        )

                        // Arrowhead
                        val angle = atan2((end.y - control2.y).toDouble(), (end.x - control2.x).toDouble())
                        val arrowLength = 22f
                        val arrowAngle = Math.PI / 6.0

                        val p1 = Offset(
                            (end.x - arrowLength * cos(angle - arrowAngle)).toFloat(),
                            (end.y - arrowLength * sin(angle - arrowAngle)).toFloat()
                        )
                        val p2 = Offset(
                            (end.x - arrowLength * cos(angle + arrowAngle)).toFloat(),
                            (end.y - arrowLength * sin(angle + arrowAngle)).toFloat()
                        )

                        val headPath = Path().apply {
                            moveTo(end.x, end.y)
                            lineTo(p1.x, p1.y)
                            lineTo(p2.x, p2.y)
                            close()
                        }

                        drawPath(
                            path = headPath,
                            color = HolidayPurple,
                            style = Fill
                        )
                    }
                }
            }

            // -----------------------------------------------------------
            // COMPACT CALLOUT CARD: WITH INSTAGRAM STORY PROGRESS LINE
            // -----------------------------------------------------------
            val isTargetCellHigh = (targetCenter?.y ?: 0f) < 320f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .align(if (isTargetCellHigh) Alignment.BottomCenter else Alignment.TopCenter)
                    .padding(top = if (isTargetCellHigh) 0.dp else 40.dp, bottom = if (isTargetCellHigh) 20.dp else 0.dp)
                    .onGloballyPositioned { coordinates ->
                        cardPositionInOverlay = Offset(coordinates.positionInRoot().x, coordinates.positionInRoot().y)
                        cardHeightPx = coordinates.size.height.toFloat()
                        cardWidthPx = coordinates.size.width.toFloat()
                    }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(18.dp))
                        .border(1.2.dp, HolidayPurple.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        // Hold-to-pause mechanism: Detects finger press down and pause progress
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(PointerEventPass.Initial)
                                    isHolding = event.changes.any { it.pressed }
                                }
                            }
                        }
                        .testTag("spotlight_info_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        // 1. Compact Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = HolidayPurpleContainer,
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = HolidayPurple,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = if (isFa) "اوقات و مناسبت‌های امروز" else "Today's Times & Occasions",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(26.dp)
                                    .testTag("spotlight_close_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = if (isFa) "بستن" else "Close",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        )

                        // 2. Compact Content Summary (Concise, high-density format)
                        val jalaliMonthName = JalaliCalendar.MONTH_NAMES_PERSIAN.getOrElse(dayInfo.jalaliDate.month - 1) { "" }
                        val gregorianMonthName = CalendarManager.GREGORIAN_MONTH_NAMES_PERSIAN.getOrElse(dayInfo.gregorianDate.month - 1) { "" }
                        val islamicMonthName = IslamicCalendar.MONTH_NAMES_ARABIC.getOrElse(dayInfo.islamicDate.month - 1) { "" }

                        val officialHolidays = dayInfo.events.filter { it.isHoliday }
                        val isFriday = (dayInfo.dayOfWeekPersian == "جمعه")
                        val hasAnyHoliday = officialHolidays.isNotEmpty() || isFriday

                        val holidaysText = buildString {
                            if (isFriday) append(if (isFa) "جمعه (تعطیل پایان هفته)" else "Friday (Weekend)")
                            if (officialHolidays.isNotEmpty()) {
                                if (isNotEmpty()) append(" • ")
                                append(officialHolidays.joinToString(" • ") { it.title })
                            }
                        }

                        val otherOccasions = dayInfo.events
                            .filter { !it.isHoliday }
                            .map { it.title }

                        val animal = dayInfo.yearAnimal ?: AstronomicalCalculator.getYearAnimal(dayInfo.jalaliDate.year)
                        val element = dayInfo.monthElement ?: AstronomicalCalculator.getMonthElement(dayInfo.jalaliDate.month)

                        val summaryText = buildAnnotatedString {
                            // Date line & Animal
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                                append("📅 ")
                            }
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)) {
                                append("${dayInfo.dayOfWeekPersian}، ${dayInfo.jalaliDate.day} $jalaliMonthName")
                                append(" | ${dayInfo.gregorianDate.day} $gregorianMonthName")
                                append(" | ${dayInfo.islamicDate.day} $islamicMonthName")
                            }
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                                append(" • ${animal.namePersian} ${animal.emoji} (${element.titlePersian})\n")
                            }

                            // Solar times in single compact line
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                                append("🕌 اوقات: ")
                            }
                            withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)) {
                                append("صبح ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(dayInfo.solarTimes.dawn) }
                                append(" • طلوع ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(dayInfo.solarTimes.sunrise) }
                                append(" • ظهر ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(dayInfo.solarTimes.noon) }
                                append(" • غروب ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(dayInfo.solarTimes.sunset) }
                                append(" • مغرب ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(dayInfo.solarTimes.maghrib) }
                                append("\n")
                            }

                            // Holidays
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = HolidayPurple)) {
                                append("🟣 تعطیلات: ")
                                if (hasAnyHoliday) {
                                    append(holidaysText)
                                } else {
                                    append(if (isFa) "امروز تعطیل رسمی نیست." else "No official holiday today.")
                                }
                            }

                            // Occasions (if any)
                            if (otherOccasions.isNotEmpty()) {
                                append("\n")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                                    append("🌍 مناسبت: ")
                                }
                                withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)) {
                                    append(otherOccasions.take(2).joinToString(" • "))
                                    if (otherOccasions.size > 2) append(" ...")
                                }
                            }

                            // Scorpio indicator (if any)
                            val isInScorpioToday = dayInfo.qamarDarAqrab.isInTropicalScorpio || dayInfo.qamarDarAqrab.isInSiderealScorpio
                            if (isInScorpioToday) {
                                append("\n")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = ScorpioAlert)) {
                                    append("♏ وضعیت نجومی: قمر در عقرب")
                                }
                            }
                        }

                        Text(
                            text = summaryText,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // -----------------------------------------------------------
                        // 3. INSTAGRAM STORY PROGRESS BAR (LINE FILLING OVER 3 SECONDS)
                        // -----------------------------------------------------------
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                                .testTag("story_progress_bar_track")
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFF9333EA),
                                                Color(0xFFC084FC),
                                                Color(0xFFE879F9)
                                            )
                                        )
                                    )
                                    .testTag("story_progress_bar_fill")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // -----------------------------------------------------------
                        // 4. BOTTOM STATUS ROW: PURE INSTAGRAM STORY MECHANISM
                        // -----------------------------------------------------------
                        val remainingSec = ((1f - progress) * 3f + 0.95f).toInt().coerceIn(1, 3)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Left: Timer / Hold state indicator
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = if (isHolding) AstroGold else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )

                                Text(
                                    text = if (isHolding) {
                                        if (isFa) "⏸ در حال نگه‌داشتن (توقف زمان)" else "⏸ Holding (paused)"
                                    } else {
                                        if (isFa) "بسته شدن خودکار در $remainingSec ثانیه" else "Closing in ${remainingSec}s"
                                    },
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isHolding) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isHolding) AstroGold else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Right: Simple Close button
                            TextButton(
                                onClick = onDismiss,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("dismiss_spotlight_text_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = if (isFa) "بستن" else "Close",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
