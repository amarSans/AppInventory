package com.tugasmobile.inventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.DataBarangMasuk

class LaporanAdapter(private var dataBarangMasukList: List<DataBarangMasuk>) :
    RecyclerView.Adapter<LaporanAdapter.LaporanViewHolder>() {

    class LaporanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tanggalTextView:TextView=itemView.findViewById(R.id.item_data_tanggal)
        val namaProdukTextView: TextView = itemView.findViewById(R.id.namaProdukTextView)
        val kodeprodukTextView:TextView=itemView.findViewById(R.id.kodeProdukTextView)
        val stokTextView: TextView = itemView.findViewById(R.id.stokTextView)
        val hargaTextView: TextView = itemView.findViewById(R.id.hargaTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporanViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_data_laporan, parent, false)
        return LaporanViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LaporanViewHolder, position: Int) {
        val currentLaporan = dataBarangMasukList[position]
        holder.tanggalTextView.text=currentLaporan.waktu
        holder.kodeprodukTextView.text = currentLaporan.id
        holder.namaProdukTextView.text = currentLaporan.namaBarang
        holder.stokTextView.text = currentLaporan.stok.toString()
        holder.hargaTextView.text = "Rp. ${currentLaporan.harga}"
    }
    fun updateLaporanList(newDataBarangMasukList: List<DataBarangMasuk>) {
        this.dataBarangMasukList = newDataBarangMasukList
        notifyDataSetChanged()
    }
    override fun getItemCount() = dataBarangMasukList.size
}
