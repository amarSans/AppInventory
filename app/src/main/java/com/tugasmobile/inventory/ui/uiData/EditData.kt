package com.tugasmobile.inventory.ui.uiData

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterColorIn
import com.tugasmobile.inventory.databinding.ActivityEditDataBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.ui.simpleItem.HargaUtils
import java.io.File

class EditData : AppCompatActivity() {
    private lateinit var binding: ActivityEditDataBinding
    private lateinit var editViewModel: ViewModel
    private var barangId: String = ""
    private var stokBarang = 0
    private var selectedImageUri: Uri? = null
    private lateinit var photoUri: Uri
    private lateinit var colorAdapter: AdapterColorIn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editViewModel = ViewModelProvider(this).get(ViewModel::class.java)
        barangId = intent.getStringExtra("ID_BARANG") ?: ""
        if (barangId.isNotEmpty()) editViewModel.setCurrentBarang(barangId)
        val imgViewBack = binding.imgViewBack
        imgViewBack.setOnClickListener {
            finish()
        }
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.buttonAddStok.setOnClickListener { tambahStok() }
        binding.buttonRemoveStok.setOnClickListener { kurangiStok() }

        val colorNames = resources.getStringArray(R.array.daftar_nama_warna)
        val colorValues = resources.getStringArray(R.array.daftar_warna)
        colorAdapter = AdapterColorIn(this, colorNames, colorValues)
        setupSpinners()


        binding.buttonCamera.setOnClickListener { openCamera() }
        binding.buttonGallery.setOnClickListener { openGallery() }
        binding.buttonSave.setOnClickListener { saveChanges() }
        HargaUtils.setupHargaTextWatcher(binding.editTextHargaBarang)
    }
    private fun setupSpinners() {
        val warnaList = resources.getStringArray(R.array.daftar_nama_warna)

        // Inisialisasi adapter untuk Spinner warna
        val warnaAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, warnaList)
        warnaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWarnaEdit.adapter = warnaAdapter

        // Listener untuk icon close
        binding.iconCloseEdit.setOnClickListener {
            binding.edtUkuranEdit.setText("") // Reset Spinner ukuran
            binding.spinnerWarnaEdit.setSelection(0)  // Reset Spinner warna
        }

        // Listener untuk icon check
        binding.iconCheckEdit.setOnClickListener {
            val selectedUkuran = binding.edtUkuranEdit.text.toString().trim()
            val selectedWarna = binding.spinnerWarnaEdit.selectedItem as String

            // Gabungkan ukuran dan warna
            val newEntry = "$selectedUkuran $selectedWarna"

            // Ambil teks yang sudah ada di EditText
            val currentText = binding.editTextUkuranwarna.text.toString()

            // Tambahkan entri baru ke EditText (dipisahkan koma jika sudah ada data)
            val updatedText = if (currentText.isEmpty()) {
                newEntry
            } else {
                "$currentText, $newEntry"
            }

            // Set teks ke EditText
            binding.editTextUkuranwarna.setText(updatedText)

            // Reset Spinner
            binding.edtUkuranEdit.setText("")
            binding.spinnerWarnaEdit.setSelection(0)
        }
    }

    private fun setupObservers() {
        editViewModel.currentBarang.observe(this) { barang ->
            barang?.let {
                binding.editTextNamaBarang.setText(it.nama_barang)
                binding.editTextKodeBarang.setText(it.id_barang)
                selectedImageUri = if (it.gambar.isNullOrEmpty()) null else Uri.parse(it.gambar)
                binding.imageViewBarang.setImageURI(selectedImageUri)
            }?: run {
                // Handle kasus ketika barang bernilai null
                Toast.makeText(this, "Data barang tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
        editViewModel.currentStok.observe(this) { stok ->
            stok?.let {
                binding.editTextUkuranwarna.setText(it.ukuranwarna.toString())
                binding.editStokBarang.setText(it.stokBarang.toString())
                stokBarang = it.stokBarang
            }?: run {
                // Handle kasus ketika stok bernilai null
                Toast.makeText(this, "Data stok tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
        editViewModel.currentBarangIn.observe(this){barangIn->
            barangIn?.let{
                binding.editTextHargaBarang.setText(HargaUtils.formatHarga(it.Harga_Modal))
                binding.edtNamaToko.setText(it.Nama_Toko)
            } ?: run {
                // Handle kasus ketika barangIn bernilai null
                Toast.makeText(this, "Data barang masuk tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveChanges() {
        val selectedColors = colorAdapter.getSelectedColors().toSet()
        val hargaBarang = binding.editTextHargaBarang.text.toString().replace(".", "").toIntOrNull() ?: 0
        val updatedBarang = editViewModel.currentBarang.value?.copy(
            nama_barang = binding.editTextNamaBarang.text.toString(),
            id_barang = binding.editTextKodeBarang.text.toString(),
            gambar = selectedImageUri?.toString() ?: ""
        )?: run {
            Toast.makeText(this, "Data barang tidak valid", Toast.LENGTH_SHORT).show()
            return
        }
        val updatedStok = editViewModel.currentStok.value?.copy(
            stokBarang = binding.editStokBarang.text.toString().toIntOrNull() ?: 0,
            ukuranwarna = binding.editTextUkuranwarna.text.toString()

                .replace("[", "") // Hapus semua tanda "["
                .replace("]", "") // Hapus semua tanda "]"
                .trim() // Hapus spasi di awal dan akhir
                .split(",")
                .map { it.trim() }
        )?: run {
            Toast.makeText(this, "Data stok tidak valid", Toast.LENGTH_SHORT).show()
            return
        }
        val updatedBarangMasuk = editViewModel.currentBarangIn.value?.copy(
            Harga_Modal = hargaBarang,
            Nama_Toko = binding.edtNamaToko.text.toString()
        )?: run {
            Toast.makeText(this, "Data barang masuk tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        updatedBarang?.let {
            deleteImage(Uri.parse(it.gambar))
            editViewModel.updateWarna(it.id_barang, selectedColors.toList())
        }
        if (updatedBarang != null && updatedStok != null && updatedBarangMasuk!=null) {
            editViewModel.updateBarang(updatedBarang, updatedStok, updatedBarangMasuk)
        }
        editViewModel.loadBarang()
        finish()
    }


    private fun openCamera() {
        selectedImageUri?.let { deleteImage(it) }
        val photoFile = File(getAppSpecificAlbumStorageDir(), "IMG_${System.currentTimeMillis()}.jpg").apply {
            if (!parentFile.exists()) parentFile.mkdirs()
            photoUri = FileProvider.getUriForFile(
                this@EditData,
                "$packageName.fileprovider",
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
            Toast.makeText(this, "Gambar berhasil disimpan.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Pengambilan gambar dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        selectedImageUri?.let { deleteImage(it) }
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data?.data
            binding.imageViewBarang.setImageURI(selectedImageUri)
        }
    }

    private fun deleteImage(imageUri: Uri) {
        val fileToDelete = File(imageUri.path ?: return)
        if (fileToDelete.exists() && fileToDelete.delete()) {
            Log.d("EditActivity", "Gambar dihapus: ${fileToDelete.absolutePath}")
        }
    }

    private fun getAppSpecificAlbumStorageDir(): File {
        return File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "InventoryApp").apply {
            if (!exists()) mkdirs()
        }
    }

    private fun tambahStok() {
        stokBarang++
        binding.editStokBarang.setText(stokBarang.toString())
    }

    private fun kurangiStok() {
        if (stokBarang > 0) stokBarang--
        binding.editStokBarang.setText(stokBarang.toString())
    }
}