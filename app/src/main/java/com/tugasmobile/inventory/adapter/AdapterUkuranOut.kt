package com.tugasmobile.inventory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.data.UkuranItem
import com.tugasmobile.inventory.databinding.ItemUkuranCheckboxBinding

class AdapterUkuranOut (private val ukuranList: List<UkuranItem>) :
    RecyclerView.Adapter<AdapterUkuranOut.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemUkuranCheckboxBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UkuranItem) {
            binding.checkboxUkuran.text = item.ukuran
            binding.checkboxUkuran.isChecked = item.isChecked

            // Event saat checkbox diklik
            binding.checkboxUkuran.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
            }
        }
    }
    fun getSelectedUkuran(): List<String> {
        return ukuranList.filter { it.isChecked }.map { it.ukuran }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUkuranCheckboxBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ukuranList[position])
    }

    override fun getItemCount(): Int = ukuranList.size
}