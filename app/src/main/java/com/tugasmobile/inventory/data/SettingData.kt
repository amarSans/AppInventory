package com.tugasmobile.inventory.data

import android.database.Cursor

data class SettingData(
    val isNotifEnabled: Boolean,
    val notifTime: String,
    val startDay: String,
    val endDay: String
) ;
