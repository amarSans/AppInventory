package com.tugasmobile.inventory.ui.uiData

import android.app.Activity
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.MainActivity
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterColorIn
import com.tugasmobile.inventory.data.Barang
import com.tugasmobile.inventory.databinding.ActivityAddDataBinding
import com.tugasmobile.inventory.ui.ViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class addData : AppCompatActivity() {
    private val viewModel: ViewModel by viewModels()
    private lateinit var binding: ActivityAddDataBinding
    private var stokBarang = 0
    private lateinit var recyclerView: RecyclerView
    private var selectedColor: String? = null
    private var selectedSizesList: String = ""
    private var selectedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imgViewBack=binding.imgViewBack
        imgViewBack.setOnClickListener {
            // Logika kembali ke halaman sebelumnya atau menutup halaman saat ini
            finish() // Menutup Activity saat ini dan kembali ke halaman sebelumnya
        }
        stokBarang = binding.editStokBarang.text.toString().toIntOrNull() ?: 0
        binding.editStokBarang.setText(stokBarang.toString())
        binding.buttonAddStok.setOnClickListener { tambahStok() }
        binding.buttonRemoveStok.setOnClickListener { kurangiStok() }

        binding.buttonSave.setOnClickListener {
            saveData()
        }
        val colorNames = resources.getStringArray(R.array.daftar_nama_warna)
        val colorValues = resources.getStringArray(R.array.daftar_warna)

        // Inisialisasi RecyclerView dan Adapter
        recyclerView = findViewById(R.id.recyclerViewColors)
        val colorAdapter = AdapterColorIn(this, colorNames, colorValues)

        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = colorAdapter


        val currentDate = getCurrentDate()
        binding.editTextDate.setText(currentDate)


        binding.edtUkuran.setOnClickListener {
            val bottonUkuranSheet = BottonUkuranSheet()
            bottonUkuranSheet.listener = object : BottonUkuranSheet.SizeSelectionListener {
                override fun onSizeSelected(selectedSizes: List<String>) {
                    selectedSizesList = selectedSizes.joinToString(", ")
                    binding.edtUkuran.text=selectedSizesList
                }
            }
            bottonUkuranSheet.show(supportFragmentManager, BottonUkuranSheet.TAG)
        }
        binding.buttonGallery.setOnClickListener {
            openGallery()
        }



    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data?.data
            binding.imageViewBarang.setImageURI(selectedImageUri)
        }
    }

    private fun saveData() {
        val namaProduk = binding.editTextNamaBarang.text.toString()
        val kodeProduk = binding.editTextKodeBarang.text.toString()
        val hargaProduk = binding.editTextHargaBarang.text.toString().toInt()
        val selectedColors =
            (recyclerView.adapter as AdapterColorIn).getSelectedColors()
        val selectedCategory = binding.SpinnerKategori.selectedItem.toString()
        val barang = Barang(
            id = 0,
            namaBarang = namaProduk,
            kodeBarang = kodeProduk,
            harga = hargaProduk,
            stok = stokBarang,
            warna = selectedColors,
            waktu = getCurrentDate(),
            kategori = selectedCategory,
            ukuran = selectedSizesList,
            gambar = selectedImageUri?.toString() ?: ""
        )


        viewModel.insertLaporan(barang)
        Toast.makeText(this, "data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        val intent = Intent(
            this, MainActivity::class.java
        )
        startActivity(intent)
        finish()
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }


    private fun tambahStok() {
        stokBarang += 1
        binding.editStokBarang.setText(stokBarang.toString())
    }

    // Fungsi untuk mengurangi stok (tidak kurang dari 0)
    private fun kurangiStok() {
        if (stokBarang > 0) {
            stokBarang -= 1
        }
        binding.editStokBarang.setText(stokBarang.toString())
    }
}