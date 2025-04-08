package com.tugasmobile.inventory.ui.editdata

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.History
import com.tugasmobile.inventory.databinding.ActivityDetailBarangBinding
import com.tugasmobile.inventory.ui.InventoryViewModelFactory
import com.tugasmobile.inventory.ui.data.EditData
import com.tugasmobile.inventory.utils.DateUtils

class DetailBarang : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBarangBinding
    private val detailBarangViewModel:DetailViewModel  by viewModels {
        InventoryViewModelFactory.getInstance(this.application)
    }

    private var barangId:String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val typedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true)
        window.statusBarColor = typedValue.data


        barangId = intent.getStringExtra("ID_BARANG") ?: ""
        if (barangId.isNotEmpty()) detailBarangViewModel.setCurrentBarang(barangId)
        detailBarangViewModel.setCurrentBarang(barangId)


        if (savedInstanceState == null) {
            val rincianFragment = RincianFragment().apply {
                arguments = Bundle().apply {
                    putString("ID_BARANG", barangId)
                }

            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, rincianFragment)  // Gunakan ID container dari layout
                .commit()
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

// Menambahkan ikon back
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_new_24) // Gunakan icon yang diinginkan

// Aksi klik pada tombol back
        toolbar.setNavigationOnClickListener {
            onBackPressed() // Kembali ke activity sebelumnya
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val intent = Intent(this, EditData::class.java).apply {
                    putExtra("ID_BARANG", barangId)
                }
                startActivity(intent)
                true
            }
            R.id.action_delete -> {
                deleteBarang()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun deleteBarang() {
        val itemBarang = detailBarangViewModel.currentBarang.value
        val stok = detailBarangViewModel.currentStok.value

        if (itemBarang != null) {
            val history = History(
                id = 0,
                waktu = DateUtils.getCurrentDate(),
                kodeBarang = itemBarang.id_barang,  // Ambil kode barang sebelum dihapus
                stok = stok?.stokBarang.toString() ?: "0",  // Ambil stok sebelum dihapus
                jenisData = "barangdihapus"
            )

            // Simpan history sebelum menghapus barang
            detailBarangViewModel.insertHistory(history)
            itemBarang.gambar.let { gambarUriString ->
                val gambarUri = Uri.parse(gambarUriString)
                val isDeleted = deleteImageFromStorage(gambarUri)
                if (isDeleted) {
                    Log.d("DeleteBarang", "Gambar berhasil dihapus.")
                } else {
                    Log.w("DeleteBarang", "Gagal menghapus gambar.")
                }
            }
            // Hapus barang dari database
            detailBarangViewModel.deleteBarang(barangId)

            // Tutup activity setelah menghapus
            finish()
        }


    }
    fun deleteImageFromStorage(imageUri: Uri): Boolean {
        return try {
            val rowsDeleted = this.contentResolver.delete(imageUri, null, null)
            rowsDeleted > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}