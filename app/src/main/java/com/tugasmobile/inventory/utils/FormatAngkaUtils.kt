package com.tugasmobile.inventory.utils

object FormatAngkaUtils {
    fun formatAngka(angka: Double): String {
        return if (angka % 1 == 0.0) {
            angka.toInt().toString() // buang .0
        } else {
            angka.toString() // tampilkan angka lengkap
        }
    }
}