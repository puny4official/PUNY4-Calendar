package com.example.calendar.core

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

/** Lets the top bar ask the calendar grid to open the monthly forecast dialog. */
object ForecastTrigger {
    var request by mutableIntStateOf(0)
    fun fire() { request += 1 }
}
