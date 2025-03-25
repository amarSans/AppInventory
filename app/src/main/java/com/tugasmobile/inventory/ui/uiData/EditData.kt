package com.tugasmobile.inventory.ui.uiData

import android.app.Activity
import android.content.ContentValues
import android.content.Context
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterColorIn
import com.tugasmobile.inventory.databinding.ActivityEditDataBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.utils.HargaUtils
import com.tugasmobile.inventory.utils.getImageUri
import com.tugasmobile.inventory.utils.reduceFileImage
import com.tugasmobile.inventory.utils.uriToFile
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
        binding.buttonAddStokEdit.setOnClickListener { tambahStok() }
        binding.buttonRemoveStokEdit.setOnClickListener { kurangiStok() }

        val colorNames = resources.getStringArray(R.array.daftar_nama_warna)
        val colorValues = resources.getStringArray(R.array.daftar_warna)
        colorAdapter = AdapterColorIn(this, colorNames, colorValues)
        setupSpinners()

        binding.buttonCamera.setOnClickListener { openCamera() }
        binding.buttonGallery.setOnClickListener { openGallery() }
        binding.buttonSave.setOnClickListener {
            if (selectedImageUri != null) {
                // Jika gambar belum ada di MediaStore, simpan dulu
                val savedUri = if (selectedImageUri.toString().startsWith("content://media/")) {
                    selectedImageUri // Sudah di MediaStore, tidak perlu menyimpan ulang
                } else {
                    saveImageToStorage(selectedImageUri!!) // Simpan ke MediaStore
                }

                if (savedUri != null) {
                    selectedImageUri = savedUri // Simpan URI ke database
                    saveChanges()
                    Toast.makeText(this, "Gambar berhasil disimpan!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Tidak ada gambar yang dipilih!", Toast.LENGTH_SHORT).show()
            }
        }
        HargaUtils.setupHargaTextWatcher(binding.editTextHargaBarangEdit)

        // Tambahkan listener untuk memantau perubahan stok
        binding.editStokBarang.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ambil nilai stok
                val stokBarangText = s?.toString()?.trim() ?: ""
                val stokBarang = stokBarangText.toIntOrNull() ?: 0

                // Ambil teks yang sudah ada di EditText (ukuran dan warna yang sudah dipilih)
                val currentText = binding.editTextUkuranwarnaEdit.text.toString()
                val jumlahKombinasi = if (currentText.isEmpty()) 0 else currentText.split(",").size

                // Nonaktifkan tombol check jika stok sudah sama dengan jumlah kombinasi
                binding.iconCheckEdit.isEnabled = stokBarang > jumlahKombinasi
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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
            val selectedUkuranText = binding.edtUkuranEdit.text.toString().trim()
            val selectedWarna = binding.spinnerWarnaEdit.selectedItem as String

            // Pengecekan ukuran
            if (selectedUkuranText.isEmpty()) {
                binding.edtUkuranEdit.error = "Ukuran tidak boleh kosong"
                return@setOnClickListener
            }

            val selectedUkuran = selectedUkuranText.toIntOrNull()
            if (selectedUkuran == null || selectedUkuran !in 1..45) {
                binding.edtUkuranEdit.error = "Ukuran harus antara 1 - 45"
                return@setOnClickListener
            }
            if (selectedWarna.equals("kosong", ignoreCase = true)) {
                Toast.makeText(this, "Pilih warna dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ambil nilai stok
            val stokBarangText = binding.editStokBarang.text.toString().trim()
            val stokBarang = stokBarangText.toIntOrNull() ?: 0

            // Ambil teks yang sudah ada di EditText (ukuran dan warna yang sudah dipilih)
            val currentText = binding.editTextUkuranwarnaEdit.text.toString()
            val jumlahKombinasi = if (currentText.isEmpty()) 0 else currentText.split(",").size

            // Pengecekan apakah stok sudah sama dengan jumlah kombinasi
            if (stokBarang == jumlahKombinasi) {
                Toast.makeText(
                    this,
                    "Tambahkan stok jika ingin menambahkan ukuran dan warna",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Gabungkan ukuran dan warna
            val newEntry = "$selectedUkuran $selectedWarna"

            // Tambahkan entri baru ke EditText (dipisahkan koma jika sudah ada data)
            val updatedText = if (currentText.isEmpty()) {
                newEntry
            } else {
                "$currentText, $newEntry"
            }

            // Set teks ke EditText
            binding.editTextUkuranwarnaEdit.setText(updatedText)

            // Reset Spinner
            binding.edtUkuranEdit.setText("")
            binding.spinnerWarnaEdit.setSelection(0)
        }
    }


    private fun setupObservers() {
        editViewModel.currentBarang.observe(this) { barang ->
            barang?.let {
                binding.editTextNamaBarangEdit.setText(it.nama_barang)
                binding.editTextKodeBarangEdit.setText(it.id_barang)
                selectedImageUri = if (it.gambar.isNullOrEmpty()) null else Uri.parse(it.gambar)
                binding.imageViewBarangEdit.setImageURI(selectedImageUri)
            } ?: run {
                // Handle kasus ketika barang bernilai null
                Toast.makeText(this, "Data barang tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
        editViewModel.currentStok.observe(this) { stok ->
            stok?.let {
                binding.editTextUkuranwarnaEdit.setText(it.ukuranwarna.toString())
                binding.editStokBarang.setText(it.stokBarang.toString())
                stokBarang = it.stokBarang
            } ?: run {
                // Handle kasus ketika stok bernilai null
                Toast.makeText(this, "Data stok tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
        editViewModel.currentBarangIn.observe(this) { barangIn ->
            barangIn?.let {
                binding.editTextHargaBarangEdit.setText(HargaUtils.formatHarga(it.Harga_Modal))
                binding.edtNamaTokoEdit.setText(it.Nama_Toko)
            } ?: run {
                // Handle kasus ketika barangIn bernilai null
                Toast.makeText(this, "Data barang masuk tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveChanges() {
        val stokBarangText = binding.editStokBarang.text.toString().trim()
        val stokBarang = stokBarangText.toIntOrNull() ?: 0

        val ukuranWarna = binding.editTextUkuranwarnaEdit.text.toString().trim()
        val jumlahKombinasi = if (ukuranWarna.isEmpty()) 0 else ukuranWarna.split(",").size

        // Validasi jumlah kombinasi tidak melebihi stok
        if (stokBarang != jumlahKombinasi) {
            binding.editTextUkuranwarnaEdit.error =
                "Jumlah stok ($stokBarang) harus sama dengan jumlah kombinasi ukuran dan warna ($jumlahKombinasi)"
            return
        }
        val selectedColors = colorAdapter.getSelectedColors().toSet()
        val hargaBarang =
            binding.editTextHargaBarangEdit.text.toString().replace(".", "").toIntOrNull() ?: 0
        val updatedBarang = editViewModel.currentBarang.value?.copy(
            nama_barang = binding.editTextNamaBarangEdit.text.toString(),
            id_barang = binding.editTextKodeBarangEdit.text.toString(),
            gambar = selectedImageUri?.toString() ?: ""
        ) ?: run {
            Toast.makeText(this, "Data barang tidak valid", Toast.LENGTH_SHORT).show()
            return
        }
        val updatedStok = editViewModel.currentStok.value?.copy(
            stokBarang = binding.editStokBarang.text.toString().toIntOrNull() ?: 0,
            ukuranwarna = binding.editTextUkuranwarnaEdit.text.toString()
                .replace("[", "") // Hapus semua tanda "["
                .replace("]", "") // Hapus semua tanda "]"
                .trim() // Hapus spasi di awal dan akhir
                .split(",")
                .map { it.trim() }
        ) ?: run {
            Toast.makeText(this, "Data stok tidak valid", Toast.LENGTH_SHORT).show()
            return
        }
        val updatedBarangMasuk = editViewModel.currentBarangIn.value?.copy(
            Harga_Modal = hargaBarang,
            Nama_Toko = binding.edtNamaTokoEdit.text.toString()
        ) ?: run {
            Toast.makeText(this, "Data barang masuk tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        updatedBarang?.let {
            deleteImage(this,Uri.parse(it.gambar))
            editViewModel.updateWarna(it.id_barang, selectedColors.toList())
        }
        if (updatedBarang != null && updatedStok != null && updatedBarangMasuk != null) {
            editViewModel.updateBarang(updatedBarang, updatedStok, updatedBarangMasuk)
        }
        editViewModel.loadBarang()
        finish()
    }


    private fun openCamera() {
        val uri = getImageUri(this)
        photoUri = uri
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        cameraLauncher.launch(cameraIntent)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val imageFile = uriToFile(photoUri, this).reduceFileImage()

            val savedUri = saveImageToStorage(Uri.fromFile(imageFile))
            if (savedUri != null) {
                selectedImageUri = savedUri
                binding.imageViewBarangEdit.setImageURI(savedUri)
            } else {
                Log.e("CameraDebug", "Gagal menyimpan gambar ke MediaStore!")

            }
        } else {
            Log.w("CameraDebug", "Pengambilan gambar dibatalkan.")
        }
    }

    private fun openGallery() {
        selectedImageUri?.let { deleteImage(this,it) }
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            result.data?.data?.let { selectedUri ->
                val originalFile = uriToFile(selectedUri, this) // Ubah URI ke File
                val compressedFile = originalFile.reduceFileImage() // Kompres gambar
                selectedImageUri = saveImageToStorage(Uri.fromFile(compressedFile)) // Ubah kembali ke URI

                binding.imageViewBarangEdit.setImageURI(selectedImageUri)
            } ?: run {
                Toast.makeText(this, "Gagal mendapatkan URI gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToStorage(imageUri: Uri): Uri? {
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/InventoryApp")
        }

        val savedUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        savedUri?.let { uri ->
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Simpan gambar yang sudah dikompres
            }
            return uri
        }

        return null
    }

    private fun deleteImage(context: Context, imageUri: Uri) {
        try {
            val rowsDeleted = context.contentResolver.delete(imageUri, null, null)
            if (rowsDeleted > 0) {
                Log.d("EditActivity", "Gambar berhasil dihapus: $imageUri")
            } else {
                Log.w("EditActivity", "Gagal menghapus gambar atau gambar tidak ditemukan: $imageUri")
            }
        } catch (e: Exception) {
            Log.e("EditActivity", "Error saat menghapus gambar: ${e.message}", e)
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