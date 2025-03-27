package com.tugasmobile.inventory.ui.simpleItem

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatEditText
import com.tugasmobile.inventory.R

class CustomEditText : AppCompatEditText {

    // Constructor untuk inisialisasi dari kode
    constructor(context: Context) : super(context) {
        init()
    }

    // Constructor untuk inisialisasi dari XML
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    // Constructor untuk inisialisasi dari XML dengan style
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        // Atur ukuran teks berdasarkan lebar layar
        setAutoSizeText()
    }

    private fun setAutoSizeText() {
        // Dapatkan ukuran layar dalam dp
        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density

        // Sesuaikan ukuran teks berdasarkan lebar layar
        when {
            screenWidthDp > 600 -> setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_large))
            screenWidthDp > 400 -> setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_medium))
            screenWidthDp > 200 -> setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_small))
            else -> setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_medium))
        }
    }
}