package com.tugasmobile.inventory.ui.editdata

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

class DetailBarang : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBarangBinding
    private lateinit var detailBarangViewModel:ViewModel
    private var barangId:Long =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        detailBarangViewModel=  ViewModelProvider(this).get(ViewModel::class.java)
        barangId = intent.getLongExtra("ID_BARANG", 0L)
        detailBarangViewModel.setCurrentBarang(barangId)

        detailBarangViewModel.currentBarang.observe(this) { barang ->
            barang?.let {
                binding.toolbar.title = it.namaProduk
            }
        }
        if (savedInstanceState == null) {
            val rincianFragment = RincianFragment().apply {
                arguments = Bundle().apply {
                    putLong("ID_BARANG", barangId)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, rincianFragment)  // Gunakan ID container dari layout
                .commit()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val editFragment = EditFragment().apply {
                    arguments = Bundle().apply { putLong("ID_BARANG", barangId) }
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, editFragment) // Pastikan `fragment_container` ada di layout activity
                    .addToBackStack(null)
                    .commit()
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