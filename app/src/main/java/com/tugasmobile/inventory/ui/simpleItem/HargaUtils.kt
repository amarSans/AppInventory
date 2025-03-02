package com.tugasmobile.inventory.ui.simpleItem

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.NumberFormat
import java.util.Locale

object HargaUtils {

    fun formatHarga(harga: Int): String {
        val formatRupiah = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return formatRupiah.format(harga)
    }

    fun setupHargaTextWatcher(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) return
                editText.removeTextChangedListener(this)
                try {
                    val cleanString = s.toString().replace(".", "") // Hilangkan titik sebelumnya
                    val parsed = cleanString.toLong()
                    val formatted = formatHarga(parsed.toInt())

                    editText.setText(formatted)
                    editText.setSelection(formatted.length) // Geser cursor ke akhir
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }

                editText.addTextChangedListener(this) // Tambahkan kembali listener
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}