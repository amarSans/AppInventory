package com.tugasmobile.inventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.DataBarangAkses
import com.tugasmobile.inventory.utils.AnimationHelper
import com.tugasmobile.inventory.utils.HargaUtils

class AdapterBarangMasuk(private var listDataBarangAkses: List<DataBarangAkses>, private val itemClickListener: (DataBarangAkses) -> Unit):
    RecyclerView.Adapter<AdapterBarangMasuk.ListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_barang_masuk,parent,false)
        return ListViewHolder(view)
    }
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val laporan=listDataBarangAkses[position]
        holder.name.text= laporan.namaBarang
        holder.kode.text = laporan.id
        holder.time.text = laporan.waktu
        Glide.with(holder.itemView.context)
            .load(laporan.gambar) // Gantilah dengan path gambar dari database
            .into(holder.gambar)

        holder.itemView.setOnClickListener {
            itemClickListener(laporan)  // Panggil listener dengan data barang
        }
        AnimationHelper.animateRecyclerItem(holder.itemView,position)

    }

    override fun getItemCount(): Int =listDataBarangAkses.size
    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView =itemView.findViewById(R.id.item_name)
        val kode: TextView =itemView.findViewById(R.id.item_code)
        val time: TextView =itemView.findViewById(R.id.item_time)
        val gambar: ImageView =itemView.findViewById(R.id.item_image)
    }
    fun updateLaporanList(newDataBarangAksesList: List<DataBarangAkses>) {
        this.listDataBarangAkses = newDataBarangAksesList
        notifyDataSetChanged()
    }

}