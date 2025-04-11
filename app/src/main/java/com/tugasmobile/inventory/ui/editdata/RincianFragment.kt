package com.tugasmobile.inventory.ui.editdata

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterSizeColorUI
import com.tugasmobile.inventory.databinding.FragmentRincianBinding
import com.tugasmobile.inventory.ui.InventoryViewModelFactory
import com.tugasmobile.inventory.utils.AnimationHelper
import com.tugasmobile.inventory.utils.HargaUtils


class RincianFragment : Fragment() {
    private var _binding:FragmentRincianBinding?=null
    private val binding get() = _binding!!
    private var gambarUri: Uri? = null
    private val rincianViewModel:DetailViewModel  by viewModels {
        InventoryViewModelFactory.getInstance(requireActivity().application)
    }
    private var BarangId:String=""
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRincianBinding.inflate(inflater,container,false)
        AnimationHelper.animateItems(binding.linearLayoutRincian,requireContext())
        BarangId = arguments?.getString("ID_BARANG") ?: ""
        val colorNames = resources.getStringArray(R.array.daftar_nama_warna)
        val colorValues = resources.getStringArray(R.array.daftar_warna)
        val colorMap = colorNames.indices.associate { index ->
            colorNames[index] to colorValues[index]
        }
// Inisialisasi RecyclerView dan Adapter
        val recyclerView: RecyclerView = binding.rvUkuranWarnaSendal // Sesuaikan dengan ID RecyclerView Anda
        val colorAdapter = AdapterSizeColorUI(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = colorAdapter

        checkPermission()

        // Gunakan barangId untuk memuat detail barang
        rincianViewModel.setCurrentBarang(BarangId)
        rincianViewModel.currentBarang.observe(viewLifecycleOwner){ barang->
            barang?.let{
                binding.tvNamaBarang.text = it.merek_barang
                binding.tvKodeBarang.text = it.id_barang
                tampilkanKarakteristik(it.karakteristik)
                gambarUri = it.gambar.let { Uri.parse(it) }
                gambarUri?.let { uri ->
                    // Menyimpan URI untuk digunakan setelah izin diberikan
                    loadImage(uri)
            }
        }}
        rincianViewModel.currentStok.observe(viewLifecycleOwner) { stok ->
            stok?.let {
                val teksLengkap = it.ukuranwarna.map { item -> item.trim() }
                binding.tvStok.text = it.stokBarang.toString()
                val warnaFromDb = it.ukuranwarna.map { item ->
                    item.split(" ").last().trim() // Ambil bagian terakhir (warna)
                }
                val selectedColorValues = warnaFromDb.mapNotNull { colorMap[it] }
                Log.d("RincianFragment", "Data ukuranwarna dikirim ke adapter: $teksLengkap")
                Log.d("RincianFragment", "Data warna dikirim ke adapter: $selectedColorValues")

                if (teksLengkap.isNotEmpty() && selectedColorValues.isNotEmpty()) {
                    colorAdapter.updateColors(teksLengkap, selectedColorValues)
                } else {
                    // Handle jika data kosong
                    colorAdapter.updateColors(emptyList(), emptyList())
                    Toast.makeText(requireContext(), "Tidak ada data warna yang valid", Toast.LENGTH_SHORT).show()
                }
            }
        }
        rincianViewModel.currentBarangIn.observe(viewLifecycleOwner){barangIn->
            barangIn?.let{
                val formattedHarga = HargaUtils.formatHarga(it.Harga_Modal)
                binding.tvHarga.text = "Rp. $formattedHarga"
                binding.tvNamaToko.text = it.Nama_Toko
            }
        }
        return binding.root // Inflate the layout for this fragment
    }
    private fun tampilkanKarakteristik(karakteristikText: String) {
        val chipGroup = binding.tvrincinkarakteristik
        chipGroup.removeAllViews() // Bersihkan sebelum isi ulang

        val karakteristikList = karakteristikText.split(",").map { it.trim() }

        for (karakter in karakteristikList) {
            val chip = Chip(requireContext()).apply {
                text = karakter
                isClickable = false
                isCheckable = false
                chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
                )
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            }
            chipGroup.addView(chip)
        }
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
        Log.d("RincianFragment", "Memuat gambar dari URI: $uri")
        // Memuat gambar dari URI
        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.baseline_image_24) // Gambar default jika gagal
            .error(R.drawable.baseline_image_24) // Gambar jika error
            .into(binding.imageView3)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
        // Memuat ulang data saat fragment kembali ke tampilan
        rincianViewModel.setCurrentBarang(BarangId)
    }


}