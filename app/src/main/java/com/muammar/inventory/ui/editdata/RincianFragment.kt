package com.muammar.inventory.ui.editdata

import android.Manifest
import android.content.Intent
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
import com.muammar.inventory.R
import com.muammar.inventory.adapter.AdapterSizeColorUI
import com.muammar.inventory.databinding.FragmentRincianBinding
import com.muammar.inventory.ui.InventoryViewModelFactory
import com.muammar.inventory.ui.data.DataActivity
import com.muammar.inventory.ui.main.MainActivity
import com.muammar.inventory.utils.AnimationHelper
import com.muammar.inventory.utils.HargaUtils


class RincianFragment : Fragment() {
    private var _binding:FragmentRincianBinding?=null
    private val binding get() = _binding!!
    private var gambarUri: Uri? = null
    private var isFabOpen = false
    private var KodeBarang:String=""
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

        val recyclerView: RecyclerView = binding.rvUkuranWarnaSendal
        val colorAdapter = AdapterSizeColorUI(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = colorAdapter

        checkPermission()


        rincianViewModel.setCurrentBarang(BarangId)
        rincianViewModel.currentBarang.observe(viewLifecycleOwner){ barang->
            barang?.let{
                binding.tvNamaBarang.text = it.merekBarang
                binding.tvKodeBarang.text = it.idBarang
                KodeBarang=it.idBarang
                tampilkanKarakteristik(it.karakteristik)
                gambarUri = it.gambar.let { Uri.parse(it) }
                gambarUri?.let { uri ->

                    loadImage(uri)
            }
        }}
        rincianViewModel.currentStok.observe(viewLifecycleOwner) { stok ->
            stok?.let {
                val teksLengkap = it.ukuranwarna.split(",").map { it.trim() }
                binding.tvStok.text = it.stokBarang.toString()
                val warnaFromDb = teksLengkap.map { item ->
                    item.split(" ").last().trim()
                }
                val selectedColorValues = warnaFromDb.mapNotNull { colorMap[it] }
                Log.d("RincianFragment", "Data ukuranwarna dikirim ke adapter: $teksLengkap")
                Log.d("RincianFragment", "Data warna dikirim ke adapter: $selectedColorValues")

                if (teksLengkap.isNotEmpty() && selectedColorValues.isNotEmpty()) {
                    colorAdapter.updateColors(teksLengkap, selectedColorValues)
                } else {

                    colorAdapter.updateColors(emptyList(), emptyList())
                    Toast.makeText(requireContext(), "Tidak ada data warna yang valid", Toast.LENGTH_SHORT).show()
                }
            }
        }
        rincianViewModel.currentBarangMasukItem.observe(viewLifecycleOwner){ barangIn->
            barangIn?.let{
                val formattedHarga = HargaUtils.formatHarga(it.hargaModal)
                binding.tvHarga.text = "Rp. $formattedHarga"
                binding.tvNamaToko.text = it.namaToko
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabMain.setOnClickListener {
            animateFab()
        }

        binding.fabBarangMasuk.setOnClickListener {
            val intent = Intent(requireContext(), DataActivity::class.java)
            intent.putExtra("ID_BARANG", KodeBarang)
            startActivity(intent)

        }

        binding.fabBarangKeluar.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra("ID_BARANG", KodeBarang)
            startActivity(intent)

        }

    }

    private fun animateFab() {
        if (isFabOpen) {
            binding.fabBarangMasuk.animate().translationY(0f).alpha(0f).withEndAction {
                binding.fabBarangMasuk.visibility = View.GONE
            }
            binding.fabBarangKeluar.animate().translationY(0f).alpha(0f).withEndAction {
                binding.fabBarangKeluar.visibility = View.GONE
            }
            binding.fabMain.setImageResource(R.drawable.baseline_more_vert_24)
        } else {
            binding.fabBarangMasuk.visibility = View.VISIBLE
            binding.fabBarangKeluar.visibility = View.VISIBLE

            binding.fabBarangMasuk.alpha = 0f
            binding.fabBarangMasuk.translationY = 0f
            binding.fabBarangMasuk.animate().translationY(-120f).alpha(1f)

            binding.fabBarangKeluar.alpha = 0f
            binding.fabBarangKeluar.translationY = 0f
            binding.fabBarangKeluar.animate().translationY(-240f).alpha(1f)

            binding.fabMain.setImageResource(R.drawable.baseline_close_24)
        }
        isFabOpen = !isFabOpen
    }

    private fun tampilkanKarakteristik(karakteristikText: String) {
        val chipGroup = binding.tvrincinkarakteristik
        chipGroup.removeAllViews()

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

                rincianViewModel.currentDataBarangMasuk.value?.gambar?.let { gambar ->
                    loadImage(Uri.parse(gambar))
                }
            } else {

                Toast.makeText(requireContext(), "Izin diperlukan untuk mengakses gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun loadImage(uri: Uri) {
        Log.d("RincianFragment", "Memuat gambar dari URI: $uri")

        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.baseline_image_24)
            .error(R.drawable.baseline_image_24)
            .into(binding.imageView3)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()

        rincianViewModel.setCurrentBarang(BarangId)
    }


}