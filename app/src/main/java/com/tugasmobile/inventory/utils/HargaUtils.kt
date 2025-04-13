package com.tugasmobile.inventory.utils

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
                    val cleanString = s.toString().replace(".", "")
                    val parsed = cleanString.toLong()
                    val formatted = formatHarga(parsed.toInt())

                    editText.setText(formatted)
                    editText.setSelection(formatted.length)
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }

                editText.addTextChangedListener(this)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}