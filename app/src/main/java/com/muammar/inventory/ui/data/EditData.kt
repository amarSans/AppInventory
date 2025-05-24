package com.muammar.inventory.ui.data

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.muammar.inventory.R
import com.muammar.inventory.adapter.AdapterColorIn
import com.muammar.inventory.databinding.ActivityEditDataBinding
import com.muammar.inventory.ui.InventoryViewModelFactory
import com.muammar.inventory.ui.camera.CameraActivity
import com.muammar.inventory.ui.simpleItem.KarakteristikBottomSheetFragment
import com.muammar.inventory.utils.AnimationHelper
import com.muammar.inventory.utils.DateUtils
import com.muammar.inventory.utils.FormatAngkaUtils
import com.muammar.inventory.utils.HargaUtils
import com.muammar.inventory.utils.PerformClickUtils
import com.muammar.inventory.utils.reduceFileImage
import com.muammar.inventory.utils.uriToFile

class EditData : AppCompatActivity() {
    private lateinit var binding: ActivityEditDataBinding
    private val editViewModel: DataViewModel by viewModels {
        InventoryViewModelFactory.getInstance(this.application)
    }
    private var barangId: String = ""
    private var stokBarang = 0
    private var selectedImageUri: Uri? = null
    private var previousImageUri: Uri? = null
    private var selectedItems = mutableSetOf<String>()

