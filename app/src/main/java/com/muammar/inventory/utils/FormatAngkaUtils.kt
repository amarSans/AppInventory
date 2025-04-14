package com.muammar.inventory.utils

object FormatAngkaUtils {
    fun formatAngka(angka: Double): String {
        return if (angka % 1 == 0.0) {
            angka.toInt().toString()
        } else {
            angka.toString()
        }
    }
}