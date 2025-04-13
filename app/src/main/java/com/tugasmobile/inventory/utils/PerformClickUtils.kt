package com.tugasmobile.inventory.utils

object PerformClickUtils {
    private var lastClickTime = 0L
    private const val MIN_CLICK_INTERVAL = 600L

    fun preventMultipleClick(action: () -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= MIN_CLICK_INTERVAL) {
            lastClickTime = currentTime
            action()
        }
    }
}