package com.tugasmobile.inventory.adapter


import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.R

class AdapterColorOut(
    private val context: Context,
    private var selectedColorNames: List<String> = listOf(),
    private var selectedColorValues: List<String> = listOf()
) : RecyclerView.Adapter<AdapterColorOut.ColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_color_out, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val colorName = selectedColorNames[position]
        val colorValue = selectedColorValues[position]

        holder.bind(colorName, colorValue)


    }

    override fun getItemCount(): Int = selectedColorNames.size

    fun updateColors(colorNames: List<String>, colorValues: List<String>) {
        selectedColorNames = colorNames
        selectedColorValues = colorValues
        notifyDataSetChanged()
    }

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorItem: TextView = itemView.findViewById(R.id.textViewColorName)

        fun bind(colorName: String, colorValue: String) {
            colorItem.text = colorName
            val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.bacground_warna)?.mutate()
            colorItem.setBackgroundColor(Color.parseColor(colorValue))
            colorItem.background = backgroundDrawable
            colorItem.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorValue))
            }
    }
}

