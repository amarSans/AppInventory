package com.tugasmobile.inventory.ui.editdata

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterColorIn
import com.tugasmobile.inventory.databinding.FragmentEditBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.ui.uiData.BottonUkuranSheet
import java.io.File

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var editViewModel: ViewModel
    private var barangId: Long = 0
    private var stokBarang = 0
    private var selectedSizesList: List<String> = emptyList()
    private var selectedImageUri: Uri? = null
    private lateinit var photoUri: Uri
    private lateinit var colorAdapter: AdapterColorIn

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        editViewModel = ViewModelProvider(this).get(ViewModel::class.java)
        _binding = FragmentEditBinding.inflate(inflater, container, false)

        barangId = arguments?.getLong("ID_BARANG", 0) ?: 0L
        if (barangId != 0L) {
            editViewModel.setCurrentBarang(barangId)
        }

        setupUI()
        setupObservers()

        return binding.root
    }

    private fun setupUI() {
        binding.buttonAddStok.setOnClickListener { tambahStok() }
        binding.buttonRemoveStok.setOnClickListener { kurangiStok() }

        val colorNames = resources.getStringArray(R.array.daftar_nama_warna)
        val colorValues = resources.getStringArray(R.array.daftar_warna)
        colorAdapter = AdapterColorIn(requireContext(), colorNames, colorValues)

        binding.recyclerViewColorsEdit.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
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
            bottonUkuranSheet.show(parentFragmentManager, BottonUkuranSheet.TAG)
        }

        binding.buttonCamera.setOnClickListener { openCamera() }
        binding.buttonGallery.setOnClickListener { openGallery() }

        binding.buttonSave.setOnClickListener { saveChanges(colorAdapter) }
    }

    private fun setupObservers() {
        editViewModel.currentBarangPrototype.observe(viewLifecycleOwner) { barang ->
            barang?.let {
                binding.editTextNamaBarang.setText(it.namaBarang)
                binding.editStokBarang.setText(it.stok.toString())
                binding.editTextHargaBarang.setText(it.harga.toString())
                binding.editTextKodeBarang.setText(it.kodeBarang)
                binding.edtNamaToko.setText(it.nama_toko)
                stokBarang = it.stok
                binding.editTextDate.setText(it.waktu)
                binding.edtUkuran.text = it.ukuran
                val warnaFromDb = it.warna
                colorAdapter.setSelectedColors(warnaFromDb)

                selectedSizesList = it.ukuran.split(",").map { size -> size.trim() }
                selectedImageUri = Uri.parse(it.gambar)
                binding.imageViewBarang.setImageURI(selectedImageUri)


            }
        }
    }

    private fun saveChanges(colorAdapter: AdapterColorIn) {
        val selectedColors = colorAdapter.getSelectedColors().toSet()
        val updatedBarang = editViewModel.currentBarangPrototype.value?.copy(
            namaBarang = binding.editTextNamaBarang.text.toString(),
            stok = binding.editStokBarang.text.toString().toInt(),
            harga = binding.editTextHargaBarang.text.toString().toInt(),
            kodeBarang = binding.editTextKodeBarang.text.toString(),
            warna = selectedColors.toList(),
            nama_toko = binding.edtNamaToko.text.toString(),
            ukuran = binding.edtUkuran.text.toString(),
            gambar = selectedImageUri?.toString() ?: ""
        )

        updatedBarang?.let {
            deleteImage(Uri.parse(it.gambar)) // Hapus gambar sebelumnya
            editViewModel.updateWarna(it.id, selectedColors.toList())
            editViewModel.updateLaporan(it)
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    private fun openCamera() {
        selectedImageUri?.let { deleteImage(it) }
        val photoFile = File(getAppSpecificAlbumStorageDir(), "IMG_${System.currentTimeMillis()}.jpg").apply {
            if (!parentFile.exists()) {
                parentFile.mkdirs()
            }
            photoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
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
            editViewModel.currentBarangPrototype.value?.gambar = selectedImageUri.toString()
            Toast.makeText(requireContext(), "Gambar berhasil disimpan di folder aplikasi.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Pengambilan gambar dibatalkan", Toast.LENGTH_SHORT).show()
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
            editViewModel.currentBarangPrototype.value?.gambar = selectedImageUri.toString()
        }
    }

    private fun deleteImage(imageUri: Uri) {
        val fileToDelete = File(imageUri.path ?: return)
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                Log.d("EditFragment", "Gambar berhasil dihapus: ${fileToDelete.absolutePath}")
            } else {
                Log.e("EditFragment", "Gagal menghapus gambar: ${fileToDelete.absolutePath}")
            }
        }
    }

    private fun getAppSpecificAlbumStorageDir(): File {
        return File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "InventoryApp").apply {
            if (!exists()) {
                mkdirs()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
