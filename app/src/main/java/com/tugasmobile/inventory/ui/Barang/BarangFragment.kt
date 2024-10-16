package com.tugasmobile.inventory.ui.Barang

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.databinding.FragmentBarangBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.ui.editdata.DetailBarang

class BarangFragment : Fragment() {

    private var _binding: FragmentBarangBinding? = null
    private val binding get() = _binding!!
    private lateinit var barangAdapter: BarangAdapter
    private lateinit var barangViewModel: ViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBarangBinding.inflate(inflater, container, false)

        // Inisialisasi ViewModel
        barangViewModel = ViewModelProvider(this).get(ViewModel::class.java)

        // Inisialisasi RecyclerView dan Adapter
        binding.recyclerViewLaporan.layoutManager = LinearLayoutManager(requireContext())
        barangAdapter = BarangAdapter(emptyList()) { barang ->
            val intent = Intent(requireActivity(), DetailBarang::class.java).apply {
                putExtra("NAMA_BARANG", barang.namaProduk)
                putExtra("STOK_BARANG", barang.stok)
                putExtra("HARGA_BARANG", barang.harga)
            }
            startActivity(intent)  // Mulai Activity dengan data
        }
        binding.recyclerViewLaporan.adapter = barangAdapter
        binding.recyclerViewLaporan.layoutManager = GridLayoutManager(requireContext(), 2) // 2 kolom
        // Observasi LiveData dari ViewModel untuk memperbarui UI ketika data berubah
        barangViewModel.barangList.observe(viewLifecycleOwner) { listBarang ->
            barangAdapter.updateLaporanList(listBarang)
        }
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}