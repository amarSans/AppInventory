package com.tugasmobile.inventory.ui.uiData

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterColorIn
import com.tugasmobile.inventory.databinding.ActivityEditDataBinding
import com.tugasmobile.inventory.ui.ViewModel
import java.io.File

class EditData : AppCompatActivity() {
    private lateinit var binding: ActivityEditDataBinding
    private lateinit var editViewModel: ViewModel
    private var barangId: Long = 0
    private var stokBarang = 0
    private var selectedSizesList: List<String> = emptyList()
    private var selectedImageUri: Uri? = null
    private lateinit var photoUri: Uri
    private lateinit var colorAdapter: AdapterColorIn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editViewModel = ViewModelProvider(this).get(ViewModel::class.java)
        barangId = intent.getLongExtra("ID_BARANG", 0)
        if (barangId != 0L) editViewModel.setCurrentBarang(barangId)
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

        binding.recyclerViewColorsEdit.apply {
            layoutManager = LinearLayoutManager(this@EditData, LinearLayoutManager.HORIZONTAL, false)
            adapter = colorAdapter
        }

        binding.edtUkuran.setOnClickListener {
            val bottonUkuranSheet = BottonUkuranSheet.newInstance(selectedSizesList)
            bottonUkuranSheet.listener = object : BottonUkuranSheet.SizeSelectionListener {
                override fun onSizeSelected(selectedSizes: List<String>) {
                    selectedSizesList = selectedSizes
                    binding.edtUkuran.text = selectedSizes.joinToString(", ")
                }
            }
            bottonUkuranSheet.show(supportFragmentManager, BottonUkuranSheet.TAG)
        }

        binding.buttonCamera.setOnClickListener { openCamera() }
        binding.buttonGallery.setOnClickListener { openGallery() }
        binding.buttonSave.setOnClickListener { saveChanges() }
    }

    private fun setupObservers() {
        editViewModel.currentBarang.observe(this) { barang ->
            barang?.let {
                binding.editTextNamaBarang.setText(it.nama_barang)
                binding.editTextKodeBarang.setText(it.kode_barang)
                selectedImageUri = Uri.parse(it.gambar)
                binding.imageViewBarang.setImageURI(selectedImageUri)
            }
        }
        editViewModel.currentStok.observe(this) { stok ->
            stok?.let {
                binding.edtUkuran.text = it.ukuran
                selectedSizesList = it.ukuran.split(",").map { size -> size.trim() }
                colorAdapter.setSelectedColors(it.warna)
                binding.editStokBarang.setText(it.stokBarang.toString())
                stokBarang = it.stokBarang
            }
        }
        editViewModel.currentBarangIn.observe(this){barangIn->
            barangIn?.let{
                binding.editTextHargaBarang.setText(it.Harga_Modal.toString())
                binding.edtNamaToko.setText(it.Nama_Toko)
            }
        }
    }

    private fun saveChanges() {
        val selectedColors = colorAdapter.getSelectedColors().toSet()
        val updatedBarang = editViewModel.currentBarang.value?.copy(
            nama_barang = binding.editTextNamaBarang.text.toString(),
            kode_barang = binding.editTextKodeBarang.text.toString(),
            gambar = selectedImageUri?.toString() ?: ""
        )
        val updatedStok = editViewModel.currentStok.value?.copy(
            stokBarang = binding.editStokBarang.text.toString().toIntOrNull() ?: 0,
            ukuran = binding.edtUkuran.text.toString(),
            warna = selectedColors.toList()
        )
        val updatedBarangMasuk = editViewModel.currentBarangIn.value?.copy(
            Harga_Modal = binding.editTextHargaBarang.text.toString().toInt(),
            Nama_Toko = binding.edtNamaToko.text.toString()
        )

        updatedBarang?.let {
            deleteImage(Uri.parse(it.gambar))
            editViewModel.updateWarna(it.id_barang, selectedColors.toList())
        }
        if (updatedBarang != null && updatedStok != null && updatedBarangMasuk!=null) {
            editViewModel.updateBarang(updatedBarang, updatedStok, updatedBarangMasuk)
        }
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