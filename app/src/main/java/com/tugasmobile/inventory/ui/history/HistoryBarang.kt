package com.tugasmobile.inventory.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.adapter.AdapterHistoryBarang
import com.tugasmobile.inventory.data.History
import com.tugasmobile.inventory.databinding.FragmentHistoryBarangBinding
import com.tugasmobile.inventory.ui.ViewModel


class HistoryBarang : Fragment() {

    private val historyViewModel: ViewModel by viewModels()
    private lateinit var adapterHistory: AdapterHistoryBarang
    private var allHistoryItems: List<History> = listOf()
    private var _binding: FragmentHistoryBarangBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBarangBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView

        adapterHistory=AdapterHistoryBarang(emptyList())
        binding.recyclerHistory.adapter = adapterHistory
        binding.recyclerHistory.layoutManager = LinearLayoutManager(requireContext())

        // Observe data dari ViewModel
        historyViewModel.allHistory.observe(viewLifecycleOwner) { historyList ->
            Log.d("HistoryBarangFragment", "Data diterima dari ViewModel: ${historyList.size} item")
            historyList.forEach { history ->
                Log.d("HistoryBarangFragment", "History: $history")
            }
            adapterHistory.setItems(historyList) // Update data di adapter
        }

        // Muat data history
        historyViewModel.loadHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Bersihkan binding saat fragment dihancurkan
    }

}