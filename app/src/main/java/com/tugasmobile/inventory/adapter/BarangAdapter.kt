package com.tugasmobile.inventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.DataBarangMasuk
import com.tugasmobile.inventory.ui.simpleItem.HargaUtils

class BarangAdapter(private var listDataBarangMasuk: List<DataBarangMasuk>, private val itemClickListener: (DataBarangMasuk) -> Unit):
    RecyclerView.Adapter<BarangAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view:View=LayoutInflater.from(parent.context).inflate(R.layout.item_barang,parent,false)
        return ListViewHolder(view)
    }
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val laporan=listDataBarangMasuk[position]
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

    }

    override fun getItemCount(): Int =listDataBarangMasuk.size
    class ListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name:TextView=itemView.findViewById(R.id.TVkodebarang)
        val stok:TextView=itemView.findViewById(R.id.TVstok)
        val price:TextView=itemView.findViewById(R.id.TVPrice)
        val gambar:ImageView=itemView.findViewById(R.id.item_image)
    }
    fun updateLaporanList(newDataBarangMasukList: List<DataBarangMasuk>) {
        this.listDataBarangMasuk = newDataBarangMasukList
        notifyDataSetChanged()
    }

}