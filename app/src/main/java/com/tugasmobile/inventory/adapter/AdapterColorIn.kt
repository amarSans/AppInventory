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

    private val selectedColors = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_warna, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(colorNames[position], colorValues[position])
        val colorName = colorNames[position] // Ambil nama warna untuk posisi saat ini
        holder.bind(colorName, colorValues[position])
        holder.checkBoxColor.isChecked = selectedColors.contains(colorName)
    }

    override fun getItemCount(): Int = colorNames.size

    fun getSelectedColors(): List<String> {
        return selectedColors.toList()
    }
    fun setSelectedColors(colors: List<String>) {
        selectedColors.clear()
        selectedColors.addAll(colors)
        notifyDataSetChanged()
    }
    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBoxColor: CheckBox = itemView.findViewById(R.id.checkBoxColor)

        fun bind(colorName: String, colorValue: String) {
            checkBoxColor.text = colorName
            val backgroundDrawable = checkBoxColor.background as? LayerDrawable
            val colorLayer = backgroundDrawable?.findDrawableByLayerId(R.id.color_layer)
            colorLayer?.setTint(Color.parseColor(colorValue))
            checkBoxColor.setOnCheckedChangeListener(null) // Hapus listener yang ada
            checkBoxColor.isChecked = selectedColors.contains(colorName)
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
