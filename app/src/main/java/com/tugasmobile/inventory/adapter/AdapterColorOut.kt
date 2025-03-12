package com.tugasmobile.inventory.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.tugasmobile.inventory.R

class AdapterColorOut(
    private val context: Context,
    private val selectedColors: MutableList<String> = mutableListOf()
) : RecyclerView.Adapter<AdapterColorOut.ColorViewHolder>() {
    private var colors: List<Pair<String, String>> = emptyList()

    fun updateColors(colorNames: List<String>, colorValues: List<String>) {
        Log.d("AdapterColorOut", "updateColors dipanggil")
        Log.d("AdapterColorOut", "colorNames: $colorNames")
        Log.d("AdapterColorOut", "colorValues: $colorValues")


        selectedColors.clear()
        colors = colorNames.zip(colorValues)
        Log.d("AdapterColorOut", "Colors setelah zip: $colors")
        notifyDataSetChanged()
        Log.d("AdapterColorOut", "Jumlah item setelah update: ${colors.size}")
    }
    fun getSelectedColors(): List<String> {
        return selectedColors.toList()
    }

    inner class ColorViewHolder(val chip: Chip) : RecyclerView.ViewHolder(chip)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        Log.d("AdapterColorOut", "onCreateViewHolder dipanggil")
        Log.d("AdapterColorOut", "onCreateViewHolder - colors: $colors")
        val chip = LayoutInflater.from(context).inflate(R.layout.item_color, parent, false) as Chip
        return ColorViewHolder(chip)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        Log.d("AdapterColorOut", "onBindViewHolder - colors: $colors")
        val (colorName, colorValue) = colors[position]

        Log.d("AdapterColorOut", "onBindViewHolder - Posisi: $position, Warna: $colorName, Nilai: $colorValue")

        holder.chip.text = colorName
        holder.chip.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(colorValue))

        holder.chip.setOnCheckedChangeListener(null)

        holder.chip.isChecked = selectedColors.contains(colorName)
        Log.d("AdapterColorOut", "Chip Checked State: ${holder.chip.isChecked}")
        holder.chip.setOnCheckedChangeListener { _, isChecked ->
            Log.d("AdapterColorOut", "onCheckedChange - $colorName: $isChecked")
            if (isChecked) {
                if (!selectedColors.contains(colorName)) {
                    selectedColors.add(colorName) // Tambahkan jika dipilih
                }
            } else {
                selectedColors.remove(colorName) // Hapus jika tidak dipilih
            }
            Log.d("AdapterColorOut", "Selected Colors: $selectedColors")

        }
    }
    override fun getItemCount(): Int {
        Log.d("AdapterColorOut", "Jumlah item di adapter: ${colors.size}")
        return colors.size
    }



}
