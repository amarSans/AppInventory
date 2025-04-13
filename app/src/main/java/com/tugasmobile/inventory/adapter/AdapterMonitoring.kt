package com.tugasmobile.inventory.ui.main.monitoring

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.BarangMonitor
import com.tugasmobile.inventory.ui.editdata.DetailBarang
import com.tugasmobile.inventory.utils.AnimationHelper
import com.tugasmobile.inventory.utils.DateUtils
import com.tugasmobile.inventory.utils.PerformClickUtils

class AdapterMonitoring(
    private val context: Context,
    private var data:List<BarangMonitor>
) : RecyclerView.Adapter<AdapterMonitoring.MonitoringViewHolder>() {
    fun submitList(list: List<BarangMonitor>) {
        data = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonitoringViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_barang_monitor, parent, false)
        return MonitoringViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonitoringViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            PerformClickUtils.preventMultipleClick {
                val intent = Intent(context, DetailBarang::class.java).apply {
                    putExtra("ID_BARANG", item.kodeBarang)
                }
                context.startActivity(intent)
            }
        }

        AnimationHelper.animateRecyclerItem(holder.itemView, position)
    }

    override fun getItemCount(): Int = data.size

    class MonitoringViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMerek: TextView = itemView.findViewById(R.id.tvMerekBarang)
        private val tvStok: TextView = itemView.findViewById(R.id.tvStok)
        private val tvLastUpdate: TextView = itemView.findViewById(R.id.tvLastUpdate)

        fun bind(item: BarangMonitor) {
            tvMerek.text = item.merekBarang
            tvStok.text = "Stok: ${item.stok}"

            val days = item.lastUpdate?.let { DateUtils.getDaysSince(it) }
            val lastUpdateText = if (days != null && days >= 0) {
                "Terakhir diperbarui $days hari yang lalu"
            } else {
                "Tanggal tidak valid"
            }
            tvLastUpdate.text = lastUpdateText
        }
    }}
