package com.muammar.inventory.ui.data

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
import com.muammar.inventory.R
import com.muammar.inventory.data.BarangIn
import com.muammar.inventory.data.History
import com.muammar.inventory.data.ItemBarang
import com.muammar.inventory.data.Stok
import com.muammar.inventory.databinding.FragmentTambahBarangBinding
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

private const val ARG_KODE_BARANG = "kodeBarang"

class TambahBarangFragment : Fragment() {
    private var kodeBarang: String? = null
    private lateinit var binding: FragmentTambahBarangBinding
    private var stokBarang = 0
    private val NewItemViewModel:DataViewModel by viewModels {
        InventoryViewModelFactory.getInstance(requireActivity().application)
    }

    private val selectedItems = mutableSetOf<String>()
    private var selectedImageUri: Uri? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            kodeBarang = it.getString(ARG_KODE_BARANG)
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
        AnimationHelper.animateItems(binding.constraintTambahbarang,requireContext())
        binding.imgViewBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        setupUI()
    }
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }
    private fun openCamera() {
        val intent = Intent(requireContext(), CameraActivity::class.java)
        cameraLauncher.launch(intent)
    }


    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uriString = result.data?.getStringExtra("imageUri")
            val photoUri = uriString?.let { Uri.parse(it) }

            if (photoUri != null) {
                val imageFile = uriToFile(photoUri, requireContext()).reduceFileImage()
                val savedUri = saveImageToStorage(Uri.fromFile(imageFile))
                if (savedUri != null) {
                    selectedImageUri = savedUri
                    binding.imageViewBarang.setImageURI(selectedImageUri)
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
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            result.data?.data?.let { selectedUri ->
                val originalFile = uriToFile(selectedUri, requireContext())
                val compressedFile = originalFile.reduceFileImage()
                selectedImageUri = saveImageToStorage(Uri.fromFile(compressedFile))

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
        val drawableResourceId = R.drawable.baseline_image_24
        return Uri.parse("android.resource://${requireContext().packageName}/$drawableResourceId")
    }
    private fun saveData() {
        val gambarUri = selectedImageUri ?: getDefaultImageUri()
        val merekProduk = binding.editTextNamaBarang.text.toString().trim()
        if (merekProduk.isEmpty()) {
            binding.editTextNamaBarang.error = "Nama barang tidak boleh kosong"
            return
        }
        var kodeProduk = binding.editTextKodeBarang.text.toString()
        if (kodeProduk.isBlank()) {
            kodeProduk = generateKodeBarang(kodeProduk) { kode ->
                NewItemViewModel.cekKodeBarangAda(kode)
            }
        }
        val karakteristik = if (binding.editKarakterikstik.text.toString().trim().isEmpty()) {
            "Belum diisi"
        } else {
            binding.editKarakterikstik.text.toString().trim()
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

        val namaTokoPreview = binding.edtNamaToko.text.toString().trim()
        val namaToko = if (namaTokoPreview.isEmpty()) "belum ada" else namaTokoPreview

        val itemBarang= ItemBarang(
            id_barang = kodeProduk,
            merek_barang = merekProduk,
            karakteristik = karakteristik,
            gambar = gambarUri.toString(),
            lastUpdate = DateUtils.getCurrentDate()
        )
        val stok= Stok(
            idStok = 0,
            id_barang = kodeProduk,
            stokBarang = stokBarang,
            ukuranwarna = ukuranWarna,
        )
        val barangIn = BarangIn(
            IdBrgMasuk = 0,
            id_barang = kodeProduk,
            Tgl_Masuk = DateUtils.getCurrentDate(),
            Harga_Modal = hargaProduk,
            Nama_Toko =namaToko
        )

        val history= History(
            id = 0,
            waktu = DateUtils.getCurrentDate(),
            kodeBarang = kodeProduk,
            stok = stokBarang.toString(),
            jenisData = "barangmasuk"
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
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
            PerformClickUtils.preventMultipleClick {
                val dialog = KarakteristikBottomSheetFragment(selectedItems) { updatedItems ->
                    selectedItems.clear()
                    selectedItems.addAll(updatedItems)
                    Log.d("KarakteristikFragment", "Selected Items Setelah Dialog Ditutup: $selectedItems")
                    updateKarakteristikText()
                }
                dialog.show(parentFragmentManager, "KarakteristikBottomSheet")
            }

        }
        setupSpinners()
        HargaUtils.setupHargaTextWatcher(binding.editTextHargaBarang)
        binding.editStokBarang.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val stokBarangText = s?.toString()?.trim() ?: ""
                val stokBarang = stokBarangText.toIntOrNull() ?: 0


                val currentText = binding.editTextUkuranwarna.text.toString()
                val jumlahKombinasi = if (currentText.isEmpty()) 0 else currentText.split(",").size


                binding.iconCheck.isEnabled = stokBarang > jumlahKombinasi
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.edtUkuran.addTextChangedListener(object : TextWatcher {
            val daftarUkuran = resources.getStringArray(R.array.daftar_ukuran_valid).toSet()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().trim()
                if (input.isNotEmpty() && input !in daftarUkuran) {
                    binding.edtUkuran.error = "Ukuran tidak valid. Gunakan ukuran standar!"
                } else {
                    binding.edtUkuran.error = null
                }
            }
        })
    }
    private fun setupSpinners() {
        val daftarUkuran = resources.getStringArray(R.array.daftar_ukuran_valid).toSet()
        val warnaList = resources.getStringArray(R.array.daftar_nama_warna)
        binding.spinnerWarna.prompt = "Pilih Warna"

        val warnaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, warnaList)
        warnaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWarna.adapter = warnaAdapter


        binding.iconClose.setOnClickListener {
            binding.edtUkuran.setText("")
            binding.spinnerWarna.setSelection(0)
        }


        binding.iconCheck.setOnClickListener {
            val selectedUkuranText = binding.edtUkuran.text.toString().trim()
            val selectedWarna = binding.spinnerWarna.selectedItem as String

            if (selectedUkuranText.isEmpty()) {
                binding.edtUkuran.error = "Ukuran tidak boleh kosong"
                return@setOnClickListener
            }
            val selectedUkuran = selectedUkuranText.toDoubleOrNull()
                ?.let { it1 -> FormatAngkaUtils.formatAngka(it1) }
            if (selectedUkuranText !in daftarUkuran) {
                binding.edtUkuran.error = "Ukuran tidak valid. Gunakan ukuran standar!"
                return@setOnClickListener
            }
            if (selectedWarna.equals("kosong", ignoreCase = true)) {
                Toast.makeText(requireContext(), "Pilih warna dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val newEntry = "$selectedUkuran $selectedWarna"


            val currentText = binding.editTextUkuranwarna.text.toString()
            val jumlahKombinasi = if (currentText.isEmpty()) 0 else currentText.split(",").size


            if (stokBarang == jumlahKombinasi) {
                Toast.makeText(requireContext(), "Tambahkan stok jika ingin menambahkan ukuran dan warna", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedText = if (currentText.isEmpty()) {
                newEntry
            } else {
                "$currentText, $newEntry"
            }


            binding.editTextUkuranwarna.setText(updatedText)


            binding.edtUkuran.setText("")
            binding.spinnerWarna.setSelection(0)
        }
        binding.removeUkuranwarna.setOnClickListener {
            val currentText = binding.editTextUkuranwarna.text.toString()

            if (currentText.isNotEmpty()) {
                val listEntries = currentText.split(", ").toMutableList()
                listEntries.removeLastOrNull()


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
        val karakteristik = selectedItems.joinToString(", ")
        Log.d("KarakteristikFragment", "Karakteristik yang dipilih: $karakteristik")
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
