package com.tugasmobile.inventory.ui.main.home

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterBarangHampirHabisHome
import com.tugasmobile.inventory.adapter.AdapterHistoryBarang
import com.tugasmobile.inventory.databinding.FragmentHomeBinding
import com.tugasmobile.inventory.ui.InventoryViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.tugasmobile.inventory.ui.search.SearchActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels {
        InventoryViewModelFactory.getInstance(requireActivity().application)
    }

    private lateinit var adapterHampirHabis: AdapterBarangHampirHabisHome
    private lateinit var adapterHistory: AdapterHistoryBarang

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        setupUI()
        observeData()
        updateTanggalWaktu()
        binding.searchContainer.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                v.performClick() // <- ini penting untuk aksesibilitas
                if (binding.editTextSearchHome.isFocused) {
                    binding.editTextSearchHome.clearFocus()
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.editTextSearchHome.windowToken, 0)
                }
            }
            false
        }
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_main, menu)
                menu.findItem(R.id.action_filter)?.isVisible = false
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return menuItem.itemId == R.id.action_settings
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupUI() {
        adapterHampirHabis = AdapterBarangHampirHabisHome(emptyList())
        binding.rvBarangHampirHabis.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterHampirHabis
        }

        adapterHistory = AdapterHistoryBarang(emptyList())
        binding.recyclerViewAktivitas.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterHistory
        }

        binding.menuBarangMasuk.setOnClickListener {
            navigateTo(R.id.action_nav_home_to_nav_barang_masuk)
        }
        binding.menuBarangKeluar.setOnClickListener {
            navigateTo(R.id.action_nav_home_to_nav_barang_keluar)
        }
        binding.menuRiwayat.setOnClickListener {
            navigateTo(R.id.action_nav_home_to_nav_history_barang)
        }
        binding.menuDaftarBarang.setOnClickListener {
            navigateTo(R.id.action_nav_home_to_nav_daftar_barang)
        }
    }

    private fun observeData() {
        homeViewModel.totalBarang.observe(viewLifecycleOwner) { total ->
            binding.textTotalBarangValue.text = total.toString()
        }

        homeViewModel.stokRendah.observe(viewLifecycleOwner) { total ->
            binding.textStokRendahValue.text = total.toString()
        }

        homeViewModel.dataTertinggi.observe(viewLifecycleOwner) { stok ->
            binding.stokTertinggi.text = if (!stok.isNullOrEmpty()) {
                "Stok sandal tertinggi: $stok"
            } else {
                "Belum ada data stok tersedia"
            }
        }

        homeViewModel.barangHampirHabis.observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                adapterHampirHabis.updateData(list)
                binding.rvBarangHampirHabis.visibility = View.VISIBLE
                binding.tvKosongBarangHabis.visibility = View.GONE
            } else {
                binding.rvBarangHampirHabis.visibility = View.GONE
                binding.tvKosongBarangHabis.visibility = View.VISIBLE
            }
        }

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

        homeViewModel.getTotalBarang()
        homeViewModel.getStokRendah()
        homeViewModel.getDataTertinggi()
        homeViewModel.loadBarangHampirHabis()
        homeViewModel.loadLastThreeHistory()
    }

    private fun updateTanggalWaktu() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            while (isActive) {
                val calendar = Calendar.getInstance()

                binding.tvWaktu.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
                binding.tvHari.text = SimpleDateFormat("EEEE", Locale("id")).format(calendar.time)
                binding.tvTanggal.text = SimpleDateFormat("dd MMMM yyyy", Locale("id")).format(calendar.time)

                delay(60000)
            }
        }
    }

    private fun navigateTo(destinationId: Int) {
        findNavController().navigate(destinationId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
