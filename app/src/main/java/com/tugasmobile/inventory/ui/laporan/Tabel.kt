package com.tugasmobile.inventory.ui.laporan
import com.tugasmobile.inventory.adapter.LaporanAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.databinding.FragmentLaporanBinding
import com.tugasmobile.inventory.ui.ViewModel

class Tabel : Fragment() {

    private var _binding: FragmentLaporanBinding? = null
    private val binding get() = _binding!!

    private lateinit var laporanAdapter: LaporanAdapter
    private lateinit var laporanViewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        _binding = FragmentLaporanBinding.inflate(inflater, container, false)

        // Inisialisasi ViewModel
        laporanViewModel = ViewModelProvider(this).get(ViewModel::class.java)

        // Inisialisasi RecyclerView dan Adapter
        binding.recyclerViewLaporan.layoutManager = LinearLayoutManager(requireContext())
        laporanAdapter = LaporanAdapter(emptyList()) // Mulai dengan list kosong
        binding.recyclerViewLaporan.adapter = laporanAdapter

        // Observasi LiveData dari ViewModel untuk memperbarui UI ketika data berubah
        laporanViewModel.barangList.observe(viewLifecycleOwner) { laporanList ->
            laporanAdapter.updateLaporanList(laporanList)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
