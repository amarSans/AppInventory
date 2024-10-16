package com.tugasmobile.inventory.ui.editdata

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.databinding.ActivityDetailBarangBinding
import java.lang.reflect.Array.getDouble
import java.lang.reflect.Array.getInt

class DetailBarang : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBarangBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) 
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_barang)
        binding = ActivityDetailBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val namaBarang = intent.getStringExtra("NAMA_BARANG") ?: "barang tidak ada"
        val stokBarang = intent.getIntExtra("STOK_BARANG", 0)
        val hargaBarang = intent.getDoubleExtra("HARGA_BARANG", 0.0)

        // Tampilkan data di UI
        binding.NamaBarang.text = namaBarang
        binding.StokBarang.text = stokBarang.toString()
        binding.HargaBarang.text = hargaBarang.toString()}
}