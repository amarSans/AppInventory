package com.tugasmobile.inventory.ui.daftarBarang

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.MainActivity
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterDaftarBarang
import com.tugasmobile.inventory.databinding.FragmentDaftarBarangBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.ui.editdata.DetailBarang
import com.tugasmobile.inventory.ui.editdata.RincianFragment


class DaftarBarang : Fragment() {
    private var _binding: FragmentDaftarBarangBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterDaftarBarang: AdapterDaftarBarang
    private lateinit var barangViewModel: ViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDaftarBarangBinding.inflate(inflater, container, false)

        // Inisialisasi ViewModel
        barangViewModel = ViewModelProvider(this).get(ViewModel::class.java)

        // Inisialisasi RecyclerView dan Adapter
        binding.recyclerViewLaporan.layoutManager = LinearLayoutManager(requireContext())
        adapterDaftarBarang = AdapterDaftarBarang(emptyList()) { barang ->
            val intent = Intent(requireActivity(), DetailBarang::class.java).apply {
                putExtra("NAMA_BARANG", barang.namaBarang)
                putExtra("STOK_BARANG", barang.stok)
                putExtra("HARGA_BARANG", barang.harga)
                putExtra("ID_BARANG", barang.id)
            }
            startActivity(intent)  // Mulai Activity dengan data
        }

        binding.recyclerViewLaporan.adapter = adapterDaftarBarang
        binding.recyclerViewLaporan.layoutManager =
            GridLayoutManager(requireContext(), 2) // 2 kolom
        barangViewModel.dataBarangAksesList.observe(viewLifecycleOwner) { listBarang ->
            adapterDaftarBarang.updateLaporanList(listBarang)
        }
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_main, menu)// Tambahkan menu hanya di sini
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_filter -> {
                        showFilterMenu(requireActivity().findViewById(R.id.action_filter))
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }




    private fun showFilterMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.menu_filter, popup.menu)

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

    override fun onResume() {
        super.onResume()
        barangViewModel.loadBarang()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}