package com.tugasmobile.inventory.ui.uiData

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.MainActivity
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterColorIn
import com.tugasmobile.inventory.data.ItemBarang
import com.tugasmobile.inventory.data.BarangIn
import com.tugasmobile.inventory.data.Stok
import com.tugasmobile.inventory.databinding.ActivityAddDataBinding
import com.tugasmobile.inventory.ui.Barang.BarangMasuk
import com.tugasmobile.inventory.ui.ViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class addData : AppCompatActivity() {
    private val viewModel: ViewModel by viewModels()
    private lateinit var binding: ActivityAddDataBinding
    private var stokBarang = 0
    private lateinit var recyclerView: RecyclerView

    private var selectedSizesList: String = ""
    private var selectedImageUri: Uri? = null
    private lateinit var photoUri: Uri

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1001 // Definisikan REQUEST_CODE_PERMISSIONS di sini
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imgViewBack = binding.imgViewBack
        imgViewBack.setOnClickListener {
            finish()
        }
        // Tambahkan periksa izin
        checkPermissions()
        // Inisialisasi komponen UI dan set onClickListener
        setupUI()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun openCamera() {
        val photoFile = File(getAppSpecificAlbumStorageDir(), "IMG_${System.currentTimeMillis()}.jpg").apply {
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
            photoUri = FileProvider.getUriForFile(
                this@addData,
                "${packageName}.fileprovider",
                this
            )
        }

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }

        cameraLauncher.launch(cameraIntent)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = photoUri
            binding.imageViewBarang.setImageURI(selectedImageUri)

            Toast.makeText(this, "Gambar berhasil disimpan di folder aplikasi.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Pengambilan gambar dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAppSpecificAlbumStorageDir(): File {
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "InventoryApp") // Membuat folder khusus di direktori aplikasi
        if (!file.exists()) {
            file.mkdirs() // Buat folder jika belum ada
        }
        return file
    }

    private fun saveData() {
        val namaProduk = binding.editTextNamaBarang.text.toString()
        val kodeProduk = binding.editTextKodeBarang.text.toString()
        val hargaProduk = binding.editTextHargaBarang.text.toString().toInt()
        val selectedColors = (recyclerView.adapter as AdapterColorIn).getSelectedColors()
        val namaToko = binding.edtNamaToko.text.toString()
        val itemBarang=ItemBarang(
            id_barang = 0,
            nama_barang = namaProduk,
            kode_barang = kodeProduk,
            gambar = selectedImageUri.toString()
        )
        val stok= Stok(
            idStok = 0,
            id_barang = 0,
            stokBarang = stokBarang,
            warna = selectedColors,
            ukuran = selectedSizesList,
        )
        val barangIn = BarangIn(
            IdBrgMasuk = 0,
            id_barang = 0,
            Tgl_Masuk = getCurrentDate(),
            Harga_Modal = hargaProduk,
            Nama_Toko =namaToko
        )
        viewModel.insertInputBarang(itemBarang,stok,barangIn)/*
        Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()*/
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setupUI() {
        // Inisialisasi UI dan tombol untuk stok dan warna
        // Serta set onClickListener pada tombol kamera dan galeri
        binding.buttonAddStok.setOnClickListener { tambahStok() }
        binding.buttonRemoveStok.setOnClickListener { kurangiStok() }
        binding.buttonSave.setOnClickListener { saveData() }
        binding.buttonCamera.setOnClickListener { openCamera() }
        binding.buttonGallery.setOnClickListener { openGallery() }
        binding.editTextDate.setText(getCurrentDate())
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


        // Inisialisasi lainnya
        recyclerView = findViewById(R.id.recyclerViewColors) // Pastikan RecyclerView sudah di-inisialisasi
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = AdapterColorIn(this, resources.getStringArray(R.array.daftar_nama_warna), resources.getStringArray(R.array.daftar_warna))
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

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun tambahStok() {
        stokBarang += 1
        binding.editStokBarang.setText(stokBarang.toString())
    }

    private fun kurangiStok() {
        if (stokBarang > 0) {
            stokBarang -= 1
        }
        binding.editStokBarang.setText(stokBarang.toString())
    }
}
