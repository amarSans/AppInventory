package com.tugasmobile.inventory.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.R

class AdapterColorIn(
    private val context: Context,
    private val colorNames: Array<String>,
    private val colorValues: Array<String>
) : RecyclerView.Adapter<AdapterColorIn.ColorViewHolder>() {

    // List untuk menyimpan warna yang dipilih
    private val selectedColors = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_warna, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colorNames[position], colorValues[position])
    }

    override fun getItemCount(): Int = colorNames.size

    fun getSelectedColors(): List<String> = selectedColors

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBoxColor: CheckBox = itemView.findViewById(R.id.checkBoxColor)

        fun bind(colorName: String, colorValue: String) {
            checkBoxColor.text = colorName
            //checkBoxColor.setBackgroundColor(Color.parseColor(colorValue))
            val backgroundDrawable = checkBoxColor.background as? LayerDrawable
            val colorLayer = backgroundDrawable?.findDrawableByLayerId(R.id.color_layer)
            colorLayer?.setTint(Color.parseColor(colorValue))



            // Mengatur listener untuk menambahkan atau menghapus warna yang dipilih
            checkBoxColor.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedColors.add(colorName)
                } else {
                    selectedColors.remove(colorName)
                }
            }
        }
    }
}
