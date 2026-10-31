package com.example.reelslock

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var status: TextView
    private lateinit var count: TextView
    private lateinit var toggle: Switch
    private lateinit var open: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pad = (24 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(pad, pad * 2, pad, pad)
        }

        val title = TextView(this).apply {
            text = "Reels Lock"
            textSize = 28f
        }
        status = TextView(this).apply { textSize = 16f; setPadding(0, pad, 0, pad / 2) }
        count = TextView(this).apply { textSize = 16f; setPadding(0, 0, 0, pad) }
        toggle = Switch(this).apply {
            text = "Block Reels"
            textSize = 18f
            setOnCheckedChangeListener { _, checked -> Prefs.setEnabled(this@MainActivity, checked) }
        }
        open = Button(this).apply {
            text = "Open Accessibility settings"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        }
        val hint = TextView(this).apply {
            text = "Turn on \"Reels Lock\" under Accessibility > Installed apps. " +
                "On Android 13+, if it is greyed out: App info > ⋮ > Allow restricted settings."
            textSize = 13f
            setPadding(0, pad, 0, 0)
        }

        listOf(title, status, count, toggle, open, hint).forEach { root.addView(it) }
        setContentView(root)
    }

    override fun onResume() {
        super.onResume()
        toggle.isChecked = Prefs.isEnabled(this)
        count.text = "Reels blocked today: ${Prefs.blocksToday(this)}"
        val on = isServiceEnabled()
        status.text = if (on) "Service is ON" else "Service is OFF - enable it in Accessibility settings"
        open.text = if (on) "Accessibility settings" else "Enable service"
    }

    private fun isServiceEnabled(): Boolean {
        val expected = ComponentName(this, ReelsBlockerService::class.java).flattenToString()
        val enabled = Settings.Secure.getString(
            contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabled.split(':').any { it.equals(expected, ignoreCase = true) }
    }
}
