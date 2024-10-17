package com.tugasmobile.inventory.ui.editdata

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.databinding.ActivityDetailBarangBinding
import com.tugasmobile.inventory.ui.ViewModel
import java.lang.reflect.Array.getDouble
import java.lang.reflect.Array.getInt

class DetailBarang : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBarangBinding
    private lateinit var detailBarangViewModel:ViewModel
    private var barangId:Long =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_barang)
        binding = ActivityDetailBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        detailBarangViewModel=  ViewModelProvider(this).get(ViewModel::class.java)
        val namaBarang = intent.getStringExtra("NAMA_BARANG") ?: "barang tidak ada"
        val stokBarang = intent.getIntExtra("STOK_BARANG", 0)
        val hargaBarang = intent.getDoubleExtra("HARGA_BARANG", 0.0)
        barangId=intent.getLongExtra("ID_BARANG",0L)
        // Tampilkan data di UI
        binding.NamaBarang.text = namaBarang
        binding.StokBarang.text = stokBarang.toString()
        binding.HargaBarang.text = hargaBarang.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                // Aksi untuk Edit
                // Tambahkan logika edit di sini
                true
            }
            R.id.action_delete -> {
                deleteBarang()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun deleteBarang(){
        detailBarangViewModel.deleteLaporan(barangId)
        finish()
    }

}