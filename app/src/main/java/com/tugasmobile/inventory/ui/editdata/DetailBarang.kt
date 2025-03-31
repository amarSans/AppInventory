package com.tugasmobile.inventory.ui.editdata

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.databinding.ActivityDetailBarangBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.ui.data.EditData

class DetailBarang : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBarangBinding
    private lateinit var detailBarangViewModel:ViewModel
    private var barangId:String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        detailBarangViewModel=  ViewModelProvider(this).get(ViewModel::class.java)
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
    private fun deleteBarang(){
        detailBarangViewModel.deleteBarang(barangId)
        finish()
    }

}