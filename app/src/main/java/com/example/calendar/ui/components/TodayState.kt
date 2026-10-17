package com.example.calendar.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.calendar.core.CalendarManager
import com.example.calendar.model.GregorianDate
import kotlinx.coroutines.delay

/**
 * Today's date in Iran time. Updates itself at Iran midnight while the app is open,
 * and re-checks whenever the app comes back to the foreground.
 */
@Composable
fun rememberTodayGregorian(): GregorianDate {
    var today by remember { mutableStateOf(CalendarManager.getTodayGregorian()) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) today = CalendarManager.getTodayGregorian()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(CalendarManager.millisUntilNextIranMidnight() + 500L)
            today = CalendarManager.getTodayGregorian()
        }
    }
    return today
}
