package com.example.calendar.core

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** Checks the latest GitHub Release of the project and compares it with the installed version. */
object UpdateChecker {
    private const val API = "https://api.github.com/repos/puny4official/PUNY4-Calendar/releases/latest"

    data class Result(val hasUpdate: Boolean, val latest: String, val notes: String, val downloadUrl: String)

    private fun parts(v: String): List<Int> =
        v.trim().removePrefix("v").removePrefix("V").split('.', '-', '_').map { it.toIntOrNull() ?: 0 }

    fun isNewer(latest: String, current: String): Boolean {
        val a = parts(latest); val b = parts(current)
        for (i in 0 until maxOf(a.size, b.size)) {
            val x = a.getOrElse(i) { 0 }; val y = b.getOrElse(i) { 0 }
            if (x != y) return x > y
        }
        return false
    }

    /** Blocking network call: run off the main thread. Returns null when the check fails. */
    fun check(currentVersion: String): Result? = runCatching {
        val c = URL(API).openConnection() as HttpURLConnection
        c.connectTimeout = 10000; c.readTimeout = 10000
        c.setRequestProperty("Accept", "application/vnd.github+json")
        if (c.responseCode != 200) return null
        val j = JSONObject(c.inputStream.bufferedReader().readText())
        val tag = j.optString("tag_name")
        var url = j.optString("html_url")
        val assets = j.optJSONArray("assets")
        if (assets != null) for (i in 0 until assets.length()) {
            val a = assets.getJSONObject(i)
            if (a.optString("name").endsWith(".apk")) { url = a.optString("browser_download_url"); break }
        }
        Result(isNewer(tag, currentVersion), tag, j.optString("body"), url)
    }.getOrNull()
}
