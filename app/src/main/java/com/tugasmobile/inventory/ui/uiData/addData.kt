package com.tugasmobile.inventory.ui.uiData

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterColorIn
import com.tugasmobile.inventory.data.ItemBarang
import com.tugasmobile.inventory.data.BarangIn
import com.tugasmobile.inventory.data.Stok
import com.tugasmobile.inventory.databinding.ActivityAddDataBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.ui.simpleItem.BottonUkuranSheet
import com.tugasmobile.inventory.ui.simpleItem.DateUtils
import com.tugasmobile.inventory.ui.simpleItem.HargaUtils
import java.io.File

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

    fun generateKodeBarang(kodeBarang: String, checkExist: (String) -> Boolean): String {
        var kode: String
        do {
            val prefix = if (kodeBarang.isBlank()) "" else kodeBarang.take(3).uppercase()
            kode = "SND$prefix${(1000..9999).random()}"
        } while (checkExist(kode))
        return kode
    }


    private fun saveData() {
        var kodeProduk = binding.editTextKodeBarang.text.toString()
        if (kodeProduk.isBlank()) {
            kodeProduk = generateKodeBarang(kodeProduk) { kode ->
                viewModel.cekKodeBarangAda(kode)
            }
        }
        val namaProduk = binding.editTextNamaBarang.text.toString().trim()
        if (namaProduk.isEmpty()) {
            binding.editTextNamaBarang.error = "Nama barang tidak boleh kosong"
            return
        }
        val stokBarangText = binding.editStokBarang.text.toString().trim()
        if (stokBarangText.isEmpty()) {
            binding.editStokBarang.error = "Stok barang tidak boleh kosong"
            return
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

        val selectedColors = (recyclerView.adapter as AdapterColorIn).getSelectedColors()
        if (selectedColors.isEmpty()) {
            Toast.makeText(this, "Pilih minimal satu warna", Toast.LENGTH_SHORT).show()
            return
        }
        val namaToko = binding.edtNamaToko.text.toString().trim()
        if (namaToko.isEmpty()) {
            binding.edtNamaToko.error = "Nama toko tidak boleh kosong"
            return
        }

        val itemBarang=ItemBarang(
            id_barang = kodeProduk,
            nama_barang = namaProduk,
            gambar = selectedImageUri.toString()
        )
        val stok= Stok(
            idStok = 0,
            id_barang = kodeProduk,
            stokBarang = stokBarang,
            warna = selectedColors,
            ukuran = selectedSizesList,
        )
        val barangIn = BarangIn(
            IdBrgMasuk = 0,
            id_barang = kodeProduk,
            Tgl_Masuk = DateUtils.getCurrentDate(),
            Harga_Modal = hargaProduk,
            Nama_Toko =namaToko
        )
        viewModel.insertInputBarang(itemBarang,stok,barangIn)
        Toast.makeText(this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
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
        binding.editTextDate.setText(DateUtils.getCurrentDate())
        binding.edtUkuran.setOnClickListener {
            selectedSizesList = ""
            val stok = binding.editStokBarang.text.toString().toIntOrNull() ?: 0

            // Buat instance BottomSheet dan kirim nilai stok
            val bottonUkuranSheet = BottonUkuranSheet.newInstance(selectedSizesList.split(","),stok)
            bottonUkuranSheet.listener = object : BottonUkuranSheet.SizeSelectionListener {
                override fun onSizeSelected(selectedSizes: List<String>) {
                    selectedSizesList = selectedSizes.filter { it.isNotBlank() }.joinToString(", ")
                    binding.edtUkuran.text = selectedSizesList
                }
            }
            bottonUkuranSheet.show(supportFragmentManager, BottonUkuranSheet.TAG)
        }


        // Inisialisasi lainnya
        recyclerView = findViewById(R.id.recyclerViewColors) // Pastikan RecyclerView sudah di-inisialisasi
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = AdapterColorIn(this, resources.getStringArray(R.array.daftar_nama_warna), resources.getStringArray(R.array.daftar_warna))
        HargaUtils.setupHargaTextWatcher(binding.editTextHargaBarang)
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
