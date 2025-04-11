package com.tugasmobile.inventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.DataBarangAkses
import com.tugasmobile.inventory.utils.AnimationHelper
import com.tugasmobile.inventory.utils.HargaUtils

class AdapterDaftarBarang(private var listDataBarangAkses: List<DataBarangAkses>, private val itemClickListener: (DataBarangAkses) -> Unit):
    RecyclerView.Adapter<AdapterDaftarBarang.ListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view:View=LayoutInflater.from(parent.context).inflate(R.layout.item_barang_horizontal,parent,false)
        return ListViewHolder(view)
    }
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val laporan=listDataBarangAkses[position]
        holder.name.text= laporan.namaBarang
        holder.stok.text = laporan.stok.toString()
        val formattedHarga = HargaUtils.formatHarga(laporan.harga)
        holder.price.text = "Rp. ${formattedHarga}"
        Glide.with(holder.itemView.context)
            .load(laporan.gambar) // Gantilah dengan path gambar dari database
            .into(holder.gambar)

        holder.itemView.setOnClickListener {
            itemClickListener(laporan)  // Panggil listener dengan data barang
        }
        AnimationHelper.animateRecyclerItem(holder.itemView,position)

    }

    override fun getItemCount(): Int =listDataBarangAkses.size
    class ListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name:TextView=itemView.findViewById(R.id.TVkodebarang)
        val stok:TextView=itemView.findViewById(R.id.TVstok)
        val price:TextView=itemView.findViewById(R.id.TVPrice)
        val gambar:ImageView=itemView.findViewById(R.id.item_image)
    }
    fun updateLaporanList(newDataBarangAksesList: List<DataBarangAkses>) {
        this.listDataBarangAkses = newDataBarangAksesList
        notifyDataSetChanged()
    }
    fun sortByStok(ascending: Boolean) {
        listDataBarangAkses = if (ascending) {
            listDataBarangAkses.sortedBy { it.stok }.toMutableList()
        } else {
            listDataBarangAkses.sortedByDescending { it.stok }.toMutableList()
        }
        notifyDataSetChanged()
    }

    // Fungsi sorting berdasarkan harga
    fun sortByHarga(ascending: Boolean) {
        listDataBarangAkses = if (ascending) {
            listDataBarangAkses.sortedBy { it.harga }.toMutableList()
        } else {
            listDataBarangAkses.sortedByDescending { it.harga }.toMutableList()
        }
        notifyDataSetChanged()
    }

    // Fungsi sorting berdasarkan nama barang
    fun sortByNama(ascending: Boolean) {
        listDataBarangAkses = if (ascending) {
            listDataBarangAkses.sortedBy { it.namaBarang }.toMutableList()
        } else {
            listDataBarangAkses.sortedByDescending { it.namaBarang }.toMutableList()
        }
        notifyDataSetChanged()
    }




}