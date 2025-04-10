package com.tugasmobile.inventory.ui.main.monitoring

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.databinding.FragmentMonitoringBinding
import com.tugasmobile.inventory.ui.InventoryViewModelFactory
import com.tugasmobile.inventory.ui.editdata.DetailBarang


class MonitoringFragment : Fragment() {
    private var _binding: FragmentMonitoringBinding? = null
    private val binding get() = _binding!!
    private val monitoringViewModel: MonitoringViewModel by viewModels {
        InventoryViewModelFactory.getInstance(requireActivity().application)
    }
    private lateinit var adapter : MonitoringAdapter
    private lateinit var filterOptions: Array<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = MonitoringAdapter { barang ->
            // Aksi jika item diklik (misalnya buka Detail)
            val intent = Intent(requireContext(), DetailBarang::class.java).apply {
                putExtra("ID_BARANG", barang.kodeBarang)
            }
            startActivity(intent)
        }
        binding.rvMonitoring.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMonitoring.adapter = adapter
        filterOptions = resources.getStringArray(R.array.filterOption)

        setupSpinner()
        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                monitoringViewModel.setFilter(filterOptions[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        monitoringViewModel.filteredList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.tvKosong.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        monitoringViewModel.loadBarangTertingal() // Ambil data awal
    }
    private fun setupSpinner() {
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilter.adapter = spinnerAdapter
    }

}