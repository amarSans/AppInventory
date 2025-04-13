package com.tugasmobile.inventory.ui.main.barang

import android.content.Intent
import android.os.Bundle
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
import com.tugasmobile.inventory.adapter.AdapterBarangMasuk
import com.tugasmobile.inventory.databinding.FragmentBarangMasukBinding
import com.tugasmobile.inventory.ui.InventoryViewModelFactory
import com.tugasmobile.inventory.ui.data.DataActivity
import com.tugasmobile.inventory.ui.editdata.DetailBarang
import com.tugasmobile.inventory.utils.AnimationHelper
import com.tugasmobile.inventory.utils.PerformClickUtils

class BarangMasuk : Fragment() {

    private var _binding: FragmentBarangMasukBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterDaftarBarang: AdapterBarangMasuk
    private val barangViewModel: BarangViewModel by viewModels {
        InventoryViewModelFactory.getInstance(requireActivity().application)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBarangMasukBinding.inflate(inflater, container, false)


        AnimationHelper.animateItems(binding.fragmentBarangMasuk,requireContext())


        binding.recyclerViewLaporan.layoutManager = LinearLayoutManager(requireContext())
        adapterDaftarBarang = AdapterBarangMasuk(requireContext(),emptyList())
        binding.btnTamBar.setOnClickListener {
            PerformClickUtils.preventMultipleClick {
                val intent = Intent(requireContext(), DataActivity::class.java)
                startActivity(intent)
            }

        }
        binding.recyclerViewLaporan.adapter = adapterDaftarBarang
        binding.recyclerViewLaporan.layoutManager = LinearLayoutManager(requireContext())
        barangViewModel.dataBarangAksesList.observe(viewLifecycleOwner) { listBarang ->
            adapterDaftarBarang.updateLaporanList(listBarang.reversed())
        }
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