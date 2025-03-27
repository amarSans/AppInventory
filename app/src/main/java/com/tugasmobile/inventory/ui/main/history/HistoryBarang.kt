package com.tugasmobile.inventory.ui.main.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.R
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
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_main, menu)
                menu.findItem(R.id.action_filter)?.isVisible = false
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> {

                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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