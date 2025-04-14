package com.muammar.inventory.ui.main.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.muammar.inventory.R
import com.muammar.inventory.adapter.AdapterHistoryBarang
import com.muammar.inventory.databinding.FragmentHistoryBarangBinding
import com.muammar.inventory.ui.InventoryViewModelFactory


class HistoryBarang : Fragment() {

    private val historyViewModel: HistoryViewModel by viewModels {
        InventoryViewModelFactory.getInstance(requireActivity().application)
    }
    private lateinit var adapterHistory: AdapterHistoryBarang
    private var _binding: FragmentHistoryBarangBinding? = null
    private var selectedJenisData: String? = null
    private val binding get() = _binding!!
    private lateinit var imgNoData: View
    private lateinit var recyclerViewHistory: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBarangBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imgNoData = view.findViewById(R.id.lottie_empty)
        recyclerViewHistory = view.findViewById(R.id.recycler_history)

        applyFilter()
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_filter -> {
                        showFilterMenu(requireActivity().findViewById(R.id.action_filter))
                        true
                    }
                    R.id.action_settings -> {
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        adapterHistory=AdapterHistoryBarang(emptyList())
        binding.recyclerHistory.adapter = adapterHistory
        binding.recyclerHistory.layoutManager = LinearLayoutManager(requireContext())


        historyViewModel.allHistory.observe(viewLifecycleOwner) { historyList ->
            Log.d("HistoryBarangFragment", "Data diterima dari ViewModel: ${historyList.size} item")
            historyList.forEach { history ->
                Log.d("HistoryBarangFragment", "History: $history")
            }
            adapterHistory.setItems(historyList.reversed())
        }


        historyViewModel.loadHistory()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun applyFilter() {
        historyViewModel.allHistory.observe(viewLifecycleOwner) { historyList ->
            val filteredList = if (selectedJenisData != null) {

                historyList.filter { it.jenisData == selectedJenisData }
            } else {

                historyList
            }


            adapterHistory.setItems(filteredList.reversed())
            if (filteredList.isEmpty()) {
                imgNoData.visibility = View.VISIBLE
                recyclerViewHistory.visibility = View.GONE
            } else {
                imgNoData.visibility = View.GONE
                recyclerViewHistory.visibility = View.VISIBLE
            }
        }
    }

    private fun showFilterMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_filter_history, popupMenu.menu)


        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_filter_barangmasuk -> {
                    selectedJenisData = "barangmasuk"
                    applyFilter()
                    true
                }
                R.id.action_filter_stokmasuk -> {
                    selectedJenisData = "stokmasuk"
                    applyFilter()
                    true
                }
                R.id.action_filter_stokkeluar -> {
                    selectedJenisData = "stokkeluar"
                    applyFilter()
                    true
                }
                R.id.action_filter_barangdihapus -> {
                    selectedJenisData = "barangdihapus"
                    applyFilter()
                    true
                }
                R.id.action_filter_reset -> {

                    selectedJenisData = null
                    applyFilter()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }


}