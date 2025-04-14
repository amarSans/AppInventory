package com.muammar.inventory.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("session_pref", Context.MODE_PRIVATE)

    companion object {
        const val KEY_MODE = "mode_akses"
        const val MODE_LIHAT = "lihat"
        const val MODE_KELOLA = "kelola"
    }

    fun setMode(mode: String) {
        prefs.edit().putString(KEY_MODE, mode).apply()
    }

    fun getMode(): String {
        return prefs.getString(KEY_MODE, MODE_LIHAT) ?: MODE_LIHAT
    }

    fun isKelola(): Boolean {
        return getMode() == MODE_KELOLA
    }

    fun resetMode() {
        setMode(MODE_LIHAT)
    }
}
