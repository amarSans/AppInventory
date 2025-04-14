package com.muammar.inventory.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muammar.inventory.R
import com.muammar.inventory.data.DataBarangAkses
import com.muammar.inventory.ui.editdata.DetailBarang
import com.muammar.inventory.utils.AnimationHelper
import com.muammar.inventory.utils.PerformClickUtils

class AdapterBarangMasuk(
    private val context: Context,
    private var listDataBarangAkses: List<DataBarangAkses>
) : RecyclerView.Adapter<AdapterBarangMasuk.ListViewHolder>() {
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
            .load(laporan.gambar)
            .into(holder.gambar)

        holder.itemView.setOnClickListener {
            PerformClickUtils.preventMultipleClick {
                val intent = Intent(context, DetailBarang::class.java).apply {
                    putExtra("ID_BARANG", laporan.id)
                }
                context.startActivity(intent)
            }

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