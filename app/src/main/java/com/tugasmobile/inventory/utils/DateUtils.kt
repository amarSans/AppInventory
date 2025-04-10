package com.tugasmobile.inventory.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {
    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
    fun getDaysSince(dateString: String): Int {
        return try {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val lastUpdateDate = format.parse(dateString)
            val currentDate = format.parse(getCurrentDate())

            val diff = currentDate.time - lastUpdateDate.time
            (diff / (1000 * 60 * 60 * 24)).toInt() // Konversi millis ke hari
        } catch (e: Exception) {
            e.printStackTrace()
            -1 // Jika parsing gagal
        }
    }

}