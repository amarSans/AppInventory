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
import com.muammar.inventory.utils.HargaUtils
import com.muammar.inventory.utils.PerformClickUtils

class AdapterDaftarBarang(
    private val context: Context,
    private var listDataBarangAkses: List<DataBarangAkses>
) : RecyclerView.Adapter<AdapterDaftarBarang.ListViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view:View=LayoutInflater.from(parent.context).inflate(R.layout.item_barang_horizontal,parent,false)
        return ListViewHolder(view)
    }
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val itemdatabarang=listDataBarangAkses[position]
        holder.name.text= itemdatabarang.namaBarang
        holder.stok.text = itemdatabarang.stok.toString()
        val formattedHarga = HargaUtils.formatHarga(itemdatabarang.harga)
        holder.price.text = "Rp. ${formattedHarga}"
        Glide.with(holder.itemView.context)
            .load(itemdatabarang.gambar)
            .into(holder.gambar)

        holder.itemView.setOnClickListener {
            PerformClickUtils.preventMultipleClick {
                val intent = Intent(context, DetailBarang::class.java).apply {
                    putExtra("ID_BARANG", itemdatabarang.id)
                }
                context.startActivity(intent)
            }

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


    fun sortByHarga(ascending: Boolean) {
        listDataBarangAkses = if (ascending) {
            listDataBarangAkses.sortedBy { it.harga }.toMutableList()
        } else {
            listDataBarangAkses.sortedByDescending { it.harga }.toMutableList()
        }
        notifyDataSetChanged()
    }


    fun sortByNama(ascending: Boolean) {
        listDataBarangAkses = if (ascending) {
            listDataBarangAkses.sortedBy { it.namaBarang }.toMutableList()
        } else {
            listDataBarangAkses.sortedByDescending { it.namaBarang }.toMutableList()
        }
        notifyDataSetChanged()
    }




}