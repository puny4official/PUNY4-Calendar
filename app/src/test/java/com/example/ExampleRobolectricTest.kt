package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.calendar.core.AstronomicalCalculator
import com.example.calendar.core.IslamicCalendar
import com.example.calendar.core.JalaliCalendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PUNY4 Calendar", appName)
  }

  @Test
  fun `verify jalali calendar conversion`() {
    // 2026-03-21 is around 1405-01-01
    val jdn = JalaliCalendar.gregorianToJdn(2026, 3, 21)
    val jalali = JalaliCalendar.jdnToJalali(jdn)
    assertEquals(1405, jalali.year)
    assertEquals(1, jalali.month)
    assertEquals(1, jalali.day)

    val backToGreg = JalaliCalendar.jalaliToGregorian(jalali.year, jalali.month, jalali.day)
    assertEquals(2026, backToGreg.year)
    assertEquals(3, backToGreg.month)
    assertEquals(21, backToGreg.day)
  }

  @Test
  fun `verify islamic calendar conversion`() {
    val jdn = JalaliCalendar.gregorianToJdn(2026, 9, 24)
    val islamic = IslamicCalendar.jdnToIslamic(jdn)
    assertTrue(islamic.year >= 1448)
    assertTrue(islamic.month in 1..12)
    assertTrue(islamic.day in 1..30)
  }

  @Test
  fun `verify astronomical moon and zodiac calculation`() {
    val jdn = JalaliCalendar.gregorianToJdn(2026, 9, 24)
    val moon = AstronomicalCalculator.calculateMoonInfo(jdn)
    assertNotNull(moon.phaseType)
    assertTrue(moon.illuminationPercent in 0..100)
    assertTrue(moon.ageDays >= 0.0 && moon.ageDays <= 30.0)

    val qamar = AstronomicalCalculator.checkQamarDarAqrab(jdn)
    assertNotNull(qamar.statusSummary)
  }
}
