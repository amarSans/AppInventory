package com.tugasmobile.inventory.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.R

class KarakteristikAdapter(
    private val karakteristikList: List<String>,
    private val selectedItems: MutableSet<String>,
    private val onItemChecked: (MutableSet<String>) -> Unit
) : RecyclerView.Adapter<KarakteristikAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkboxItem)

        fun bind(item: String) {
            checkBox.text = item
            checkBox.isChecked = selectedItems.contains(item)

            checkBox.setOnCheckedChangeListener(null) // Hindari callback ganda
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedItems.add(item)
                } else {
                    selectedItems.remove(item)
                }
                Log.d("KarakteristikAdapter", "Updated Selected Items: $selectedItems") // 🔥 Debugging

                onItemChecked(HashSet(selectedItems)) // 🔥 Kirim data terbaru ke Fragment
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_karakteristik_checkbox, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = karakteristikList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = karakteristikList.size
}
