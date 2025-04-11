package com.tugasmobile.inventory.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.databinding.ItemKarakteristikCheckboxBinding

class KarakteristikAdapter(
    private val karakteristikList: List<String>,
    private val selectedItems: MutableSet<String>,
    private val onItemChecked: (MutableSet<String>) -> Unit
) : RecyclerView.Adapter<KarakteristikAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemKarakteristikCheckboxBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.chipItem.text = item
            binding.chipItem.isChecked = selectedItems.contains(item)

            binding.chipItem.setOnCheckedChangeListener(null) // Hindari callback ganda
            binding.chipItem.setOnCheckedChangeListener { _, isChecked ->
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
        val binding = ItemKarakteristikCheckboxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = karakteristikList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = karakteristikList.size
}
