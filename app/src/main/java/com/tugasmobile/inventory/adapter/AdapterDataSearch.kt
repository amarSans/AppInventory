package com.tugasmobile.inventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.SearchData

class AdapterDataSearch (
    private var barangList: List<SearchData>,
    private val onItemClick: (SearchData) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object{
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_DATA = 1
    }
    override fun getItemViewType(position: Int): Int {
        return if (barangList.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_DATA
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(barang: SearchData) {
            itemView.findViewById<TextView>(R.id.tvkodebarang).text = barang.id
            itemView.findViewById<TextView>(R.id.tvmerekbarang).text = barang.merekBarang
            itemView.findViewById<TextView>(R.id.tvkarakteristik).text = barang.karakteristik

            itemView.setOnClickListener {
                onItemClick(barang)
            }
        }
    }
    inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_EMPTY) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_empty_search, parent, false)
            EmptyViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_data_search, parent, false)
            ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder && barangList.isNotEmpty()) {
            val barang = barangList[position]
            holder.bind(barang)
        }
    }



    override fun getItemCount() : Int = if (barangList.isEmpty()) 1 else barangList.size

    fun updateList(newList: List<SearchData>) {
        barangList = newList
        notifyDataSetChanged()
    }
}
