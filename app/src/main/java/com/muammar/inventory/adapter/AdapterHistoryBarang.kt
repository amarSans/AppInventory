package com.muammar.inventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muammar.inventory.R
import com.muammar.inventory.data.History
import com.muammar.inventory.utils.AnimationHelper

class AdapterHistoryBarang (private var historyList: List<History>) :
    RecyclerView.Adapter<AdapterHistoryBarang.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBubble: ImageView = itemView.findViewById(R.id.imageViewAktifitas)
        val tvJenisAktivitas: TextView = itemView.findViewById(R.id.tvJenisAktivitas)
        val tvDetailAktivitas: TextView = itemView.findViewById(R.id.tvDetailAktivitas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aktivitas_home, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]


        val jenisTransaksi = when (item.jenisData) {
            "barangmasuk" -> "Barang Masuk"
            "stokmasuk" -> "Stok Masuk"
            "stokkeluar" -> "Stok Keluar"
            "barangdihapus" -> "Barang Dihapus"
            else -> "Tidak Diketahui"
        }

        holder.tvJenisAktivitas.text = "$jenisTransaksi: ${item.kodeBarang}"
        holder.tvDetailAktivitas.text = "Jumlah: ${item.stok} - ${item.waktu}"


        val iconRes = when (item.jenisData) {
            "barangmasuk" -> R.drawable.bg_chat_bubble_blue
            "stokmasuk" -> R.drawable.bg_chat_bubble_green
            "stokkeluar" -> R.drawable.bg_chat_bubble_orange
            "barangdihapus" -> R.drawable.bg_chat_bubble_red
            else -> R.drawable.bacground_warna
        }

        holder.imgBubble.setImageResource(iconRes)
        AnimationHelper.animateRecyclerItem(holder.itemView,position)
    }

    override fun getItemCount(): Int = historyList.size

    fun setItems(newItems: List<History>) {
        historyList = newItems
        notifyDataSetChanged()
    }
}