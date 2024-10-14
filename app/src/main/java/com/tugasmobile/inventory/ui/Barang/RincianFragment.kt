package com.tugasmobile.inventory.ui.Barang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.databinding.FragmentBarangBinding
import com.tugasmobile.inventory.databinding.FragmentRincianBinding
import com.tugasmobile.inventory.ui.ViewModel


class RincianFragment : Fragment() {

    private var _binding: FragmentRincianBinding? = null
    private val binding get() = _binding!!
    private lateinit var barangAdapter: BarangAdapter
    private lateinit var barangViewModel: ViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRincianBinding.inflate(inflater, container, false)

        // Inisialisasi ViewModel
        barangViewModel = ViewModelProvider(this).get(ViewModel::class.java)

        val namaBarang = arguments?.getString("namaBarang")?:"barang tidak ada"
        val stokBarang = arguments?.getInt("stokBarang")?:0
        val hargaBarang = arguments?.getDouble("hargaBarang")?:0.0

        // Tampilkan data di UI
        binding.NamaBarang.text = namaBarang
        binding.StokBarang.text = stokBarang.toString()
        binding.HargaBarang.text = hargaBarang.toString()
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