package com.tugasmobile.inventory.ui.data

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.BarangIn
import com.tugasmobile.inventory.data.History
import com.tugasmobile.inventory.data.ItemBarang
import com.tugasmobile.inventory.data.Stok
import com.tugasmobile.inventory.databinding.FragmentTambahBarangBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.ui.simpleItem.KarakteristikBottomSheetFragment
import com.tugasmobile.inventory.utils.DateUtils
import com.tugasmobile.inventory.utils.HargaUtils
import com.tugasmobile.inventory.utils.getCacheImageUri
import com.tugasmobile.inventory.utils.reduceFileImage
import com.tugasmobile.inventory.utils.uriToFile

private const val ARG_KODE_BARANG = "kodeBarang"

class TambahBarangFragment : Fragment() {
    private var kodeBarang: String? = null
    private lateinit var binding: FragmentTambahBarangBinding
    private var stokBarang = 0
    private val NewItemViewModel:ViewModel by viewModels()

    private val selectedItems = mutableSetOf<String>()
    private lateinit var selectedSizesColorList: List<String>
    private var selectedImageUri: Uri? = null
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            kodeBarang = it.getString(ARG_KODE_BARANG) // Ambil kode barang dari arguments
        }
        checkPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTambahBarangBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        binding.imgViewBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        setupUI()
    }
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                TambahBarangFragment.REQUEST_CODE_PERMISSIONS
            )
        }
    }
    private fun openCamera() {
        val uri = getCacheImageUri(requireContext())
        photoUri = uri
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
        cameraLauncher.launch(cameraIntent)
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val imageFile = uriToFile(photoUri, requireContext()).reduceFileImage()
            val savedUri = saveImageToStorage(Uri.fromFile(imageFile))
            if (savedUri != null) {
                selectedImageUri = savedUri
                binding.imageViewBarang.setImageURI(selectedImageUri)

            } else {
                Log.e("CameraDebug", "Gagal menyimpan gambar ke MediaStore!")

            }
        } else {
            Log.w("CameraDebug", "Pengambilan gambar dibatalkan.")
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            result.data?.data?.let { selectedUri ->
                val originalFile = uriToFile(selectedUri, requireContext()) // Ubah URI ke File
                val compressedFile = originalFile.reduceFileImage() // Kompres gambar
                selectedImageUri = saveImageToStorage(Uri.fromFile(compressedFile)) // Ubah kembali ke URI

                binding.imageViewBarang.setImageURI(selectedImageUri)
            } ?: run {
                Toast.makeText(requireContext(), "Gagal mendapatkan URI gambar", Toast.LENGTH_SHORT).show()
            }
        }
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
        return Uri.parse("android.resource://${requireContext().packageName}/$drawableResourceId")
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
                NewItemViewModel.cekKodeBarangAda(kode)
            }
        }
        var karakteristik = binding.editKarakterikstik.text.toString().trim()

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

        selectedSizesColorList = ukuranWarna.split(",").map { it.trim() }

        val namaTokoPreview = binding.edtNamaToko.text.toString().trim()
        val namaToko = if (namaTokoPreview.isEmpty()) "belum ada" else namaTokoPreview

        val itemBarang= ItemBarang(
            id_barang = kodeProduk,
            merek_barang = namaProduk,
            karakteristik = karakteristik,
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
        val history= History(
            id = 0,
            waktu = DateUtils.getCurrentDate(),
            kodeBarang = kodeProduk,
            stok = stokBarang.toString(),
            ukuranWarna = ukuranWarna,
            harga = harga_history,
            jenisData = true
        )
        NewItemViewModel.insertHistory(history)
        NewItemViewModel.insertInputBarang(itemBarang,stok,barangIn)
        Toast.makeText(requireContext(), "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }
    private fun saveImageToStorage(imageUri: Uri): Uri? {
        val bitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(imageUri))
        val fileName = "IMG_${System.currentTimeMillis()}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/InventoryApp")
        }

        val savedUri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        savedUri?.let { uri ->
            requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Simpan gambar yang sudah dikompres
            }
            return uri
        }

        return null
    }
    private fun setupUI() {
        binding.editTextKodeBarang.setText(kodeBarang)
        binding.buttonAddStok.setOnClickListener { tambahStok() }
        binding.buttonRemoveStok.setOnClickListener { kurangiStok() }
        binding.buttonSave.setOnClickListener {
            saveData()
        }

        binding.buttonCamera.setOnClickListener { openCamera() }
        binding.buttonGallery.setOnClickListener { openGallery() }
        binding.btncharater.setOnClickListener {
            val dialog = KarakteristikBottomSheetFragment(selectedItems) { updatedItems ->
                selectedItems.clear()
                selectedItems.addAll(updatedItems)
                Log.d("KarakteristikFragment", "Selected Items Setelah Dialog Ditutup: $selectedItems")
                updateKarakteristikText()
            }
            dialog.show(parentFragmentManager, "KarakteristikBottomSheet")
        }
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
        val warnaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, warnaList)
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
                Toast.makeText(requireContext(), "Pilih warna dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Gabungkan ukuran dan warna
            val newEntry = "$selectedUkuran $selectedWarna"

            // Ambil teks yang sudah ada di EditText
            val currentText = binding.editTextUkuranwarna.text.toString()
            val jumlahKombinasi = if (currentText.isEmpty()) 0 else currentText.split(",").size

            // Pengecekan apakah stok sudah sama dengan jumlah kombinasi
            if (stokBarang == jumlahKombinasi) {
                Toast.makeText(requireContext(), "Tambahkan stok jika ingin menambahkan ukuran dan warna", Toast.LENGTH_SHORT).show()
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
        binding.removeUkuranwarna.setOnClickListener {
            val currentText = binding.editTextUkuranwarna.text.toString()

            if (currentText.isNotEmpty()) {
                val listEntries = currentText.split(", ").toMutableList()
                listEntries.removeLastOrNull()  // Hapus item terakhir jika ada

                // Set teks baru setelah penghapusan
                val updatedText = listEntries.joinToString(", ")
                binding.editTextUkuranwarna.setText(updatedText)
            } else {
                Toast.makeText(requireContext(), "Tidak ada ukuran-warna untuk dihapus", Toast.LENGTH_SHORT).show()
            }
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
    private fun updateKarakteristikText() {
        val karakteristik = if (selectedItems.isEmpty()) "Belum diisi" else selectedItems.joinToString(", ")
        Log.d("KarakteristikFragment", "Karakteristik yang dipilih: $karakteristik") // 🔥 Debugging
        binding.editKarakterikstik.setText(karakteristik)
    }


    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1001
        @JvmStatic
        fun newInstance(kodeBarang: String) =
            TambahBarangFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_KODE_BARANG, kodeBarang)
                }
            }
    }
}
