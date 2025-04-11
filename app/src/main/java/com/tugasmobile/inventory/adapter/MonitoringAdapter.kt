package com.tugasmobile.inventory.ui.main.monitoring

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.data.BarangMonitor
import com.tugasmobile.inventory.databinding.ItemBarangMonitorBinding
import com.tugasmobile.inventory.utils.DateUtils

class MonitoringAdapter(
    private val onClick: (BarangMonitor) -> Unit
) : RecyclerView.Adapter<MonitoringAdapter.MonitoringViewHolder>() {

    private var data: List<BarangMonitor> = emptyList()

    fun submitList(list: List<BarangMonitor>) {
        data = list
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitoringViewHolder {
        val binding = ItemBarangMonitorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MonitoringViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonitoringViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    inner class MonitoringViewHolder(private val binding: ItemBarangMonitorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BarangMonitor) {

            binding.tvMerekBarang.text = item.merekBarang
            binding.tvStok.text = "Stok: ${item.stok}"
            val days = item.lastUpdate?.let { DateUtils.getDaysSince(it) }
            val lastUpdateText = if (days!! >= 0) {
                "Terakhir diperbarui $days hari yang lalu"
            } else {
                "Tanggal tidak valid"
            }
            binding.tvLastUpdate.text = lastUpdateText

            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }
}
