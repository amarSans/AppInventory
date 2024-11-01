package com.tugasmobile.inventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.Barang

class BarangAdapter(private var listBarang: List<Barang>, private val itemClickListener: (Barang) -> Unit):
    RecyclerView.Adapter<BarangAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view:View=LayoutInflater.from(parent.context).inflate(R.layout.item_barang,parent,false)
        return ListViewHolder(view)
    }


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val laporan=listBarang[position]
        holder.name.text= laporan.namaBarang
        holder.stok.text = laporan.stok.toString()
        holder.price.text = laporan.harga.toString()
        laporan.kodeBarang
        laporan.harga
        laporan.warna
        holder.itemId
        holder.itemView.setOnClickListener {
            itemClickListener(laporan)  // Panggil listener dengan data barang
        }

    }

    override fun getItemCount(): Int =listBarang.size
    class ListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name:TextView=itemView.findViewById(R.id.TVkodebarang)
        val stok:TextView=itemView.findViewById(R.id.TVstok)
        val price:TextView=itemView.findViewById(R.id.TVPrice)
    }
    fun updateLaporanList(newBarangList: List<Barang>) {
        this.listBarang = newBarangList
        notifyDataSetChanged()
    }

}