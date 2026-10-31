package com.example.reelslock

import android.content.Context

object Prefs {
    private const val FILE = "reelslock"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_BLOCKS = "blocks_today"
    private const val KEY_DAY = "day"

    private fun sp(c: Context) = c.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun isEnabled(c: Context) = sp(c).getBoolean(KEY_ENABLED, true)
    fun setEnabled(c: Context, v: Boolean) = sp(c).edit().putBoolean(KEY_ENABLED, v).apply()

    private fun today() = java.time.LocalDate.now().toString()

    fun blocksToday(c: Context): Int {
        val p = sp(c)
        return if (p.getString(KEY_DAY, "") == today()) p.getInt(KEY_BLOCKS, 0) else 0
    }

    fun addBlock(c: Context) {
        val p = sp(c)
        val n = blocksToday(c) + 1
        p.edit().putString(KEY_DAY, today()).putInt(KEY_BLOCKS, n).apply()
    }
}
