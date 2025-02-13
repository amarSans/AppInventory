package com.tugasmobile.inventory.ui.Barang

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.adapter.BarangAdapter
import com.tugasmobile.inventory.databinding.FragmentBarangMasukBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.ui.editdata.DetailBarang
import com.tugasmobile.inventory.ui.uiData.addData

class BarangMasuk : Fragment() {

    private var _binding: FragmentBarangMasukBinding? = null
    private val binding get() = _binding!!
    private lateinit var barangAdapter: BarangAdapter
    private lateinit var barangViewModel: ViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBarangMasukBinding.inflate(inflater, container, false)

        // Inisialisasi ViewModel
        barangViewModel = ViewModelProvider(this).get(ViewModel::class.java)

        // Inisialisasi RecyclerView dan Adapter
        binding.recyclerViewLaporan.layoutManager = LinearLayoutManager(requireContext())
        barangAdapter = BarangAdapter(emptyList()) { barang ->
            val intent = Intent(requireActivity(), DetailBarang::class.java).apply {
                putExtra("NAMA_BARANG", barang.namaBarang)
                putExtra("STOK_BARANG", barang.stok)
                putExtra("HARGA_BARANG", barang.harga)
                putExtra("ID_BARANG", barang.id)
            }
            startActivity(intent)  // Mulai Activity dengan data
        }
        binding.btnTamBar.setOnClickListener {
            val intent = Intent(requireContext(), addData::class.java)
            startActivity(intent)
        }
        binding.recyclerViewLaporan.adapter = barangAdapter
        binding.recyclerViewLaporan.layoutManager = LinearLayoutManager(requireContext())// 2 kolom
        barangViewModel.barangPrototypeList.observe(viewLifecycleOwner) { listBarang ->
            barangAdapter.updateLaporanList(listBarang)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        barangViewModel.loadLaporan()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}