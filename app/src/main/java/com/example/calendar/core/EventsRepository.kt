package com.example.calendar.core

import com.example.calendar.model.CalendarCategory
import com.example.calendar.model.CalendarEvent
import com.example.calendar.model.EventType
import com.example.calendar.model.GregorianDate
import com.example.calendar.model.IslamicDate
import com.example.calendar.model.JalaliDate
import com.example.calendar.model.SearchableCalendarEvent

object EventsRepository {

    /**
     * مناسبت‌های دقیق آبان ۱۴۰۵ (از فایل تقویم آبان ۱۴۰۵؛ منابع: تقویم رسمی دانشگاه تهران و تقویم سازمان ملل).
     * کلید: روز ماه آبان. برای آبان ۱۴۰۵ فقط همین فهرست نمایش داده می‌شود.
     */
    private val ABAN_1405_EVENTS: Map<Int, List<CalendarEvent>> = mapOf(
        1 to listOf(
            CalendarEvent("ابوالفضل بیهقی", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("سالروز شهادت سید مصطفی خمینی", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز آمار و برنامه‌ریزی", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز صنعت ساختمان", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز جهانی پلنگ برفی", isHoliday = false, EventType.INTERNATIONAL, "")
        ),
        2 to listOf(
            CalendarEvent("شهادت حضرت فاطمه زهرا (به یک روایت)", isHoliday = false, EventType.RELIGIOUS, "نکته: انتساب شهادت حضرت فاطمه زهرا در این روز بر پایهٔ یک روایت است؛ تعطیل رسمی آبان در تقویم ۱۴۰۵ روز ۲۲ است. بازه‌های مرتبط: هفته خلع سلاح سازمان ملل، ۲۴ تا ۳۰ اکتبر؛ هفته جهانی سواد رسانه‌ای و اطلاعاتی، ۲۴ تا ۳۱ اکتبر."),
            CalendarEvent("روز ملل متحد", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("روز جهانی اطلاعات توسعه", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("هفته خلع سلاح سازمان ملل", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲ تا ۸ آبان ۱۴۰۵ — بازه: ۲۴ تا ۳۰ اکتبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("هفته جهانی سواد رسانه‌ای و اطلاعاتی", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲ تا ۹ آبان ۱۴۰۵ — بازه: ۲۴ تا ۳۱ اکتبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("دانشگاه تهران — ثبت‌نام الکترونیکی ورودی‌های جدید کارشناسی ارشد", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۲ تا ۵ آبان ۱۴۰۵ — بازه: ۲۴ تا ۲۷ اکتبر ۲۰۲۶، شامل هر دو روز. — محدودیت شمول: این اطلاعیه فقط برای ورودی‌های جدید کارشناسی ارشد است.")
        ),
        3 to listOf(
            CalendarEvent("هفته خلع سلاح سازمان ملل", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲ تا ۸ آبان ۱۴۰۵ — بازه: ۲۴ تا ۳۰ اکتبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("هفته جهانی سواد رسانه‌ای و اطلاعاتی", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲ تا ۹ آبان ۱۴۰۵ — بازه: ۲۴ تا ۳۱ اکتبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("دانشگاه تهران — ثبت‌نام الکترونیکی ورودی‌های جدید کارشناسی ارشد", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۲ تا ۵ آبان ۱۴۰۵ — بازه: ۲۴ تا ۲۷ اکتبر ۲۰۲۶، شامل هر دو روز. — محدودیت شمول: این اطلاعیه فقط برای ورودی‌های جدید کارشناسی ارشد است.")
        ),
        4 to listOf(
            CalendarEvent("اعتراض امام خمینی به کاپیتولاسیون در سال ۱۳۴۳", isHoliday = false, EventType.NATIONAL, "نکتهٔ گاه‌شماری: آبانگان در گاه‌شماری فصلی زرتشتی در ۲۶ اکتبر/آبان ۴ است، زیرا روز آبان در ماه آبان، روز دهم آن گاه‌شماری است؛ آن را با آبان ۱۰ هجری خورشیدی اشتباه نگیرید. گاه‌شماری‌های فصلی، شهنشاهی و قدیمی با هم تفاوت دارند."),
            CalendarEvent("آبانگان در گاه‌شماری زرتشتی فصلی", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("هفته خلع سلاح سازمان ملل", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲ تا ۸ آبان ۱۴۰۵ — بازه: ۲۴ تا ۳۰ اکتبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("هفته جهانی سواد رسانه‌ای و اطلاعاتی", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲ تا ۹ آبان ۱۴۰۵ — بازه: ۲۴ تا ۳۱ اکتبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("دانشگاه تهران — ثبت‌نام الکترونیکی ورودی‌های جدید کارشناسی ارشد", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۲ تا ۵ آبان ۱۴۰۵ — بازه: ۲۴ تا ۲۷ اکتبر ۲۰۲۶، شامل هر دو روز. — محدودیت شمول: این اطلاعیه فقط برای ورودی‌های جدید کارشناسی ارشد است.")
        ),
        5 to listOf(
            CalendarEvent("روز زعفران", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز جهانی میراث دیداری‌ـ‌شنیداری", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("هفته خلع سلاح سازمان ملل", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲ تا ۸ آبان ۱۴۰۵ — بازه: ۲۴ تا ۳۰ اکتبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("هفته جهانی سواد رسانه‌ای و اطلاعاتی", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲ تا ۹ آبان ۱۴۰۵ — بازه: ۲۴ تا ۳۱ اکتبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("دانشگاه تهران — ثبت‌نام الکترونیکی ورودی‌های جدید کارشناسی ارشد", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۲ تا ۵ آبان ۱۴۰۵ — بازه: ۲۴ تا ۲۷ اکتبر ۲۰۲۶، شامل هر دو روز. — محدودیت شمول: این اطلاعیه فقط برای ورودی‌های جدید کارشناسی ارشد است."),
            CalendarEvent("دانشگاه تهران — ثبت‌نام حضوری ورودی‌های جدید کارشناسی ارشد", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۵ تا ۶ آبان ۱۴۰۵ — بازه: ۲۷ تا ۲۸ اکتبر ۲۰۲۶، شامل هر دو روز. — محدودیت شمول: این اطلاعیه فقط برای ورودی‌های جدید کارشناسی ارشد است.")
        ),
        6 to listOf(
            CalendarEvent("هفته خلع سلاح سازمان ملل", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲ تا ۸ آبان ۱۴۰۵ — بازه: ۲۴ تا ۳۰ اکتبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("هفته جهانی سواد رسانه‌ای و اطلاعاتی", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲ تا ۹ آبان ۱۴۰۵ — بازه: ۲۴ تا ۳۱ اکتبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("دانشگاه تهران — ثبت‌نام حضوری ورودی‌های جدید کارشناسی ارشد", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۵ تا ۶ آبان ۱۴۰۵ — بازه: ۲۷ تا ۲۸ اکتبر ۲۰۲۶، شامل هر دو روز. — محدودیت شمول: این اطلاعیه فقط برای ورودی‌های جدید کارشناسی ارشد است.")
        ),
        7 to listOf(
            CalendarEvent("روز انار", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز ملل متحد برای مراقبت و حمایت", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("روز کوروش", isHoliday = false, EventType.NATIONAL, "نکته: «روز کوروش» یک یادبود غیررسمی معاصر است و تعطیل یا مناسبت رسمی تقویم ایران نیست."),
            CalendarEvent("هفته خلع سلاح سازمان ملل", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲ تا ۸ آبان ۱۴۰۵ — بازه: ۲۴ تا ۳۰ اکتبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("هفته جهانی سواد رسانه‌ای و اطلاعاتی", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲ تا ۹ آبان ۱۴۰۵ — بازه: ۲۴ تا ۳۱ اکتبر ۲۰۲۶، شامل هر دو روز.")
        ),
        8 to listOf(
            CalendarEvent("سالروز شهادت محمدحسین فهمیده", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز نوجوان و بسیج دانش‌آموزی", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز پدافند غیرعامل", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز محیط‌بان", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("هفته خلع سلاح سازمان ملل", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲ تا ۸ آبان ۱۴۰۵ — بازه: ۲۴ تا ۳۰ اکتبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("هفته جهانی سواد رسانه‌ای و اطلاعاتی", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲ تا ۹ آبان ۱۴۰۵ — بازه: ۲۴ تا ۳۱ اکتبر ۲۰۲۶، شامل هر دو روز.")
        ),
        9 to listOf(
            CalendarEvent("روز جهانی شهرها", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("هالووین (مناسبت فرهنگیِ گاه‌شماری میلادی)", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("هفته جهانی سواد رسانه‌ای و اطلاعاتی", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲ تا ۹ آبان ۱۴۰۵ — بازه: ۲۴ تا ۳۱ اکتبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("دانشگاه تهران — آغاز کلاس‌های ورودی‌های جدید کارشناسی ارشد", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۹ آبان ۱۴۰۵")
        ),
        10 to listOf(
            CalendarEvent("سالروز شهادت آیت‌الله قاضی طباطبایی", isHoliday = false, EventType.NATIONAL, "")
        ),
        11 to listOf(
            CalendarEvent("روز جهانی پایان‌دادن به مصونیت از مجازاتِ جرائم علیه روزنامه‌نگاران", isHoliday = false, EventType.INTERNATIONAL, "")
        ),
        13 to listOf(
            CalendarEvent("سالروز تسخیر سفارت آمریکا", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز ملی مبارزه با استکبار جهانی", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("روز دانش‌آموز", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("سالروز تبعید امام خمینی به ترکیه در ۱۳۴۳", isHoliday = false, EventType.NATIONAL, "")
        ),
        14 to listOf(
            CalendarEvent("روز فرهنگ عمومی", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز مازندران", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز جهانی آگاهی از سونامی", isHoliday = false, EventType.INTERNATIONAL, "")
        ),
        15 to listOf(
            CalendarEvent("روز جهانی سازمان ملل برای پیشگیری از بهره‌کشی از محیط زیست در جنگ و درگیری مسلحانه", isHoliday = false, EventType.INTERNATIONAL, "نکته: برخی رسانه‌ها «میانهٔ پاییز» یا «آیاثرم» را برای این روز ذکر می‌کنند؛ این انتساب در تقویم رسمی نیامده و نامطمئن است.")
        ),
        18 to listOf(
            CalendarEvent("روز کیفیت", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("آغاز هفتهٔ جهانی علم و صلح (تا ۱۵ نوامبر)", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("هفته جهانی علم و صلح", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۱۸ تا ۲۴ آبان ۱۴۰۵ — بازه: ۹ تا ۱۵ نوامبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("دانشگاه تهران — آماده‌شدن کارت دانشجویی ورودی‌های جدید کارشناسی ارشد", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۱۸ آبان ۱۴۰۵")
        ),
        19 to listOf(
            CalendarEvent("روز جهانی علم در خدمت صلح و توسعه", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("هفته جهانی علم و صلح", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۱۸ تا ۲۴ آبان ۱۴۰۵ — بازه: ۹ تا ۱۵ نوامبر ۲۰۲۶، شامل هر دو روز.")
        ),
        20 to listOf(
            CalendarEvent("هفته جهانی علم و صلح", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۱۸ تا ۲۴ آبان ۱۴۰۵ — بازه: ۹ تا ۱۵ نوامبر ۲۰۲۶، شامل هر دو روز.")
        ),
        21 to listOf(
            CalendarEvent("روز هوافضا", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("هفته جهانی علم و صلح", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۱۸ تا ۲۴ آبان ۱۴۰۵ — بازه: ۹ تا ۱۵ نوامبر ۲۰۲۶، شامل هر دو روز.")
        ),
        22 to listOf(
            CalendarEvent("شهادت حضرت فاطمه زهرا", isHoliday = true, EventType.RELIGIOUS, "این روز در سال ۱۴۰۵ با جمعه مصادف است."),
            CalendarEvent("هفته جهانی علم و صلح", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۱۸ تا ۲۴ آبان ۱۴۰۵ — بازه: ۹ تا ۱۵ نوامبر ۲۰۲۶، شامل هر دو روز.")
        ),
        23 to listOf(
            CalendarEvent("روز جهانی دیابت", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("آغاز بازهٔ امتحانات میان‌ترم عمومی دانشگاه تهران (تا ۵ آذر / ۲۶ نوامبر)", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("هفته جهانی علم و صلح", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۱۸ تا ۲۴ آبان ۱۴۰۵ — بازه: ۹ تا ۱۵ نوامبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("دانشگاه تهران — بازه عمومی امتحانات میان‌ترم", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۲۳ آبان تا ۵ آذر ۱۴۰۵ — بازهٔ اعلام‌شده در تقویم آموزشی دانشگاه تهران؛ این بازه عمومی است.")
        ),
        24 to listOf(
            CalendarEvent("روز کتاب، کتاب‌خوانی و کتابدار", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("بزرگداشت علامه طباطبایی", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز جهانی یادبود قربانیان سوانح رانندگی", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("روز جهانی پیشگیری از جرائم سازمان‌یافتهٔ فراملی", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("هفته جهانی علم و صلح", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۱۸ تا ۲۴ آبان ۱۴۰۵ — بازه: ۹ تا ۱۵ نوامبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("دانشگاه تهران — بازه عمومی امتحانات میان‌ترم", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۲۳ آبان تا ۵ آذر ۱۴۰۵ — بازهٔ اعلام‌شده در تقویم آموزشی دانشگاه تهران؛ این بازه عمومی است.")
        ),
        25 to listOf(
            CalendarEvent("روز اصفهان", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز صنعت نوشت‌افزار", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز جهانی مدارا", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("روز رژیم غذایی مدیترانه‌ای", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("دانشگاه تهران — بازه عمومی امتحانات میان‌ترم", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۲۳ آبان تا ۵ آذر ۱۴۰۵ — بازهٔ اعلام‌شده در تقویم آموزشی دانشگاه تهران؛ این بازه عمومی است.")
        ),
        26 to listOf(
            CalendarEvent("دانشگاه تهران — بازه عمومی امتحانات میان‌ترم", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۲۳ آبان تا ۵ آذر ۱۴۰۵ — بازهٔ اعلام‌شده در تقویم آموزشی دانشگاه تهران؛ این بازه عمومی است.")
        ),
        27 to listOf(
            CalendarEvent("روز جهانی پیشگیری و بهبود از سوءاستفاده، بهره‌کشی و خشونت جنسی علیه کودکان", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("آغاز هفتهٔ جهانی آگاهی از مقاومت میکروبی (تا ۲۵ نوامبر)", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("هفته جهانی آگاهی از مقاومت میکروبی", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲۷ آبان تا ۴ آذر ۱۴۰۵ — بازه: ۱۸ تا ۲۵ نوامبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("دانشگاه تهران — بازه عمومی امتحانات میان‌ترم", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۲۳ آبان تا ۵ آذر ۱۴۰۵ — بازهٔ اعلام‌شده در تقویم آموزشی دانشگاه تهران؛ این بازه عمومی است.")
        ),
        28 to listOf(
            CalendarEvent("روز جهانی فلسفه", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("روز جهانی توالت", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("هفته جهانی آگاهی از مقاومت میکروبی", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲۷ آبان تا ۴ آذر ۱۴۰۵ — بازه: ۱۸ تا ۲۵ نوامبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("دانشگاه تهران — بازه عمومی امتحانات میان‌ترم", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۲۳ آبان تا ۵ آذر ۱۴۰۵ — بازهٔ اعلام‌شده در تقویم آموزشی دانشگاه تهران؛ این بازه عمومی است.")
        ),
        29 to listOf(
            CalendarEvent("روز جهانی کودک", isHoliday = false, EventType.INTERNATIONAL, "یادداشت تاریخیِ پنج روز پایانی آبان: در برخی دوره‌های تاریخی، از سدهٔ پنجم تا اوایل سدهٔ یازدهم، این پنج روز را «فروردگان کوچک» می‌گرفتند؛ این یادکرد تاریخی، مناسبت ثابتِ امروز نیست."),
            CalendarEvent("روز صنعتی‌شدن آفریقا", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("هفته جهانی آگاهی از مقاومت میکروبی", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲۷ آبان تا ۴ آذر ۱۴۰۵ — بازه: ۱۸ تا ۲۵ نوامبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("دانشگاه تهران — بازه عمومی امتحانات میان‌ترم", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۲۳ آبان تا ۵ آذر ۱۴۰۵ — بازهٔ اعلام‌شده در تقویم آموزشی دانشگاه تهران؛ این بازه عمومی است.")
        ),
        30 to listOf(
            CalendarEvent("روز ملی قهرمانان", isHoliday = false, EventType.NATIONAL, "یادداشت تاریخی: پنج روز پایانی آبان در برخی دوره‌های تاریخی (از سدهٔ پنجم تا اوایل سدهٔ یازدهم) «فروردگان کوچک» شمرده می‌شدند؛ این امر یک مناسبت ثابتِ امروزی نیست."),
            CalendarEvent("بزرگداشت ابونصر فارابی", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز حکمت و فلسفه", isHoliday = false, EventType.NATIONAL, ""),
            CalendarEvent("روز جهانی تلویزیون", isHoliday = false, EventType.INTERNATIONAL, ""),
            CalendarEvent("هفته جهانی آگاهی از مقاومت میکروبی", isHoliday = false, EventType.INTERNATIONAL, "تاریخ شمسی: ۲۷ آبان تا ۴ آذر ۱۴۰۵ — بازه: ۱۸ تا ۲۵ نوامبر ۲۰۲۶، شامل هر دو روز."),
            CalendarEvent("دانشگاه تهران — بازه عمومی امتحانات میان‌ترم", isHoliday = false, EventType.NATIONAL, "تاریخ شمسی: ۲۳ آبان تا ۵ آذر ۱۴۰۵ — بازهٔ اعلام‌شده در تقویم آموزشی دانشگاه تهران؛ این بازه عمومی است.")
        )
    )

    fun getEvents(
        jalali: JalaliDate,
        islamic: IslamicDate,
        gregorian: GregorianDate
    ): List<CalendarEvent> {
        val list = mutableListOf<CalendarEvent>()

        // Aban 1405: exact, source-checked list replaces the generic recurring events
        if (jalali.year == 1405 && jalali.month == 8) {
            ABAN_1405_EVENTS[jalali.day]?.let { list.addAll(it) }
            ASTRONOMICAL_EVENTS[Pair(jalali.month, jalali.day)]?.let { list.add(it) }
            return list
        }

        // 1. Solar Hijri events (National and Iranian holidays)
        val solarEvents = SOLAR_EVENTS[Pair(jalali.month, jalali.day)]
        if (solarEvents != null) {
            list.addAll(solarEvents)
        }

        // 2. Islamic Lunar events (Religious and Official holidays in Iran)
        val islamicEvents = ISLAMIC_EVENTS[Pair(islamic.month, islamic.day)]
        if (islamicEvents != null) {
            list.addAll(islamicEvents)
        }

        // 3. International / Global events (مناسبت‌های جهانی)
        val globalEvents = GLOBAL_EVENTS[Pair(gregorian.month, gregorian.day)]
        if (globalEvents != null) {
            list.addAll(globalEvents)
        }

        // 4. Astronomical events (رویدادهای نجومی و آسمانی)
        val astroEvent = ASTRONOMICAL_EVENTS[Pair(jalali.month, jalali.day)]
        if (astroEvent != null) {
            list.add(astroEvent)
        }

        // 5. مناسبت‌های متغیر: چهارشنبه‌سوری (آخرین سه‌شنبه‌شب منتهی به نوروز)
        if (jalali.month == 12 && jalali.day >= 23) {
            val jdn = JalaliCalendar.jalaliToJdn(jalali.year, jalali.month, jalali.day)
            val nowruz = JalaliCalendar.jalaliToJdn(jalali.year + 1, 1, 1)
            var wed = nowruz - 1
            while (JalaliCalendar.getDayOfWeekIndex(wed) != 4) wed--
            if (jdn == wed - 1) {
                list.add(CalendarEvent("شب چهارشنبه‌سوری (جشن باستانی آتش)", isHoliday = false, EventType.NATIONAL))
            }
        }

        return list
    }

    fun getAllSearchableEvents(): List<SearchableCalendarEvent> {
        val list = mutableListOf<SearchableCalendarEvent>()

        // In 1405 the exact Aban list replaces the generic Aban / overlapping lunar and global entries
        val todayG = CalendarManager.getTodayGregorian()
        val is1405 = JalaliCalendar.jdnToJalali(JalaliCalendar.gregorianToJdn(todayG.year, todayG.month, todayG.day)).year == 1405
        if (is1405) {
            val seen = HashSet<String>()
            ABAN_1405_EVENTS.toSortedMap().forEach { (day, events) ->
                events.forEach { ev ->
                    if (seen.add(ev.title)) { // multi-day ranges are listed once, on their first day
                        list.add(
                            SearchableCalendarEvent(
                                title = ev.title,
                                isHoliday = ev.isHoliday,
                                eventType = ev.type,
                                calendarCategory = CalendarCategory.SOLAR,
                                month = 8,
                                day = day,
                                formattedDate = "$day آبان (شمسی)"
                            )
                        )
                    }
                }
            }
        }

        SOLAR_EVENTS.forEach { (key, events) ->
            val (month, day) = key
            if (is1405 && month == 8) return@forEach
            val monthName = JalaliCalendar.MONTH_NAMES_PERSIAN.getOrElse(month - 1) { "" }
            events.forEach { ev ->
                list.add(
                    SearchableCalendarEvent(
                        title = ev.title,
                        isHoliday = ev.isHoliday,
                        eventType = ev.type,
                        calendarCategory = CalendarCategory.SOLAR,
                        month = month,
                        day = day,
                        formattedDate = "$day $monthName (شمسی)"
                    )
                )
            }
        }

        ISLAMIC_EVENTS.forEach { (key, events) ->
            val (month, day) = key
            if (is1405 && ((month == 5 && day >= 12) || (month == 6 && day <= 11))) return@forEach
            val monthName = IslamicCalendar.MONTH_NAMES_ARABIC.getOrElse(month - 1) { "" }
            events.forEach { ev ->
                list.add(
                    SearchableCalendarEvent(
                        title = ev.title,
                        isHoliday = ev.isHoliday,
                        eventType = ev.type,
                        calendarCategory = CalendarCategory.ISLAMIC,
                        month = month,
                        day = day,
                        formattedDate = "$day $monthName (قمری)"
                    )
                )
            }
        }

        GLOBAL_EVENTS.forEach { (key, events) ->
            val (month, day) = key
            // Aban 1405 spans 23 Oct - 21 Nov 2026
            if (is1405 && ((month == 10 && day >= 23) || (month == 11 && day <= 21))) return@forEach
            val monthName = CalendarManager.GREGORIAN_MONTH_NAMES_PERSIAN.getOrElse(month - 1) { "" }
            events.forEach { ev ->
                list.add(
                    SearchableCalendarEvent(
                        title = ev.title,
                        isHoliday = ev.isHoliday,
                        eventType = ev.type,
                        calendarCategory = CalendarCategory.GLOBAL,
                        month = month,
                        day = day,
                        formattedDate = "$day $monthName (میلادی)"
                    )
                )
            }
        }

        ASTRONOMICAL_EVENTS.forEach { (key, ev) ->
            val (month, day) = key
            val monthName = JalaliCalendar.MONTH_NAMES_PERSIAN.getOrElse(month - 1) { "" }
            list.add(
                SearchableCalendarEvent(
                    title = ev.title,
                    isHoliday = ev.isHoliday,
                    eventType = ev.type,
                    calendarCategory = CalendarCategory.ASTRONOMICAL,
                    month = month,
                    day = day,
                    formattedDate = "$day $monthName (نجومی)"
                )
            )
        }

        return list
    }

    fun searchEvents(query: String, category: CalendarCategory = CalendarCategory.ALL): List<SearchableCalendarEvent> {
        val trimmed = query.trim()
        val all = getAllSearchableEvents()
        return all.filter { item ->
            val matchesCategory = when (category) {
                CalendarCategory.ALL -> true
                CalendarCategory.HOLIDAYS -> item.isHoliday
                CalendarCategory.SOLAR -> item.calendarCategory == CalendarCategory.SOLAR
                CalendarCategory.ISLAMIC -> item.calendarCategory == CalendarCategory.ISLAMIC
                CalendarCategory.GLOBAL -> item.calendarCategory == CalendarCategory.GLOBAL
                CalendarCategory.ASTRONOMICAL -> item.calendarCategory == CalendarCategory.ASTRONOMICAL
            }
            val matchesQuery = if (trimmed.isEmpty()) {
                true
            } else {
                item.title.contains(trimmed, ignoreCase = true) ||
                item.formattedDate.contains(trimmed, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
    }

    fun calculateJdnForEvent(event: SearchableCalendarEvent, todayJdn: Long): Long {
        val todayJalali = JalaliCalendar.jdnToJalali(todayJdn)
        val todayIslamic = IslamicCalendar.jdnToIslamic(todayJdn)
        val todayGregorian = JalaliCalendar.jdnToGregorian(todayJdn)

        return when (event.calendarCategory) {
            CalendarCategory.SOLAR, CalendarCategory.ASTRONOMICAL -> {
                JalaliCalendar.jalaliToJdn(todayJalali.year, event.month, event.day)
            }
            CalendarCategory.ISLAMIC -> {
                IslamicCalendar.islamicToJdn(todayIslamic.year, event.month, event.day)
            }
            CalendarCategory.GLOBAL -> {
                JalaliCalendar.gregorianToJdn(todayGregorian.year, event.month, event.day)
            }
            else -> todayJdn
        }
    }

    // --------------------------------------------------------------------
    // رویدادها و تعطیلات رسمی ایران بر پایه تقویم خورشیدی
    // --------------------------------------------------------------------
    private val SOLAR_EVENTS = mapOf(
        Pair(1, 1) to listOf(CalendarEvent("جشن نوروز / سال نو خورشیدی", isHoliday = true, EventType.NATIONAL)),
        Pair(1, 2) to listOf(CalendarEvent("عید نوروز", isHoliday = true, EventType.NATIONAL)),
        Pair(1, 3) to listOf(CalendarEvent("عید نوروز", isHoliday = true, EventType.NATIONAL)),
        Pair(1, 4) to listOf(CalendarEvent("عید نوروز", isHoliday = true, EventType.NATIONAL)),
        Pair(1, 12) to listOf(CalendarEvent("روز جمهوری اسلامی ایران", isHoliday = true, EventType.NATIONAL)),
        Pair(1, 13) to listOf(CalendarEvent("روز طبیعت (سیزده‌بدر)", isHoliday = true, EventType.NATIONAL)),
        Pair(1, 18) to listOf(CalendarEvent("روز سلامتی و بهداشت", isHoliday = false, EventType.NATIONAL), CalendarEvent("روز جهانی بهداشت", isHoliday = false, EventType.NATIONAL)),
        Pair(1, 25) to listOf(CalendarEvent("روز بزرگداشت عطار نیشابوری", isHoliday = false, EventType.NATIONAL)),
        Pair(1, 29) to listOf(CalendarEvent("روز ارتش جمهوری اسلامی ایران", isHoliday = false, EventType.NATIONAL)),
        Pair(2, 1) to listOf(CalendarEvent("روز بزرگداشت سعدی شیرازی", isHoliday = false, EventType.NATIONAL)),
        Pair(2, 5) to listOf(CalendarEvent("سالروز تاسیس سپاه پاسداران انقلاب اسلامی", isHoliday = false, EventType.NATIONAL)),
        Pair(2, 3) to listOf(CalendarEvent("روز بزرگداشت شیخ بهایی و روز معمار", isHoliday = false, EventType.NATIONAL)),
        Pair(2, 10) to listOf(CalendarEvent("روز ملی خلیج فارس", isHoliday = false, EventType.NATIONAL)),
        Pair(2, 12) to listOf(CalendarEvent("روز بزرگداشت استاد مطهری و روز معلم", isHoliday = false, EventType.NATIONAL)),
        Pair(2, 25) to listOf(CalendarEvent("روز بزرگداشت فردوسی و پاسداشت زبان فارسی", isHoliday = false, EventType.NATIONAL)),
        Pair(2, 28) to listOf(CalendarEvent("روز بزرگداشت حکیم عمر خیام نیشابوری", isHoliday = false, EventType.NATIONAL)),
        Pair(3, 3) to listOf(CalendarEvent("فتح خرمشهر در عملیات بیت‌المقدس و روز مقاومت", isHoliday = false, EventType.NATIONAL)),
        Pair(3, 14) to listOf(CalendarEvent("رحلت حضرت امام خمینی (ره)", isHoliday = true, EventType.NATIONAL)),
        Pair(3, 15) to listOf(CalendarEvent("قیام خونین ۱۵ خرداد", isHoliday = true, EventType.NATIONAL)),
        Pair(4, 1) to listOf(CalendarEvent("روز اصناف", isHoliday = false, EventType.NATIONAL)),
        Pair(4, 7) to listOf(CalendarEvent("شهادت آیت‌الله بهشتی و روز قوه قضاییه", isHoliday = false, EventType.NATIONAL)),
        Pair(4, 13) to listOf(CalendarEvent("جشن تیرگان (جشن باستانی آب)", isHoliday = false, EventType.NATIONAL)),
        Pair(4, 14) to listOf(CalendarEvent("روز قلم", isHoliday = false, EventType.NATIONAL)),
        Pair(4, 25) to listOf(CalendarEvent("روز بهزیستی و تامین اجتماعی", isHoliday = false, EventType.NATIONAL)),
        Pair(5, 6) to listOf(CalendarEvent("روز ترویج آموزش‌های فنی و حرفه‌ای", isHoliday = false, EventType.NATIONAL)),
        Pair(5, 8) to listOf(CalendarEvent("روز بزرگداشت شیخ شهاب‌الدین سهروردی", isHoliday = false, EventType.NATIONAL)),
        Pair(5, 14) to listOf(CalendarEvent("صدور فرمان مشروطیت", isHoliday = false, EventType.NATIONAL)),
        Pair(5, 17) to listOf(CalendarEvent("روز خبرنگار", isHoliday = false, EventType.NATIONAL)),
        Pair(6, 1) to listOf(CalendarEvent("روز بزرگداشت ابوعلی سینا و روز پزشک", isHoliday = false, EventType.NATIONAL)),
        Pair(6, 2) to listOf(CalendarEvent("روز کارمند و آغاز هفته دولت", isHoliday = false, EventType.NATIONAL)),
        Pair(6, 4) to listOf(CalendarEvent("جشن شهریورگان (جشن باستانی)", isHoliday = false, EventType.NATIONAL)),
        Pair(6, 5) to listOf(CalendarEvent("روز بزرگداشت زکریای رازی و روز داروساز", isHoliday = false, EventType.NATIONAL)),
        Pair(6, 8) to listOf(CalendarEvent("روز مبارزه با تروریسم", isHoliday = false, EventType.NATIONAL)),
        Pair(6, 13) to listOf(CalendarEvent("روز بزرگداشت ابوریحان بیرونی / روز ملی نجوم ایران", isHoliday = false, EventType.ASTRONOMY)),
        Pair(6, 27) to listOf(CalendarEvent("روز شعر و ادب فارسی / بزرگداشت استاد شهریار", isHoliday = false, EventType.NATIONAL)),
        Pair(6, 31) to listOf(CalendarEvent("آغاز هفته دفاع مقدس", isHoliday = false, EventType.NATIONAL)),
        Pair(7, 1) to listOf(CalendarEvent("آغاز پاییز و آغاز سال تحصیلی جدید", isHoliday = false, EventType.NATIONAL)),
        Pair(7, 16) to listOf(CalendarEvent("جشن مهرگان (جشن باستانی)", isHoliday = false, EventType.NATIONAL)),
        Pair(7, 8) to listOf(CalendarEvent("روز بزرگداشت مولوی (جلال‌الدین بلخی)", isHoliday = false, EventType.NATIONAL)),
        Pair(7, 20) to listOf(CalendarEvent("روز بزرگداشت حافظ شیرازی", isHoliday = false, EventType.NATIONAL)),
        Pair(8, 7) to listOf(CalendarEvent("روز بزرگداشت کوروش بزرگ", isHoliday = false, EventType.NATIONAL)),
        Pair(8, 8) to listOf(CalendarEvent("روز نوجوان و بسیج دانش‌آموزی", isHoliday = false, EventType.NATIONAL)),
        Pair(8, 13) to listOf(CalendarEvent("روز دانش‌آموز و تسخیر لانه جاسوسی", isHoliday = false, EventType.NATIONAL)),
        Pair(8, 24) to listOf(CalendarEvent("روز کتاب، کتابخوانی و کتابدار / بزرگداشت علامه طباطبایی", isHoliday = false, EventType.NATIONAL)),
        Pair(9, 5) to listOf(CalendarEvent("روز بسیج مستضعفان", isHoliday = false, EventType.NATIONAL)),
        Pair(9, 7) to listOf(CalendarEvent("روز نیروی دریایی", isHoliday = false, EventType.NATIONAL)),
        Pair(9, 16) to listOf(CalendarEvent("روز دانشجو", isHoliday = false, EventType.NATIONAL)),
        Pair(9, 25) to listOf(CalendarEvent("روز پژوهش", isHoliday = false, EventType.NATIONAL)),
        Pair(9, 30) to listOf(CalendarEvent("شب یلدا / جشن باستانی چله", isHoliday = false, EventType.NATIONAL)),
        Pair(10, 1) to listOf(CalendarEvent("جشن دیگان / آغاز دی‌ماه", isHoliday = false, EventType.NATIONAL)),
        Pair(11, 10) to listOf(CalendarEvent("جشن سده (جشن باستانی آتش)", isHoliday = false, EventType.NATIONAL)),
        Pair(11, 12) to listOf(CalendarEvent("بازگشت امام خمینی به میهن و آغاز دهه فجر", isHoliday = false, EventType.NATIONAL)),
        Pair(11, 19) to listOf(CalendarEvent("روز نیروی هوایی", isHoliday = false, EventType.NATIONAL)),
        Pair(11, 22) to listOf(CalendarEvent("سالروز پیروزی شکوهمند انقلاب اسلامی", isHoliday = true, EventType.NATIONAL)),
        Pair(12, 5) to listOf(CalendarEvent("روز مهندس و بزرگداشت خواجه نصیرالدین طوسی / جشن سپندارمذگان (روز عشق و زمین)", isHoliday = false, EventType.NATIONAL)),
        Pair(12, 14) to listOf(CalendarEvent("روز احسان و نیکوکاری", isHoliday = false, EventType.NATIONAL)),
        Pair(12, 15) to listOf(CalendarEvent("روز درختکاری و هفته منابع طبیعی", isHoliday = false, EventType.NATIONAL)),
        Pair(12, 25) to listOf(CalendarEvent("روز بزرگداشت پروین اعتصامی", isHoliday = false, EventType.NATIONAL)),
                Pair(1, 6) to listOf(CalendarEvent("زادروز زرتشت پیامبر ایران باستان", isHoliday = false, EventType.NATIONAL)),
        Pair(1, 20) to listOf(CalendarEvent("روز ملی فناوری هسته‌ای", isHoliday = false, EventType.NATIONAL)),
        Pair(2, 11) to listOf(CalendarEvent("روز جهانی کارگر", isHoliday = false, EventType.NATIONAL)),
        Pair(2, 7) to listOf(CalendarEvent("روز شوراها", isHoliday = false, EventType.NATIONAL)),
        Pair(5, 7) to listOf(CalendarEvent("جشن مردادگان (جشن باستانی)", isHoliday = false, EventType.NATIONAL)),
        Pair(8, 10) to listOf(CalendarEvent("جشن آبانگان (جشن باستانی)", isHoliday = false, EventType.NATIONAL)),
        Pair(9, 9) to listOf(CalendarEvent("جشن آذرگان (جشن باستانی آتش)", isHoliday = false, EventType.NATIONAL)),
        Pair(10, 19) to listOf(CalendarEvent("قیام ۱۹ دی مردم قم", isHoliday = false, EventType.NATIONAL)),
        Pair(11, 2) to listOf(CalendarEvent("جشن بهمنگان (جشن باستانی)", isHoliday = false, EventType.NATIONAL)),
        Pair(3, 27) to listOf(CalendarEvent("روز جهاد کشاورزی", isHoliday = false, EventType.NATIONAL)),
Pair(12, 29) to listOf(CalendarEvent("روز ملی شدن صنعت نفت ایران", isHoliday = true, EventType.NATIONAL)),
        Pair(7, 7) to listOf(CalendarEvent("روز آتش‌نشانی و ایمنی", isHoliday = false, EventType.NATIONAL)),
        Pair(2, 9) to listOf(CalendarEvent("روز ارتباطات و روابط عمومی", isHoliday = false, EventType.NATIONAL)),
        Pair(2, 18) to listOf(CalendarEvent("روز هلال احمر", isHoliday = false, EventType.NATIONAL)),
        Pair(3, 1) to listOf(CalendarEvent("روز بزرگداشت ملاصدرا", isHoliday = false, EventType.NATIONAL)),
        Pair(6, 17) to listOf(CalendarEvent("قیام ۱۷ شهریور (جمعه سیاه)", isHoliday = false, EventType.NATIONAL)),
        Pair(10, 9) to listOf(CalendarEvent("روز بصیرت و میثاق امت با ولایت", isHoliday = false, EventType.NATIONAL)),
        Pair(10, 13) to listOf(CalendarEvent("سالروز شهادت سپهبد قاسم سلیمانی", isHoliday = false, EventType.NATIONAL)),
        Pair(11, 29) to listOf(CalendarEvent("قیام مردم تبریز (۲۹ بهمن)", isHoliday = false, EventType.NATIONAL)),
        Pair(7, 13) to listOf(CalendarEvent("روز جهانی معلم", isHoliday = false, EventType.NATIONAL)),
        Pair(7, 14) to listOf(CalendarEvent("روز دامپزشکی", isHoliday = false, EventType.NATIONAL))
    )

    // --------------------------------------------------------------------
    // رویدادها و تعطیلات مذهبی رسمی ایران بر پایه تقویم قمری
    // --------------------------------------------------------------------
    private val ISLAMIC_EVENTS = mapOf(
        Pair(1, 9) to listOf(CalendarEvent("تاسوعای حسینی", isHoliday = true, EventType.RELIGIOUS)),
        Pair(1, 10) to listOf(CalendarEvent("عاشورای حسینی", isHoliday = true, EventType.RELIGIOUS)),
        Pair(1, 25) to listOf(CalendarEvent("شهادت امام زین‌العابدین (ع)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(2, 20) to listOf(CalendarEvent("اربعین حسینی", isHoliday = true, EventType.RELIGIOUS)),
        Pair(2, 28) to listOf(CalendarEvent("رحلت پیامبر اکرم (ص) و شهادت امام حسن مجتبی (ع)", isHoliday = true, EventType.RELIGIOUS)),
        Pair(2, 30) to listOf(CalendarEvent("شهادت امام رضا (ع)", isHoliday = true, EventType.RELIGIOUS)),
        Pair(3, 1) to listOf(CalendarEvent("هجرت پیامبر اکرم از مکه به مدینه", isHoliday = false, EventType.RELIGIOUS)),
        Pair(3, 8) to listOf(CalendarEvent("شهادت امام حسن عسکری (ع)", isHoliday = true, EventType.RELIGIOUS)),
        Pair(3, 9) to listOf(CalendarEvent("آغاز امامت حضرت ولی‌عصر (عج)", isHoliday = false, EventType.RELIGIOUS), CalendarEvent("عید الزهرا", isHoliday = false, EventType.RELIGIOUS)),
        Pair(3, 17) to listOf(CalendarEvent("میلاد حضرت رسول اکرم (ص) و ولادت امام جعفر صادق (ع)", isHoliday = true, EventType.RELIGIOUS)),
        Pair(4, 8) to listOf(CalendarEvent("ولادت حضرت امام حسن عسکری (ع)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(4, 10) to listOf(CalendarEvent("وفات حضرت معصومه (س)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(5, 5) to listOf(CalendarEvent("ولادت حضرت زینب کبری (س) و روز پرستار", isHoliday = false, EventType.RELIGIOUS)),
        Pair(6, 3) to listOf(CalendarEvent("شهادت حضرت فاطمه زهرا (س)", isHoliday = true, EventType.RELIGIOUS)),
        Pair(6, 20) to listOf(CalendarEvent("ولادت حضرت فاطمه زهرا (س) و روز مادر و زن", isHoliday = false, EventType.RELIGIOUS)),
        Pair(7, 1) to listOf(CalendarEvent("ولادت حضرت امام محمد باقر (ع)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(7, 3) to listOf(CalendarEvent("شهادت امام علی‌النقی الهادی (ع)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(7, 10) to listOf(CalendarEvent("ولادت امام محمد تقی جوادالائمه (ع)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(7, 13) to listOf(CalendarEvent("ولادت حضرت امام علی (ع) و روز پدر", isHoliday = true, EventType.RELIGIOUS)),
        Pair(7, 15) to listOf(CalendarEvent("وفات حضرت زینب کبری (س)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(7, 25) to listOf(CalendarEvent("شهادت امام موسی کاظم (ع)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(7, 27) to listOf(CalendarEvent("مبعث رسول گرامی اسلام (ص)", isHoliday = true, EventType.RELIGIOUS)),
        Pair(8, 3) to listOf(CalendarEvent("ولادت امام حسین (ع) و روز پاسدار", isHoliday = false, EventType.RELIGIOUS)),
        Pair(8, 4) to listOf(CalendarEvent("ولادت حضرت ابوالفضل العباس (ع) و روز جانباز", isHoliday = false, EventType.RELIGIOUS)),
        Pair(8, 5) to listOf(CalendarEvent("ولادت امام زین‌العابدین (ع)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(8, 11) to listOf(CalendarEvent("ولادت حضرت علی‌اکبر (ع) و روز جوان", isHoliday = false, EventType.RELIGIOUS)),
        Pair(8, 15) to listOf(CalendarEvent("ولادت حضرت مهدی موعود (عج) / نیمه شعبان", isHoliday = true, EventType.RELIGIOUS)),
        Pair(9, 1) to listOf(CalendarEvent("آغاز ماه مبارک رمضان", isHoliday = false, EventType.RELIGIOUS)),
        Pair(9, 10) to listOf(CalendarEvent("رحلت حضرت خدیجه کبری (س)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(9, 15) to listOf(CalendarEvent("ولادت حضرت امام حسن مجتبی (ع)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(9, 19) to listOf(CalendarEvent("ضربت خوردن حضرت علی (ع) / شب قدر اول", isHoliday = false, EventType.RELIGIOUS)),
        Pair(9, 21) to listOf(CalendarEvent("شهادت حضرت علی (ع) / شب قدر دوم", isHoliday = true, EventType.RELIGIOUS)),
        Pair(9, 23) to listOf(CalendarEvent("شب قدر سوم", isHoliday = false, EventType.RELIGIOUS)),
        Pair(10, 1) to listOf(CalendarEvent("عید سعید فطر", isHoliday = true, EventType.RELIGIOUS)),
        Pair(10, 2) to listOf(CalendarEvent("تعطیلی به مناسبت عید سعید فطر", isHoliday = true, EventType.RELIGIOUS)),
        Pair(10, 25) to listOf(CalendarEvent("شهادت حضرت امام جعفر صادق (ع)", isHoliday = true, EventType.RELIGIOUS)),
        Pair(11, 1) to listOf(CalendarEvent("ولادت حضرت فاطمه معصومه (س) و روز دختران", isHoliday = false, EventType.RELIGIOUS)),
        Pair(11, 11) to listOf(CalendarEvent("ولادت حضرت ثامن‌الحجج امام رضا (ع)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(11, 29) to listOf(CalendarEvent("شهادت امام محمدتقی جوادالائمه (ع)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(12, 1) to listOf(CalendarEvent("سالروز ازدواج حضرت علی (ع) و حضرت فاطمه (س)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(12, 7) to listOf(CalendarEvent("شهادت امام محمدباقر (ع)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(12, 9) to listOf(CalendarEvent("روز عرفه", isHoliday = false, EventType.RELIGIOUS)),
        Pair(12, 10) to listOf(CalendarEvent("عید سعید قربان", isHoliday = true, EventType.RELIGIOUS)),
        Pair(12, 15) to listOf(CalendarEvent("ولادت امام علی‌النقی الهادی (ع)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(12, 18) to listOf(CalendarEvent("عید بزرگ ولایت، عید سعید غدیر خم", isHoliday = true, EventType.RELIGIOUS)),
        Pair(12, 24) to listOf(CalendarEvent("روز مباهله پیامبر اکرم (ص)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(2, 13) to listOf(CalendarEvent("وفات حضرت رقیه (س)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(12, 8) to listOf(CalendarEvent("روز ترویه", isHoliday = false, EventType.RELIGIOUS)),
        Pair(1, 1) to listOf(CalendarEvent("آغاز ماه محرم و سال جدید هجری قمری", isHoliday = false, EventType.RELIGIOUS)),
        Pair(2, 1) to listOf(CalendarEvent("ورود اهل بیت (ع) به شام", isHoliday = false, EventType.RELIGIOUS)),
        Pair(2, 7) to listOf(CalendarEvent("ولادت امام موسی کاظم (ع)", isHoliday = false, EventType.RELIGIOUS)),
        Pair(3, 12) to listOf(CalendarEvent("آغاز هفته وحدت", isHoliday = false, EventType.RELIGIOUS)),
        Pair(5, 13) to listOf(CalendarEvent("شهادت حضرت فاطمه زهرا (س) به روایت اول", isHoliday = false, EventType.RELIGIOUS)),
        Pair(11, 25) to listOf(CalendarEvent("روز دحوالارض", isHoliday = false, EventType.RELIGIOUS)),
        Pair(9, 17) to listOf(CalendarEvent("سالروز غزوه بدر", isHoliday = false, EventType.RELIGIOUS))
    )

    // --------------------------------------------------------------------
    // مناسبت‌ها و روزهای جهانی (International Occasions) بر پایه تقویم میلادی
    // --------------------------------------------------------------------
    private val GLOBAL_EVENTS = mapOf(
        Pair(1, 1) to listOf(CalendarEvent("روز جهانی آغاز سال نو میلادی (New Year)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(1, 4) to listOf(CalendarEvent("روز جهانی خط بریل (World Braille Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(1, 24) to listOf(CalendarEvent("روز بین‌المللی آموزش (Education Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(1, 27) to listOf(CalendarEvent("روز جهانی یادبود قربانیان هولوکاست", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(2, 2) to listOf(CalendarEvent("روز جهانی تالاب‌ها (World Wetlands Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(2, 4) to listOf(CalendarEvent("روز جهانی مبارزه با سرطان (World Cancer Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(2, 11) to listOf(CalendarEvent("روز بین‌المللی زنان و دختران در علم", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(2, 13) to listOf(CalendarEvent("روز جهانی رادیو (World Radio Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(2, 14) to listOf(CalendarEvent("روز جهانی عشق و مهرورزی (Valentine's Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(2, 20) to listOf(CalendarEvent("روز جهانی عدالت اجتماعی (Social Justice Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(2, 21) to listOf(CalendarEvent("روز بین‌المللی زبان مادری (Mother Language Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(3, 3) to listOf(CalendarEvent("روز جهانی حیات وحش (World Wildlife Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(3, 8) to listOf(CalendarEvent("روز جهانی زن (International Women's Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(3, 14) to listOf(CalendarEvent("روز بین‌المللی ریاضیات و عدد پی (Pi Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(3, 20) to listOf(CalendarEvent("روز جهانی شادی (International Day of Happiness)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(3, 21) to listOf(
            CalendarEvent("روز جهانی نوروز (International Day of Nowruz)", isHoliday = false, EventType.INTERNATIONAL),
            CalendarEvent("روز جهانی شعر (World Poetry Day)", isHoliday = false, EventType.INTERNATIONAL)
        ),
        Pair(3, 22) to listOf(CalendarEvent("روز جهانی آب (World Water Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(3, 23) to listOf(CalendarEvent("روز جهانی هواشناسی (Meteorological Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(3, 27) to listOf(CalendarEvent("روز جهانی تئاتر و هنرهای نمایشی", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(4, 7) to listOf(CalendarEvent("روز جهانی بهداشت و سلامت (World Health Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(4, 12) to listOf(CalendarEvent("روز جهانی فضانوردی و پرواز انسان به فضا", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(4, 15) to listOf(CalendarEvent("روز جهانی هنر (World Art Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(4, 22) to listOf(CalendarEvent("روز جهانی زمین پاک (Earth Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(4, 23) to listOf(CalendarEvent("روز جهانی کتاب و کپی‌رایت (World Book Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(4, 29) to listOf(CalendarEvent("روز جهانی رقص (International Dance Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(5, 1) to listOf(CalendarEvent("روز جهانی کار و کارگر (International Workers' Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(5, 3) to listOf(CalendarEvent("روز جهانی آزادی مطبوعات (Press Freedom Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(5, 8) to listOf(CalendarEvent("روز جهانی صلیب سرخ و هلال احمر", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(5, 15) to listOf(CalendarEvent("روز بین‌المللی خانواده (International Day of Families)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(5, 17) to listOf(CalendarEvent("روز جهانی ارتباطات و جامعه اطلاعاتی", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(5, 21) to listOf(CalendarEvent("روز جهانی گفت‌وگو و تنوع فرهنگی", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(5, 22) to listOf(CalendarEvent("روز بین‌المللی تنوع زیستی (Biological Diversity)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(5, 31) to listOf(CalendarEvent("روز جهانی بدون دخانیات (World No Tobacco Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(6, 1) to listOf(CalendarEvent("روز جهانی والدین و روز جهانی شیر", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(6, 3) to listOf(CalendarEvent("روز جهانی دوچرخه‌سواری (World Bicycle Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(6, 5) to listOf(CalendarEvent("روز جهانی محیط زیست (World Environment Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(6, 8) to listOf(CalendarEvent("روز جهانی اقیانوس‌ها (World Oceans Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(6, 12) to listOf(CalendarEvent("روز جهانی مبارزه با کار کودکان", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(6, 14) to listOf(CalendarEvent("روز جهانی اهدای خون (Blood Donor Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(6, 20) to listOf(CalendarEvent("روز جهانی پناهندگان (World Refugee Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(6, 21) to listOf(
            CalendarEvent("روز جهانی موسیقی (World Music Day)", isHoliday = false, EventType.INTERNATIONAL),
            CalendarEvent("روز بین‌المللی یوگا (International Day of Yoga)", isHoliday = false, EventType.INTERNATIONAL)
        ),
        Pair(6, 23) to listOf(CalendarEvent("روز جهانی المپیک (Olympic Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(6, 30) to listOf(CalendarEvent("روز بین‌المللی سیارک‌ها (Asteroid Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(7, 11) to listOf(CalendarEvent("روز جهانی جمعیت (World Population Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(7, 15) to listOf(CalendarEvent("روز جهانی مهارت‌های جوانان (Youth Skills Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(7, 20) to listOf(
            CalendarEvent("روز جهانی شطرنج (World Chess Day)", isHoliday = false, EventType.INTERNATIONAL),
            CalendarEvent("روز بین‌المللی کاوش ماه (Moon Day)", isHoliday = false, EventType.INTERNATIONAL)
        ),
        Pair(7, 30) to listOf(CalendarEvent("روز بین‌المللی دوستی (Friendship Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(8, 9) to listOf(CalendarEvent("روز بین‌المللی مردم بومی جهان", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(8, 12) to listOf(CalendarEvent("روز بین‌المللی جوانان (International Youth Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(8, 13) to listOf(CalendarEvent("روز جهانی چپ‌دست‌ها (Left-Handers Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(8, 19) to listOf(
            CalendarEvent("روز جهانی عکاسی (World Photography Day)", isHoliday = false, EventType.INTERNATIONAL),
            CalendarEvent("روز جهانی بشردوستی (Humanitarian Day)", isHoliday = false, EventType.INTERNATIONAL)
        ),
        Pair(9, 5) to listOf(CalendarEvent("روز بین‌المللی خیریه (International Day of Charity)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(9, 8) to listOf(CalendarEvent("روز جهانی سوادآموزی (International Literacy Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(9, 15) to listOf(CalendarEvent("روز بین‌المللی دموکراسی (Democracy Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(9, 16) to listOf(CalendarEvent("روز بین‌المللی حفاظت از لایه ازون", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(9, 21) to listOf(
            CalendarEvent("روز بین‌المللی صلح (International Day of Peace)", isHoliday = false, EventType.INTERNATIONAL),
            CalendarEvent("روز جهانی آلزایمر (World Alzheimer's Day)", isHoliday = false, EventType.INTERNATIONAL)
        ),
        Pair(9, 24) to listOf(CalendarEvent("روز جهانی دریانوردی و روز پژوهشگران (World Maritime Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(9, 27) to listOf(CalendarEvent("روز جهانی گردشگری (World Tourism Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(9, 29) to listOf(CalendarEvent("روز جهانی قلب (World Heart Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(9, 30) to listOf(CalendarEvent("روز جهانی ترجمه (Translation Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(10, 1) to listOf(
            CalendarEvent("روز بین‌المللی سالمندان (Older Persons Day)", isHoliday = false, EventType.INTERNATIONAL),
            CalendarEvent("روز جهانی قهوه (International Coffee Day)", isHoliday = false, EventType.INTERNATIONAL)
        ),
        Pair(10, 4) to listOf(CalendarEvent("آغاز هفته جهانی فضا (World Space Week)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(10, 5) to listOf(CalendarEvent("روز جهانی معلم (World Teachers' Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(10, 9) to listOf(CalendarEvent("روز جهانی پست (World Post Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(10, 10) to listOf(CalendarEvent("روز جهانی سلامت روان (Mental Health Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(10, 11) to listOf(CalendarEvent("روز بین‌المللی فرزند دختر (Day of the Girl)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(10, 15) to listOf(CalendarEvent("روز جهانی نابینایان و عصای سفید", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(10, 16) to listOf(CalendarEvent("روز جهانی غذا (World Food Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(10, 24) to listOf(CalendarEvent("روز سازمان ملل متحد (United Nations Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(10, 31) to listOf(CalendarEvent("جشن هالووین (Halloween)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(11, 10) to listOf(CalendarEvent("روز جهانی علم در خدمت صلح و توسعه", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(11, 14) to listOf(CalendarEvent("روز جهانی دیابت (World Diabetes Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(11, 16) to listOf(CalendarEvent("روز بین‌المللی بردباری و مدارا (Tolerance Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(11, 17) to listOf(CalendarEvent("روز بین‌المللی دانش‌آموزان (Students' Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(11, 19) to listOf(CalendarEvent("روز بین‌المللی مردان (International Men's Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(11, 20) to listOf(CalendarEvent("روز جهانی کودک (World Children's Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(11, 21) to listOf(
            CalendarEvent("روز جهانی تلویزیون (World Television Day)", isHoliday = false, EventType.INTERNATIONAL),
            CalendarEvent("روز جهانی فلسفه (World Philosophy Day)", isHoliday = false, EventType.INTERNATIONAL)
        ),
        Pair(11, 25) to listOf(CalendarEvent("روز جهانی مبارزه با خشونت علیه زنان", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(12, 1) to listOf(CalendarEvent("روز جهانی ایدز (World AIDS Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(12, 3) to listOf(CalendarEvent("روز بین‌المللی افراد دارای معلولیت", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(12, 5) to listOf(CalendarEvent("روز جهانی خاک و روز داوطلبان", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(12, 7) to listOf(CalendarEvent("روز بین‌المللی هوانوردی غیرنظامی", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(12, 10) to listOf(CalendarEvent("روز جهانی حقوق بشر (Human Rights Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(12, 11) to listOf(CalendarEvent("روز بین‌المللی کوهستان (Mountain Day)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(12, 18) to listOf(
            CalendarEvent("روز جهانی زبان عربی (Arabic Language Day)", isHoliday = false, EventType.INTERNATIONAL),
            CalendarEvent("روز بین‌المللی مهاجران (Migrants Day)", isHoliday = false, EventType.INTERNATIONAL)
        ),
        Pair(12, 25) to listOf(CalendarEvent("میلاد حضرت عیسی مسیح (ع) و جشن کریسمس (Christmas)", isHoliday = false, EventType.INTERNATIONAL)),
        Pair(12, 31) to listOf(CalendarEvent("شب سال نو میلادی (New Year's Eve)", isHoliday = false, EventType.INTERNATIONAL))
    )

    // --------------------------------------------------------------------
    // رویدادهای نجومی و رصدی شاخص
    // --------------------------------------------------------------------
    private val ASTRONOMICAL_EVENTS = mapOf(
        Pair(1, 1) to CalendarEvent("اعتدال بهاری (لحظه تحویل سال / برابری طول روز و شب)", false, EventType.ASTRONOMY),
        Pair(2, 16) to CalendarEvent("اوج بارش شهابی اتا دلوی", false, EventType.ASTRONOMY),
        Pair(4, 1) to CalendarEvent("انقلاب تابستانی (بلندترین روز سال در نیمکره شمالی)", false, EventType.ASTRONOMY),
        Pair(5, 21) to CalendarEvent("اوج بارش شهابی برساوشی (از زیباترین بارش‌های شهابی سال)", false, EventType.ASTRONOMY),
        Pair(7, 1) to CalendarEvent("اعتدال پاییزی (برابری مجدد طول روز و شب)", false, EventType.ASTRONOMY),
        Pair(7, 29) to CalendarEvent("اوج بارش شهابی جباری", false, EventType.ASTRONOMY),
        Pair(8, 26) to CalendarEvent("اوج بارش شهابی اسدی", false, EventType.ASTRONOMY),
        Pair(9, 23) to CalendarEvent("اوج بارش شهابی جوزایی (پرشمارترین بارش شهابی سال)", false, EventType.ASTRONOMY),
        Pair(10, 1) to CalendarEvent("انقلاب زمستانی (کوتاه‌ترین روز و بلندترین شب سال)", false, EventType.ASTRONOMY),
        Pair(10, 14) to CalendarEvent("اوج بارش شهابی ربعی", false, EventType.ASTRONOMY)
    )
}
