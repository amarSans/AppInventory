package com.tugasmobile.inventory.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.History

class AdapterHistoryBarang (private var historyDataBarang: List<History>):
    RecyclerView.Adapter<AdapterHistoryBarang.ListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_history_barang,parent,false)
        Log.d("AdapterHistoryBarang", "ViewHolder dibuat")
        return ListViewHolder(view)
    }
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val history=historyDataBarang[position]
        Log.d("AdapterHistoryBarang", "Mengikat data ke ViewHolder: $history")
        holder.apply {
            time.text = history.waktu
            kode.text = history.kodeBarang
            stok.text = history.stok
            ukuranWarna.text = history.ukuranWarna
            harga.text = "Rp. ${history.harga}"
        }

        // Menentukan warna berdasarkan jenisData
        val textColor = if (history.jenisData) Color.GREEN else Color.RED
        arrayOf(holder.time, holder.kode, holder.stok, holder.ukuranWarna, holder.harga).forEach {
            it.setTextColor(textColor)
        }

    }

    override fun getItemCount(): Int =historyDataBarang.size
    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val time: TextView =itemView.findViewById(R.id.item_time_history)
        val kode: TextView =itemView.findViewById(R.id.item_kode_history)
        val stok: TextView =itemView.findViewById(R.id.item_stok_history)
        val ukuranWarna: TextView =itemView.findViewById(R.id.item_ukuran_warna_history)
        val harga: TextView =itemView.findViewById(R.id.item_harga_history)
    }

    fun setItems(newItems: List<History>) {
        Log.d("AdapterHistoryBarang", "Data di-update: ${newItems.size} item")
        historyDataBarang = newItems
        notifyDataSetChanged()
    }

}