package com.tugasmobile.inventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.History

class AdapterHistoryBarang (private var historyList: List<History>) :
    RecyclerView.Adapter<AdapterHistoryBarang.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBubble: ImageView = itemView.findViewById(R.id.imageViewAktifitas) // ganti dengan id dari ImageView di item_aktivitas.xml
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

        // Set judul
        val jenisTransaksi = when (item.jenisData) {
            "barangmasuk" -> "Barang Masuk"
            "stokmasuk" -> "Stok Masuk"
            "stokkeluar" -> "Stok Keluar"
            "barangdihapus" -> "Barang Dihapus"
            else -> "Tidak Diketahui"
        }

        holder.tvJenisAktivitas.text = "$jenisTransaksi: ${item.kodeBarang}"
        holder.tvDetailAktivitas.text = "Jumlah: ${item.stok} - ${item.waktu}"

        // Ganti gambar bubble sesuai jenis
        val iconRes = when (item.jenisData) {
            "barangmasuk" -> R.drawable.bg_chat_bubble_blue
            "stokmasuk" -> R.drawable.bg_chat_bubble_green
            "stokkeluar" -> R.drawable.bg_chat_bubble_orange
            "barangdihapus" -> R.drawable.bg_chat_bubble_red
            else -> R.drawable.bacground_warna // default
        }

        holder.imgBubble.setImageResource(iconRes)
    }

    override fun getItemCount(): Int = historyList.size

    fun setItems(newItems: List<History>) {
        historyList = newItems
        notifyDataSetChanged()
    }
}