package com.tugasmobile.inventory.ui.uiData

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dicoding.picodiploma.mycamera.getImageUri
import com.dicoding.picodiploma.mycamera.reduceFileImage
import com.dicoding.picodiploma.mycamera.uriToFile
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.ItemBarang
import com.tugasmobile.inventory.data.BarangIn
import com.tugasmobile.inventory.data.History
import com.tugasmobile.inventory.data.Stok
import com.tugasmobile.inventory.databinding.ActivityAddDataBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.utils.DateUtils
import com.tugasmobile.inventory.utils.HargaUtils
import java.io.File
import java.io.FileOutputStream

class addData : AppCompatActivity() {
    private val viewModel: ViewModel by viewModels()
    private lateinit var binding: ActivityAddDataBinding
    private var stokBarang = 0

    private lateinit var selectedSizesColorList: List<String>
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
            val originalFile = uriToFile(photoUri!!, this) // Ubah URI ke File
            val compressedFile = originalFile.reduceFileImage() // Kompres gambar
            selectedImageUri = Uri.fromFile(compressedFile) // Ubah kembali ke URI

            binding.imageViewBarang.setImageURI(selectedImageUri)

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

    fun generateKodeBarang(kodeBarang: String, checkExist: (String) -> Boolean): String {
        var kode: String
        do {
            val prefix = if (kodeBarang.isBlank()) "" else kodeBarang.take(3).uppercase()
            kode = "SND$prefix${(1000..9999).random()}"
        } while (checkExist(kode))
        return kode
    }
    private fun getDefaultImageUri(): Uri {
        val drawableResourceId = R.drawable.baseline_image_24 // ID gambar default
        val resources = resources
        return Uri.parse("android.resource://${packageName}/$drawableResourceId")
    }


