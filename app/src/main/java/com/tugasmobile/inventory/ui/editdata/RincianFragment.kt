package com.tugasmobile.inventory.ui.editdata

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private lateinit var rincianViewModel: ViewModel
    private var BarangId:Long=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        // Gunakan barangId untuk memuat detail barang
        rincianViewModel.setCurrentBarang(BarangId)
        rincianViewModel.currentBarang.observe(viewLifecycleOwner){barang->
            barang?.let{
                binding.tvNamaBarang.text = it.namaBarang
                binding.tvKodeBarang.text = it.kodeBarang
                binding.tvHarga.text = it.harga.toString()
                binding.tvStok.text = it.stok.toString()
                binding.tvKategori.text = it.kategori
                val warnaFromDb = it.warna.map { warna -> warna.trim() }
                val selectedColorValues = warnaFromDb.mapNotNull { colorMap[it] }
                colorAdapter.updateColors(warnaFromDb, selectedColorValues)
                binding.TVUkuran.text=it.ukuran
            }
        }
        return root  // Inflate the layout for this fragment
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}