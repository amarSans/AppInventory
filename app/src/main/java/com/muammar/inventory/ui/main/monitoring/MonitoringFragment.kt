package com.muammar.inventory.ui.main.monitoring

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.muammar.inventory.R
import com.muammar.inventory.databinding.FragmentMonitoringBinding
import com.muammar.inventory.ui.InventoryViewModelFactory
import com.muammar.inventory.ui.setting.SettingActivity


class MonitoringFragment : Fragment() {
    private var _binding: FragmentMonitoringBinding? = null
    private val binding get() = _binding!!
    private val monitoringViewModel: MonitoringViewModel by viewModels {
        InventoryViewModelFactory.getInstance(requireActivity().application)
    }
    private lateinit var adapter : AdapterMonitoring
    private lateinit var filterOptions: Array<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = AdapterMonitoring (requireContext(), emptyList())
        requireActivity().addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_main,menu)
                menu.findItem(R.id.action_filter)?.isVisible=false
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings->{
                        val intent = Intent(context, SettingActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }

        },viewLifecycleOwner, Lifecycle.State.RESUMED)
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
        monitoringViewModel.loadBarangTertingal()
    }
    private fun setupSpinner() {
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilter.adapter = spinnerAdapter
    }

}