    private lateinit var colorAdapter: AdapterColorIn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDataBinding.inflate(layoutInflater)
        /*setContentView(binding.root)*/
        setContentView(binding.root)
        AnimationHelper.animateItems(binding.constraintEdit,this)
        barangId = intent.getStringExtra("ID_BARANG") ?: ""
        if (barangId.isNotEmpty()) editViewModel.setCurrentBarang(barangId)
        val imgViewBack = binding.imgViewBack
        imgViewBack.setOnClickListener {
            finish()
        }
        setupUI()
        setupObservers()
    }
    private fun openCamera() {
        previousImageUri=selectedImageUri
        val intent = Intent(this, CameraActivity::class.java)
        cameraLauncher.launch(intent)
    }


    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uriString = result.data?.getStringExtra("imageUri")
            val photoUri = uriString?.let { Uri.parse(it) }

            if (photoUri != null) {
                val imageFile = uriToFile(photoUri, this).reduceFileImage()
                val savedUri = saveImageToStorage(Uri.fromFile(imageFile))
                if (savedUri != null) {
                    selectedImageUri = savedUri
                    binding.imageViewBarangEdit.setImageURI(selectedImageUri)
                    if (imageFile.exists()) {
                        imageFile.delete()
                        Log.d("CameraDebug", "File cache berhasil dihapus.")
                    }
                } else {
                    Log.e("CameraDebug", "Gagal menyimpan gambar ke MediaStore!")
                }
            } else {
                Log.e("CameraDebug", "URI hasil kamera null.")
            }
        } else {
            Log.w("CameraDebug", "Pengambilan gambar dibatalkan.")
        }
    }

    private fun openGallery() {
        previousImageUri=selectedImageUri
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            result.data?.data?.let { selectedUri ->
                val originalFile = uriToFile(selectedUri, this)
                val compressedFile = originalFile.reduceFileImage()
                selectedImageUri = null
                selectedImageUri = saveImageToStorage(Uri.fromFile(compressedFile))

                binding.imageViewBarangEdit.setImageURI(selectedImageUri)
            } ?: run {
                Toast.makeText(this, "Gagal mendapatkan URI gambar", Toast.LENGTH_SHORT).show()
            }
        }  else {

    }
    }
    private fun saveImageToStorage(imageUri: Uri): Uri? {
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
        val fileName = "IMG_${System.currentTimeMillis()}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/InventoryApp")
        }

        val savedUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        savedUri?.let { uri ->
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            }
            return uri
        }

        return null
    }
    private fun setupUI() {
        binding.buttonAddStokEdit.setOnClickListener { tambahStok() }
        binding.buttonRemoveStokEdit.setOnClickListener { kurangiStok() }

        val colorNames = resources.getStringArray(R.array.daftar_nama_warna)
        val colorValues = resources.getStringArray(R.array.daftar_warna)
        colorAdapter = AdapterColorIn(this, colorNames, colorValues)
        setupSpinners()

        binding.buttonCamera.setOnClickListener { openCamera() }
        binding.buttonGallery.setOnClickListener { openGallery() }
        binding.buttonSave.setOnClickListener {
            saveChanges()
        }
        binding.btncharater.setOnClickListener {
            PerformClickUtils.preventMultipleClick {
                selectedItems = binding.editKarakterikstikedit.text.toString()
                    .split(", ")
                    .filter { it.isNotBlank() }
                    .toMutableSet()

                val dialog = KarakteristikBottomSheetFragment(selectedItems) { updatedItems ->
                    selectedItems.clear()
                    selectedItems.addAll(updatedItems)
                    Log.d("KarakteristikFragment", "Selected Items Setelah Dialog Ditutup: $selectedItems")
                    updateKarakteristikText()
                }
                dialog.show(supportFragmentManager, "KarakteristikBottomSheet")
            }

        }
        HargaUtils.setupHargaTextWatcher(binding.editTextHargaBarangEdit)


        binding.editStokBarang.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val stokBarangText = s?.toString()?.trim() ?: ""
                val stokBarang = stokBarangText.toIntOrNull() ?: 0


                val currentText = binding.editTextUkuranwarnaEdit.text.toString()
                val jumlahKombinasi = if (currentText.isEmpty()) 0 else currentText.split(",").size


                binding.iconCheckEdit.isEnabled = stokBarang > jumlahKombinasi
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.edtUkuranEdit.addTextChangedListener(object : TextWatcher {
            val daftarUkuran = resources.getStringArray(R.array.daftar_ukuran_valid).toSet()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().trim()
                if (input.isNotEmpty() && input !in daftarUkuran) {
                    binding.edtUkuranEdit.error = "Ukuran tidak valid. Gunakan ukuran standar!"
                } else {
                    binding.edtUkuranEdit.error = null
                }
            }
        })
    }
    private fun setupSpinners() {
        val daftarUkuran = resources.getStringArray(R.array.daftar_ukuran_valid).toSet()
        val warnaList = resources.getStringArray(R.array.daftar_nama_warna)


        val warnaAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, warnaList)
        warnaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWarnaEdit.adapter = warnaAdapter


        binding.iconCloseEdit.setOnClickListener {
            binding.edtUkuranEdit.setText("")
            binding.spinnerWarnaEdit.setSelection(0)
        }


        binding.iconCheckEdit.setOnClickListener {
            val selectedUkuranText = binding.edtUkuranEdit.text.toString().trim()
            val selectedWarna = binding.spinnerWarnaEdit.selectedItem as String


            if (selectedUkuranText.isEmpty()) {
                binding.edtUkuranEdit.error = "Ukuran tidak boleh kosong"
                return@setOnClickListener
            }

            val selectedUkuran = selectedUkuranText.toDoubleOrNull()
                ?.let { it1 -> FormatAngkaUtils.formatAngka(it1) }
            if (selectedUkuranText !in daftarUkuran) {
                binding.edtUkuranEdit.error = "Ukuran tidak valid. Gunakan ukuran standar!"
                return@setOnClickListener
            }
            if (selectedWarna.equals("kosong", ignoreCase = true)) {
                Toast.makeText(this, "Pilih warna dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val stokBarangText = binding.editStokBarang.text.toString().trim()
            val stokBarang = stokBarangText.toIntOrNull() ?: 0


            val currentText = binding.editTextUkuranwarnaEdit.text.toString()
            val jumlahKombinasi = if (currentText.isEmpty()) 0 else currentText.split(",").size


            if (stokBarang == jumlahKombinasi) {
                Toast.makeText(
                    this,
                    "Tambahkan stok jika ingin menambahkan ukuran dan warna",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }


            val newEntry = "$selectedUkuran $selectedWarna"


            val updatedText = if (currentText.isEmpty()) {
                newEntry
            } else {
                "$currentText, $newEntry"
            }


            binding.editTextUkuranwarnaEdit.setText(updatedText)


            binding.edtUkuranEdit.setText("")
        }
        binding.removeUkuranwarna.setOnClickListener {
            val currentText = binding.editTextUkuranwarnaEdit.text.toString()

            if (currentText.isNotEmpty()) {
                val listEntries = currentText.split(",").map { it.trim() }.toMutableList()

                listEntries.removeLastOrNull()


                val updatedText = listEntries.joinToString(", ")
                binding.editTextUkuranwarnaEdit.setText(updatedText)
            } else {
                Toast.makeText(this, "Tidak ada ukuran-warna untuk dihapus", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setupObservers() {
        editViewModel.currentBarang.observe(this) { barang ->
            barang?.let {
                binding.editTextNamaBarangEdit.setText(it.merekBarang)
                binding.editTextKodeBarangEdit.setText(it.idBarang)
                binding.editKarakterikstikedit.setText(
                    if (it.karakteristik == "Belum diisi") "" else it.karakteristik
                )

                selectedImageUri = if (it.gambar.isNullOrEmpty()) null else Uri.parse(it.gambar)
                binding.imageViewBarangEdit.setImageURI(selectedImageUri)
            } ?: run {

                Toast.makeText(this, "Data barang tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
        editViewModel.currentStok.observe(this) { stok ->
            stok?.let {
                if (it.stokBarang == 0) {
                    binding.editTextUkuranwarnaEdit.setText("")
                    binding.editStokBarang.setText("")
                } else{
                binding.editTextUkuranwarnaEdit.setText(it.ukuranwarna)
                binding.editStokBarang.setText(it.stokBarang.toString())}
                stokBarang = it.stokBarang
            } ?: run {

                Toast.makeText(this, "Data stok tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
        editViewModel.currentBarangMasukItem.observe(this) { barangIn ->
            barangIn?.let {
                binding.editTextHargaBarangEdit.setText(HargaUtils.formatHarga(it.hargaModal))
                binding.edtNamaTokoEdit.setText(it.namaToko)
            } ?: run {

                Toast.makeText(this, "Data barang masuk tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun saveChanges() {
        val stokBarangText = binding.editStokBarang.text.toString().trim()
        val stokBarang = stokBarangText.toIntOrNull() ?: 0
        val tanggalupdate = DateUtils.getCurrentDate()
        val kodeupdate=binding.editTextKodeBarangEdit.text.toString()
        val karakteristik = if (binding.editKarakterikstikedit.text.toString().trim().isEmpty()) {
            "Belum diisi"
        } else {
            binding.editKarakterikstikedit.text.toString().trim()
        }

        val ukuranWarna = binding.editTextUkuranwarnaEdit.text.toString().trim()
        val jumlahKombinasi = if (ukuranWarna.isEmpty()) 0 else ukuranWarna.split(",").size


        if (stokBarang != jumlahKombinasi) {
            Toast.makeText(this, "Jumlah stok ($stokBarang) harus sama dengan jumlah kombinasi ukuran dan warna ($jumlahKombinasi)", Toast.LENGTH_SHORT).show()
            return
        }
        val hargaBarang =
            binding.editTextHargaBarangEdit.text.toString().replace(".", "").toIntOrNull() ?: 0
        if(hargaBarang==0){
            Toast.makeText(this, "Harga tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        val updatedBarang = editViewModel.currentBarang.value?.copy(
            merekBarang = binding.editTextNamaBarangEdit.text.toString(),
            idBarang = kodeupdate,
            karakteristik = karakteristik,
            gambar = selectedImageUri?.toString() ?: ""
        ) ?: run {
            Toast.makeText(this, "Data barang tidak valid", Toast.LENGTH_SHORT).show()
            return
        }
        val updatedStok = editViewModel.currentStok.value?.copy(
            stokBarang = binding.editStokBarang.text.toString().toIntOrNull() ?: 0,
            ukuranwarna = binding.editTextUkuranwarnaEdit.text.toString()
        ) ?: run {
            Toast.makeText(this, "Data stok tidak valid", Toast.LENGTH_SHORT).show()
            return
        }
        val updatedBarangMasuk = editViewModel.currentBarangMasukItem.value?.copy(
            hargaModal = hargaBarang,
            tglMasuk = tanggalupdate,
            namaToko = binding.edtNamaTokoEdit.text.toString()
        ) ?: run {
            Toast.makeText(this, "Data barang masuk tidak valid", Toast.LENGTH_SHORT).show()
            return
        }
        if (updatedBarang != null && updatedStok != null && updatedBarangMasuk != null) {
            editViewModel.updateBarang(updatedBarang, updatedStok, updatedBarangMasuk)
        }
        editViewModel.loadBarang()
        finish()
    }


    private fun updateKarakteristikText() {
        val karakteristik = selectedItems.joinToString(", ")
        binding.editKarakterikstikedit.setText(karakteristik)
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