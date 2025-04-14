package com.muammar.inventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muammar.inventory.R
import com.muammar.inventory.data.DataBarangHampirHabisHome

class AdapterBarangHampirHabisHome(
    private var listBarang: List<DataBarangHampirHabisHome>
) : RecyclerView.Adapter<AdapterBarangHampirHabisHome.BarangViewHolder>() {

    inner class BarangViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaBarang: TextView = itemView.findViewById(R.id.tvNamaBarang)
        val tvSisaStok: TextView = itemView.findViewById(R.id.tvSisaStok)
        val imageBarang: ImageView = itemView.findViewById(R.id.imageBarang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarangViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_barang_habis_home, parent, false)
        return BarangViewHolder(view)
    }

    override fun onBindViewHolder(holder: BarangViewHolder, position: Int) {
        val item = listBarang[position]
        holder.tvNamaBarang.text = item.namaBarang
        holder.tvSisaStok.text = "Sisa: ${item.stok}"
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.baseline_image_24)
            .into(holder.imageBarang)
    }
    fun updateData(newList: List<DataBarangHampirHabisHome>) {
        listBarang = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = listBarang.size
}
