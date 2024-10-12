package com.tugasmobile.inventory.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.data.Laporan
import com.tugasmobile.inventory.data.LaporanDatabaseHelper
import com.tugasmobile.inventory.databinding.FragmentGalleryBinding
import java.util.ArrayList

class BarangFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var rvBarang:RecyclerView
    private lateinit var barangAdapter: BarangAdapter
    private lateinit var barangViewModel: BarangViewModel
    private lateinit var laporanDatabaseHelper: LaporanDatabaseHelper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        laporanDatabaseHelper=LaporanDatabaseHelper(requireContext())
        barangAdapter=BarangAdapter(ArrayList())
        val factory = BarangViewModelFactory(laporanDatabaseHelper)
        barangViewModel = ViewModelProvider(this, factory).get(BarangViewModel::class.java)

        rvBarang = binding.recyclerViewLaporan
        rvBarang.setHasFixedSize(true)
        rvBarang.layoutManager = LinearLayoutManager(context)
        rvBarang.adapter=barangAdapter
        barangViewModel.listLaporan.observe(viewLifecycleOwner, Observer { laporanList ->
            showRecyclerList(laporanList)
        })
        barangViewModel.loadLaporan()
        barangViewModel.listLaporan.observe(viewLifecycleOwner, Observer { laporanList ->
            Log.d("BarangFragment", "Observer triggered with: $laporanList") // Tambahkan log
            showRecyclerList(laporanList)
        })
        return root
    }


    private fun showRecyclerList(laporanList:List<Laporan>) {
        rvBarang.layoutManager = LinearLayoutManager(context)

        rvBarang.adapter = barangAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}