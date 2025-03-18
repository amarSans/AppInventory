package com.tugasmobile.inventory.ui.Barang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.adapter.AdapterBarangMasuk
import com.tugasmobile.inventory.adapter.AdapterDaftarBarang
import com.tugasmobile.inventory.databinding.FragmentBarangMasukBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.ui.editdata.DetailBarang
import com.tugasmobile.inventory.ui.uiData.addData
import java.text.SimpleDateFormat
import java.util.Locale

class BarangMasuk : Fragment() {

    private var _binding: FragmentBarangMasukBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterDaftarBarang: AdapterBarangMasuk
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
        adapterDaftarBarang = AdapterBarangMasuk(emptyList()) { barang ->
            val intent = Intent(requireActivity(), DetailBarang::class.java).apply {
                putExtra("NAMA_BARANG", barang.namaBarang)
                putExtra("KODE_BARANG", barang.id)
                putExtra("TIME_BARANG", barang.harga)
                putExtra("ID_BARANG", barang.id)
            }
            startActivity(intent)  // Mulai Activity dengan data
        }
        binding.btnTamBar.setOnClickListener {
            val intent = Intent(requireContext(), addData::class.java)
            startActivity(intent)
        }
        binding.recyclerViewLaporan.adapter = adapterDaftarBarang
        binding.recyclerViewLaporan.layoutManager = LinearLayoutManager(requireContext())// 2 kolom
        barangViewModel.dataBarangAksesList.observe(viewLifecycleOwner) { listBarang ->
            Log.d("BarangMasuk", "Data diterima: ${listBarang.size} item")
            val sortedList = listBarang.sortedByDescending {
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                format.parse(it.waktu)?.time ?: 0
            }
            adapterDaftarBarang.updateLaporanList(sortedList)
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toastMessage = arguments?.getString("toastMessage")
        if (!toastMessage.isNullOrEmpty()) {
            Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onResume() {
        super.onResume()
        barangViewModel.loadBarang()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}