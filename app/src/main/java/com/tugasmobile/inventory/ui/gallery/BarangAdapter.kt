package com.tugasmobile.inventory.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.Laporan

class BarangAdapter(private val listBarang:ArrayList<Laporan>):RecyclerView.Adapter<BarangAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view:View=LayoutInflater.from(parent.context).inflate(R.layout.item_barang,parent,false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: BarangAdapter.ListViewHolder, position: Int) {
        val laporan=listBarang[position]
        holder.name.text=laporan.namaProduk
        holder.stok.text = laporan.stok.toString()
        holder.price.text = laporan.harga.toString()
    }

    override fun getItemCount(): Int =listBarang.size
    class ListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val name:TextView=itemView.findViewById(R.id.TVkodebarang)
        val stok:TextView=itemView.findViewById(R.id.TVstok)
        val price:TextView=itemView.findViewById(R.id.TVPrice)
    }
}