package com.tugasmobile.inventory.ui.main.home

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterBarangHampirHabisHome
import com.tugasmobile.inventory.adapter.AdapterHistoryBarang
import com.tugasmobile.inventory.databinding.FragmentHomeBinding
import com.tugasmobile.inventory.ui.InventoryViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var AdapterHampirHabis: AdapterBarangHampirHabisHome
    private lateinit var adapterHistory: AdapterHistoryBarang
    private val homeViewModel: HomeViewModel by viewModels {
        InventoryViewModelFactory.getInstance(requireActivity().application)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
        updateTanggalWaktu()
        homeViewModel.dataTertinggi.observe(viewLifecycleOwner) { hasil ->
            binding.stokTertinggi.text = hasil
        }
        homeViewModel.getDataTertinggi()
        homeViewModel.totalBarang.observe(viewLifecycleOwner) { total ->
            binding.textTotalBarangValue.text = total.toString()
        }
        homeViewModel.getTotalBarang()
        homeViewModel.stokRendah.observe(viewLifecycleOwner) { total ->
            binding.textStokRendahValue.text = total.toString()
        }
        homeViewModel.getStokRendah()
        AdapterHampirHabis = AdapterBarangHampirHabisHome(emptyList()) // Awal kosong
        binding.rvBarangHampirHabis.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvBarangHampirHabis.adapter = AdapterHampirHabis

        homeViewModel.barangHampirHabis.observe(viewLifecycleOwner) {
            AdapterHampirHabis.updateData(it)
        }

        homeViewModel.loadBarangHampirHabis()
        adapterHistory = AdapterHistoryBarang(emptyList())
        binding.recyclerViewAktivitas.layoutManager= LinearLayoutManager(requireContext())
        binding.recyclerViewAktivitas.adapter = adapterHistory

        homeViewModel.loadLastThreeHistory() // Ini penting

        homeViewModel.historyTerbaru.observe(viewLifecycleOwner) { historyList ->
            if (historyList.isNotEmpty()) {
                adapterHistory.setItems(historyList)
                binding.recyclerViewAktivitas.visibility = View.VISIBLE
                binding.tvKosongAktivitas.visibility = View.GONE
            } else {
                binding.recyclerViewAktivitas.visibility = View.GONE
                binding.tvKosongAktivitas.visibility = View.VISIBLE
            }
        }

    }
    private fun updateTanggalWaktu() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            while (isActive) {
                val calendar = Calendar.getInstance()

                val waktuFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val waktuSekarang = waktuFormat.format(calendar.time)

                val hariFormat = SimpleDateFormat("EEEE", Locale("id"))
                val hariSekarang = hariFormat.format(calendar.time)

                val tanggalFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id"))
                val tanggalSekarang = tanggalFormat.format(calendar.time)

                binding.apply {
                    tvWaktu.text = waktuSekarang
                    tvHari.text = hariSekarang
                    tvTanggal.text = tanggalSekarang
                }

                delay(60000) // update setiap 1 menit
            }
        }
    }



    override fun onResume() {
        super.onResume()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}