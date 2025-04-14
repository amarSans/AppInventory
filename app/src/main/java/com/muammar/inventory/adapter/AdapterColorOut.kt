package com.muammar.inventory.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.muammar.inventory.R

class AdapterColorOut(
    private val context: Context,
    private val selectedColors: MutableList<String> = mutableListOf()
) : RecyclerView.Adapter<AdapterColorOut.ColorViewHolder>() {
    private var colors: List<Pair<String, String>> = emptyList()

    fun updateColors(colorNames: List<String>, colorValues: List<String>) {

        selectedColors.clear()
        colors = colorNames.zip(colorValues)
        notifyDataSetChanged()
    }
    fun getSelectedColors(): List<String> {
        return selectedColors.toList()
    }

    inner class ColorViewHolder(val chip: Chip) : RecyclerView.ViewHolder(chip)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val chip = LayoutInflater.from(context).inflate(R.layout.item_color, parent, false) as Chip
        return ColorViewHolder(chip)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val (colorName, colorValue) = colors[position]


        holder.chip.text = colorName
        holder.chip.chipBackgroundColor = ColorStateList.valueOf(Color.parseColor(colorValue))

        holder.chip.setOnCheckedChangeListener(null)

        holder.chip.isChecked = selectedColors.contains(colorName)
        holder.chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!selectedColors.contains(colorName)) {
                    selectedColors.add(colorName)
                }
            } else {
                selectedColors.remove(colorName)
            }

        }
    }
    override fun getItemCount(): Int {
        return colors.size
    }
}
