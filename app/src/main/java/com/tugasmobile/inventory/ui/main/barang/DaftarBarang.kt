package com.tugasmobile.inventory.ui.main.barang

import android.content.Intent
import android.os.Bundle
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
import androidx.recyclerview.widget.GridLayoutManager
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterDaftarBarang
import com.tugasmobile.inventory.data.DataBarangAkses
import com.tugasmobile.inventory.databinding.FragmentDaftarBarangBinding
import com.tugasmobile.inventory.ui.InventoryViewModelFactory
import com.tugasmobile.inventory.ui.editdata.DetailBarang


class DaftarBarang : Fragment() {
    private var _binding: FragmentDaftarBarangBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterDaftarBarang: AdapterDaftarBarang
    private val barangViewModel: BarangViewModel by viewModels {
        InventoryViewModelFactory.getInstance(requireActivity().application)
    }
    private var filterStock: String? = null
    private var query: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDaftarBarangBinding.inflate(inflater, container, false)

        handleIncomingData()

        setupRecyclerView()
        barangViewModel.dataBarangAksesList.observe(viewLifecycleOwner) { listBarang ->

            if (filterStock != null) {
                applyStockFilter(listBarang)
            } else {

                adapterDaftarBarang.updateLaporanList(listBarang)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barangViewModel.dataBarangAksesList.observe(viewLifecycleOwner) { allData ->

            val filteredData = if (!query.isNullOrEmpty()) {
                allData.filter { barang ->
                    barang.id.contains(query!!, ignoreCase = true) ||
                            barang.karakteristik.contains(query!!, ignoreCase = true) ||
                            barang.nama_toko.contains(query!!, ignoreCase = true)
                }
            } else {
                allData
            }

            if (filteredData.isEmpty()) {
                emptydata()
            } else {
                binding.lottieEmpty.visibility = View.GONE
                binding.recyclerViewLaporan.visibility = View.VISIBLE
                adapterDaftarBarang.updateLaporanList(filteredData)
            }
        }
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_filter -> {
                        if(filterStock!=null){
                            barangViewModel.dataBarangAksesList.observe(viewLifecycleOwner) { listBarang ->
                                adapterDaftarBarang.updateLaporanList(listBarang)
                            }
                            filterStock=null
                            arguments?.remove("filter_stock")
                        }

                        showFilterMenu(requireActivity().findViewById(R.id.action_filter))
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
    private fun handleIncomingData() {
        val args = arguments
        filterStock = args?.getString("filter_stock")
        query = args?.getString("QUERY_KEY")
        if (filterStock != null||query!=null) {
            arguments?.remove("QUERY_KEY")
            arguments?.remove("filter_stock")
        }
    }

    private fun setupRecyclerView() {
        adapterDaftarBarang = AdapterDaftarBarang(emptyList()) { barang ->
            val intent = Intent(requireActivity(), DetailBarang::class.java).apply {
                putExtra("NAMA_BARANG", barang.namaBarang)
                putExtra("STOK_BARANG", barang.stok)
                putExtra("HARGA_BARANG", barang.harga)
                putExtra("ID_BARANG", barang.id)
            }
            startActivity(intent)
        }
        binding.recyclerViewLaporan.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = adapterDaftarBarang
        }
    }
    private fun emptydata(){
        binding.lottieEmpty.visibility=View.VISIBLE
        binding.recyclerViewLaporan.visibility=View.GONE
    }


    private fun showFilterMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.menu_filter_daftarbarang, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.filter_stok_asc -> adapterDaftarBarang.sortByStok(ascending = true)
                R.id.filter_stok_desc -> adapterDaftarBarang.sortByStok(ascending = false)
                R.id.filter_harga_asc -> adapterDaftarBarang.sortByHarga(ascending = true)
                R.id.filter_harga_desc -> adapterDaftarBarang.sortByHarga(ascending = false)
                R.id.filter_nama_az -> adapterDaftarBarang.sortByNama(ascending = true)
                R.id.filter_nama_za -> adapterDaftarBarang.sortByNama(ascending = false)
            }

            true
        }
        popup.show()
    }

    private fun applyStockFilter(listBarang: List<DataBarangAkses>) {
        val filteredList = listBarang.filter { it.stok <= 2 }
        if (filteredList.isEmpty()) {
            emptydata()
        } else {
            binding.lottieEmpty.visibility = View.GONE
            binding.recyclerViewLaporan.visibility = View.VISIBLE
            adapterDaftarBarang.updateLaporanList(filteredList)
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