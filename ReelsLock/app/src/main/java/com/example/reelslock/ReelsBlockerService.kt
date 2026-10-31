package com.example.reelslock

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

/**
 * Watches the Instagram app. When the Reels viewer is on screen, taps the
 * Home tab (or presses Back as a fallback) so you land on the normal feed.
 * DMs, Stories and the regular feed keep working.
 *
 * Instagram can rename its internal view IDs in any update. If blocking stops
 * working, edit REELS_VIEW_IDS / REELS_TAB_IDS / HOME_TAB_IDS below.
 */
class ReelsBlockerService : AccessibilityService() {

    private val pkg = "com.instagram.android"

    // Views that only exist while the full-screen Reels viewer is showing.
    private val reelsViewIds = listOf(
        "$pkg:id/clips_viewer_view_pager",
        "$pkg:id/clips_viewer_container",
        "$pkg:id/clips_video_container"
    )

    // Bottom-bar tab buttons.
    private val reelsTabIds = listOf("$pkg:id/clips_tab")
    private val homeTabIds = listOf("$pkg:id/feed_tab")

    private var lastBlockAt = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.packageName?.toString() != pkg) return
        if (!Prefs.isEnabled(this)) return

        val root = rootInActiveWindow ?: return
        try {
            if (isReelsScreen(root)) blockReels(root)
        } finally {
            @Suppress("DEPRECATION")
            root.recycle()
        }
    }

    private fun isReelsScreen(root: AccessibilityNodeInfo): Boolean {
        // 1) The Reels pager is on screen.
        if (reelsViewIds.any { root.findAccessibilityNodeInfosByViewId(it).isNotEmpty() }) return true

        // 2) The Reels tab in the bottom bar is the selected one.
        for (id in reelsTabIds) {
            if (root.findAccessibilityNodeInfosByViewId(id).any { it.isSelected }) return true
        }
        return false
    }

    private fun blockReels(root: AccessibilityNodeInfo) {
        val now = System.currentTimeMillis()
        if (now - lastBlockAt < 800) return // avoid loops while the UI transitions
        lastBlockAt = now

        var clicked = false
        for (id in homeTabIds) {
            val tab = root.findAccessibilityNodeInfosByViewId(id).firstOrNull() ?: continue
            clicked = clickNodeOrParent(tab)
            if (clicked) break
        }
        if (!clicked) performGlobalAction(GLOBAL_ACTION_BACK)

        Prefs.addBlock(this)
        Toast.makeText(this, "Reels blocked", Toast.LENGTH_SHORT).show()
    }

    private fun clickNodeOrParent(node: AccessibilityNodeInfo): Boolean {
        var n: AccessibilityNodeInfo? = node
        while (n != null) {
            if (n.isClickable) return n.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            n = n.parent
        }
        return false
    }

    override fun onInterrupt() {}
}