    private fun saveData() {
        val gambarUri = selectedImageUri ?: getDefaultImageUri()


        val namaProduk = binding.editTextNamaBarang.text.toString().trim()
        if (namaProduk.isEmpty()) {
            binding.editTextNamaBarang.error = "Nama barang tidak boleh kosong"
            return
        }
        var kodeProduk = binding.editTextKodeBarang.text.toString()
        if (kodeProduk.isBlank()) {
            kodeProduk = generateKodeBarang(kodeProduk) { kode ->
                viewModel.cekKodeBarangAda(kode)
            }
        }
        val hargaProdukText = binding.editTextHargaBarang.text.toString().replace(".", "").trim()
        if (hargaProdukText.isEmpty()) {
            binding.editTextHargaBarang.error = "Harga barang tidak boleh kosong"
            return
        }
        val hargaProduk = hargaProdukText.toIntOrNull()
        if (hargaProduk == null || hargaProduk <= 0) {
            binding.editTextHargaBarang.error = "Harga barang harus berupa angka dan lebih dari 0"
            return
        }

        val stokBarangText = binding.editStokBarang.text.toString().trim()
        if (stokBarangText.isEmpty()) {
            binding.editStokBarang.error = "Stok barang tidak boleh kosong"
            return
        }
        val stokBarang = stokBarangText.toIntOrNull() ?: 0

        val ukuranWarna = binding.editTextUkuranwarna.text.toString().trim()
        if (ukuranWarna.isEmpty()) {
            binding.editTextUkuranwarna.error = "Ukuran dan warna tidak boleh kosong"
            return
        }

        val jumlahKombinasi = if (ukuranWarna.isEmpty()) 0 else ukuranWarna.split(",").size
        if (stokBarang != jumlahKombinasi) {
            binding.editTextUkuranwarna.error = "Jumlah stok ($stokBarang) harus sama dengan jumlah kombinasi ukuran dan warna ($jumlahKombinasi)"
            return
        }
        // Simpan ke selectedSizesList
        selectedSizesColorList = ukuranWarna.split(",").map { it.trim() }

        val namaToko = binding.edtNamaToko.text.toString().trim()
        if (namaToko.isEmpty()) {
            binding.edtNamaToko.error = "Nama toko tidak boleh kosong"
            return
        }

        val itemBarang=ItemBarang(
            id_barang = kodeProduk,
            nama_barang = namaProduk,
            gambar = gambarUri.toString()
        )
        val stok= Stok(
            idStok = 0,
            id_barang = kodeProduk,
            stokBarang = stokBarang,
            ukuranwarna = selectedSizesColorList,
        )
        val barangIn = BarangIn(
            IdBrgMasuk = 0,
            id_barang = kodeProduk,
            Tgl_Masuk = DateUtils.getCurrentDate(),
            Harga_Modal = hargaProduk,
            Nama_Toko =namaToko
        )
        val harga_history = HargaUtils.formatHarga(hargaProduk)
        val history=History(
            id = 0,
            waktu = DateUtils.getCurrentDate(),
            kodeBarang = kodeProduk,
            stok = stokBarang.toString(),
            ukuranWarna = ukuranWarna,
            harga = harga_history,
            jenisData = true
        )
        viewModel.insertHistory(history)
        viewModel.insertInputBarang(itemBarang,stok,barangIn)
        Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        finish()
    }
    private fun saveImageToStorage() {
        selectedImageUri?.let { uri ->
            val savedUri = getImageUri(this) // Dapatkan lokasi penyimpanan
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = contentResolver.openOutputStream(savedUri!!)

            inputStream?.copyTo(outputStream!!)
            inputStream?.close()
            outputStream?.close()


            Toast.makeText(this, "Gambar berhasil disimpan.", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(this, "Tidak ada gambar untuk disimpan.", Toast.LENGTH_SHORT).show()
    }

    private fun setupUI() {
        // Inisialisasi UI dan tombol untuk stok dan warna
        // Serta set onClickListener pada tombol kamera dan galeri
        binding.buttonAddStok.setOnClickListener { tambahStok() }
        binding.buttonRemoveStok.setOnClickListener { kurangiStok() }
        binding.buttonSave.setOnClickListener {
            saveData()
            saveImageToStorage()
        }
        binding.buttonCamera.setOnClickListener { openCamera() }
        binding.buttonGallery.setOnClickListener { openGallery() }
        binding.editTextDate.setText(DateUtils.getCurrentDate())
        setupSpinners()
        HargaUtils.setupHargaTextWatcher(binding.editTextHargaBarang)
        binding.editStokBarang.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ambil nilai stok
                val stokBarangText = s?.toString()?.trim() ?: ""
                val stokBarang = stokBarangText.toIntOrNull() ?: 0

                // Ambil teks yang sudah ada di EditText (ukuran dan warna yang sudah dipilih)
                val currentText = binding.editTextUkuranwarna.text.toString()
                val jumlahKombinasi = if (currentText.isEmpty()) 0 else currentText.split(",").size

                // Nonaktifkan tombol check jika stok sudah sama dengan jumlah kombinasi
                binding.iconCheck.isEnabled = stokBarang > jumlahKombinasi
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun setupSpinners() {
        val warnaList = resources.getStringArray(R.array.daftar_nama_warna)
        binding.spinnerWarna.prompt = "Pilih Warna"
        // Inisialisasi adapter untuk Spinner warna
        val warnaAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, warnaList)
        warnaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWarna.adapter = warnaAdapter

        // Listener untuk icon close
        binding.iconClose.setOnClickListener {
            binding.edtUkuran.setText("") // Reset Spinner ukuran
            binding.spinnerWarna.setSelection(0)  // Reset Spinner warna
        }

        // Listener untuk icon check
        binding.iconCheck.setOnClickListener {
            val selectedUkuranText = binding.edtUkuran.text.toString().trim()
            val selectedWarna = binding.spinnerWarna.selectedItem as String

            if (selectedUkuranText.isEmpty()) {
                binding.edtUkuran.error = "Ukuran tidak boleh kosong"
                return@setOnClickListener
            }
            val selectedUkuran = selectedUkuranText.toIntOrNull()
            if (selectedUkuran == null || selectedUkuran !in 1..45) {
                binding.edtUkuran.error = "Ukuran harus antara 1 - 45"
                return@setOnClickListener
            }
            if (selectedWarna.equals("kosong", ignoreCase = true)) {
                Toast.makeText(this, "Pilih warna dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Gabungkan ukuran dan warna
            val newEntry = "$selectedUkuran $selectedWarna"

            // Ambil teks yang sudah ada di EditText
            val currentText = binding.editTextUkuranwarna.text.toString()
            val jumlahKombinasi = if (currentText.isEmpty()) 0 else currentText.split(",").size

            // Pengecekan apakah stok sudah sama dengan jumlah kombinasi
            if (stokBarang == jumlahKombinasi) {
                Toast.makeText(this, "Tambahkan stok jika ingin menambahkan ukuran dan warna", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Tambahkan entri baru ke EditText (dipisahkan koma jika sudah ada data)
            val updatedText = if (currentText.isEmpty()) {
                newEntry
            } else {
                "$currentText, $newEntry"
            }

            // Set teks ke EditText
            binding.editTextUkuranwarna.setText(updatedText)

            // Reset Spinner
            binding.edtUkuran.setText("")
            binding.spinnerWarna.setSelection(0)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val selectedUri = result.data?.data
            val originalFile = uriToFile(selectedUri!!, this) // Ubah URI ke File
            val compressedFile = originalFile.reduceFileImage() // Kompres gambar
            selectedImageUri = Uri.fromFile(compressedFile) // Ubah kembali ke URI

            binding.imageViewBarang.setImageURI(selectedImageUri)
        }
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
