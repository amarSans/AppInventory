package com.tugasmobile.inventory.ui.editdata

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterColorOut
import com.tugasmobile.inventory.databinding.FragmentRincianBinding
import com.tugasmobile.inventory.ui.ViewModel


class RincianFragment : Fragment() {
    private var _binding:FragmentRincianBinding?=null
    private val binding get() = _binding!!
    private var gambarUri: Uri? = null
    private lateinit var rincianViewModel: ViewModel
    private var BarangId:Long=0
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rincianViewModel = ViewModelProvider(this).get(ViewModel::class.java)
        _binding = FragmentRincianBinding.inflate(inflater,container,false)
        val root:View=binding.root
        BarangId = arguments?.getLong("ID_BARANG") ?: 0L
        val colorNames = resources.getStringArray(R.array.daftar_nama_warna)
        val colorValues = resources.getStringArray(R.array.daftar_warna)
        val colorMap = colorNames.indices.associate { index ->
            colorNames[index] to colorValues[index]
        }
// Inisialisasi RecyclerView dan Adapter
        val recyclerView: RecyclerView = binding.rvWarnaSendal // Sesuaikan dengan ID RecyclerView Anda
        val colorAdapter = AdapterColorOut(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = colorAdapter

        checkPermission()

        // Gunakan barangId untuk memuat detail barang
        rincianViewModel.setCurrentBarang(BarangId)
        rincianViewModel.currentBarang.observe(viewLifecycleOwner){ barang->
            barang?.let{
                binding.tvNamaBarang.text = it.nama_barang
                binding.tvKodeBarang.text = it.kode_barang
                gambarUri = it.gambar.let { Uri.parse(it) }
                gambarUri?.let { uri ->
                    // Menyimpan URI untuk digunakan setelah izin diberikan
                    loadImage(uri)
            }
        }}
        rincianViewModel.currentStok.observe(viewLifecycleOwner) { stok ->
            stok?.let {
                binding.tvStok.text = it.stokBarang.toString()
                binding.TVUkuran.text = it.ukuran
                val warnaFromDb = it.warna.map { warna -> warna.trim() }
                val selectedColorValues = warnaFromDb.mapNotNull { colorMap[it] }
                colorAdapter.updateColors(warnaFromDb, selectedColorValues)

            }
        }
        rincianViewModel.currentBarangIn.observe(viewLifecycleOwner){barangIn->
            barangIn?.let{
                binding.tvHarga.text = "Rp. ${it.Harga_Modal}"
                binding.tvNamaToko.text = it.Nama_Toko
            }
        }
        return root  // Inflate the layout for this fragment
    }
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_READ_EXTERNAL_STORAGE)
        } else {
            gambarUri?.let { uri ->
                loadImage(uri)
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, lakukan tindakan yang memerlukan izin
                rincianViewModel.currentDataBarangMasuk.value?.gambar?.let { gambar ->
                    loadImage(Uri.parse(gambar))
                }
            } else {
                // Izin ditolak, berikan penjelasan atau tampilkan pesan kepada pengguna
                Toast.makeText(requireContext(), "Izin diperlukan untuk mengakses gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun loadImage(uri: Uri) {
        // Memuat gambar dari URI
        binding.imageView3.setImageURI(uri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}