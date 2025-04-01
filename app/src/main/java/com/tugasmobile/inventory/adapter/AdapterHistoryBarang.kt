package com.tugasmobile.inventory.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.History

class AdapterHistoryBarang (private var historyList: List<History>) :
    RecyclerView.Adapter<AdapterHistoryBarang.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvHistoryText: TextView = itemView.findViewById(R.id.tvHistoryText)
        val tvHistoryDate: TextView = itemView.findViewById(R.id.tvHistoryDate)
        val chatBubble: LinearLayout = itemView.findViewById(R.id.chatBubble)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_barang, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]

        val message = when (item.jenisData) {
            "barangmasuk" -> "Barang ${item.kodeBarang} baru dimasukkan dengan stok awal ${item.stok} pcs."
            "stokmasuk" -> "Stok ${item.kodeBarang} bertambah sebanyak ${item.stok} pcs."
            "stokkeluar" -> "Stok ${item.kodeBarang} berkurang sebanyak ${item.stok} pcs."
            "barangdihapus" -> "Barang ${item.kodeBarang} telah dihapus dari database."
            else -> "Transaksi tidak diketahui."
        }
        holder.tvHistoryText.text = message
        holder.tvHistoryDate.text = item.waktu

        // Mengubah warna bubble berdasarkan jenis transaksi
        val context = holder.itemView.context
        when (item.jenisData) {
            "barangmasuk" -> holder.chatBubble.setBackgroundResource(R.drawable.bg_chat_bubble_blue)  // Biru
            "stokmasuk" -> holder.chatBubble.setBackgroundResource(R.drawable.bg_chat_bubble_green)  // Hijau
            "stokkeluar" -> holder.chatBubble.setBackgroundResource(R.drawable.bg_chat_bubble_orange)  // Oranye
            "barangdihapus" -> holder.chatBubble.setBackgroundResource(R.drawable.bg_chat_bubble_red)  // Merah
        }
    }


    override fun getItemCount(): Int = historyList.size
    fun setItems(newItems: List<History>) {
        historyList = newItems
        notifyDataSetChanged() // Memberitahu RecyclerView bahwa datanya berubah
    }
}