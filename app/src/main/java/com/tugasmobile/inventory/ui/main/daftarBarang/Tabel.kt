package com.tugasmobile.inventory.ui.main.daftarBarang
import com.tugasmobile.inventory.adapter.LaporanAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.R
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
    ): View {
        // Inflate the layout using ViewBinding
        _binding = FragmentLaporanBinding.inflate(inflater, container, false)

        // Inisialisasi ViewModel
        laporanViewModel = ViewModelProvider(this).get(ViewModel::class.java)

        // Inisialisasi RecyclerView dan Adapter
        binding.recyclerViewLaporan.layoutManager = LinearLayoutManager(requireContext())
        laporanAdapter = LaporanAdapter(emptyList()) // Mulai dengan list kosong
        binding.recyclerViewLaporan.adapter = laporanAdapter

        // Observasi LiveData dari ViewModel untuk memperbarui UI ketika data berubah
        laporanViewModel.dataBarangAksesList.observe(viewLifecycleOwner) { laporanList ->
            laporanAdapter.updateLaporanList(laporanList)
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
