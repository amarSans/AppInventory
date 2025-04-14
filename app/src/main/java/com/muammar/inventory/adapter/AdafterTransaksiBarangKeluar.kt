package com.muammar.inventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muammar.inventory.R
import com.muammar.inventory.data.DaftarBarangKeluar


class AdafterTransaksiBarangKeluar(private val data: List<DaftarBarangKeluar>) : RecyclerView.Adapter<AdafterTransaksiBarangKeluar.ViewHolder>() {
    var onTotalHargaUpdated: ((Int) -> Unit)? = null
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val kodeBarang: TextView = view.findViewById(R.id.kode_berang_out)
        val stok: TextView = view.findViewById(R.id.stok_out)
        val ukuran: TextView = view.findViewById(R.id.TV_Ukuran)
        val hargaJual: TextView = view.findViewById(R.id.harga_jual_out)
        val hargaTotal: TextView = view.findViewById(R.id.harga_total_out)
    }
    fun getTotalHarga(): Int {
        return data.sumOf { it.stok * it.hargaJual }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_data_out, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.kodeBarang.text = item.kodeBarang
        holder.stok.text = item.stok.toString()
        holder.ukuran.text = item.ukuranWarna.joinToString(", ")
        holder.hargaJual.text = item.hargaJual.toString()
        val stok= holder.stok.text.toString().toInt()
        val hargajual= holder.hargaJual.text.toString().toInt()
        holder.hargaTotal.text = (stok*hargajual).toString()
        onTotalHargaUpdated?.invoke(getTotalHarga())
    }

    override fun getItemCount(): Int = data.size
}