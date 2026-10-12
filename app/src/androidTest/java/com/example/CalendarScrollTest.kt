package com.example

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.example.calendar.core.JalaliCalendar
import com.example.calendar.data.UserSettings
import com.example.calendar.ui.components.SeasonalRainOverlay
import com.example.calendar.ui.screens.CalendarScreen
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Gesture/state regressions, not a frame-time benchmark. Run on an Android device. */
class CalendarScrollTest {
    @get:Rule val compose = createComposeRule()
    private lateinit var listState: LazyListState
    private val targetJdn = JalaliCalendar.gregorianToJdn(2026, 10, 12)

    private fun showCalendar(withRain: Boolean) {
        // A continuously ticking decorative Canvas must not auto-advance the test clock forever.
        compose.mainClock.autoAdvance = false
        compose.setContent {
            val context = LocalContext.current
            val userSettings = androidx.compose.runtime.remember(context) { UserSettings(context) }
            listState = rememberLazyListState()
            MaterialTheme {
                Column(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxWidth().height(64.dp)) {
                        if (withRain) {
                            SeasonalRainOverlay(
                                season = "Autumn",
                                animationRunning = !listState.isScrollInProgress,
                                modifier = Modifier.matchParentSize()
                            )
                        }
                    }
                    CalendarScreen(
                        userSettings = userSettings,
                        targetJdn = targetJdn,
                        listState = listState,
                        modifier = Modifier.fillMaxWidth().height(360.dp)
                    )
                }
            }
        }
        settle()
    }

    private fun settle() {
        compose.mainClock.advanceTimeBy(1_000L)
        compose.waitForIdle()
    }

    private fun position(): Pair<Int, Int> {
        var position = 0 to 0
        compose.runOnIdle { position = listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
        return position
    }

    private fun assertBidirectionalScroll(withRain: Boolean) {
        showCalendar(withRain)
        val scroll = compose.onNodeWithTag("calendar_screen_scroll")
        val start = position()
        scroll.performTouchInput { swipeUp() }
        settle()
        val down = position()
        assertTrue("Swipe up must move down the content", down.first > start.first || down.second > start.second)
        scroll.performTouchInput { swipeDown() }
        settle()
        val back = position()
        assertTrue("Swipe down must move back up", back.first < down.first || (back.first == down.first && back.second < down.second))
        scroll.performScrollToKey("notes_$targetJdn")
        settle()
        compose.onNodeWithTag("notes_card").assertIsDisplayed()
        scroll.performScrollToKey("calendar_grid")
        settle()
        compose.onNodeWithTag("calendar_grid_card").assertIsDisplayed()
    }

    @Test fun scrollWithSeasonalRain() = assertBidirectionalScroll(true)
    @Test fun scrollWithoutSeasonalRain() = assertBidirectionalScroll(false)

    @Test fun unsavedNoteSurvivesScrollingOffscreen() {
        showCalendar(false)
        val scroll = compose.onNodeWithTag("calendar_screen_scroll")
        scroll.performScrollToKey("notes_$targetJdn")
        settle()
        compose.onNode(hasText("افزودن") or hasText("ویرایش")).performClick()
        settle()
        compose.onNode(hasSetTextAction()).performTextReplacement("scroll draft")
        settle()
        scroll.performScrollToKey("calendar_grid")
        settle()
        scroll.performScrollToKey("notes_$targetJdn")
        settle()
        compose.onNode(hasSetTextAction()).assertTextEquals("scroll draft")
    }
}
