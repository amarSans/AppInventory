package com.tugasmobile.inventory.ui.editdata

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.databinding.FragmentRincianBinding
import com.tugasmobile.inventory.ui.ViewModel


class RincianFragment : Fragment() {
    private var _binding:FragmentRincianBinding?=null
    private val binding get() = _binding!!
    private lateinit var rincianViewModel: ViewModel
    private var BarangId:Long=0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rincianViewModel = ViewModelProvider(this).get(ViewModel::class.java)
        _binding = FragmentRincianBinding.inflate(inflater,container,false)
        val root:View=binding.root
        BarangId = arguments?.getLong("ID_BARANG") ?: 0L

        // Gunakan barangId untuk memuat detail barang
        rincianViewModel.setCurrentBarang(BarangId)
        rincianViewModel.currentBarang.observe(viewLifecycleOwner){barang->
            barang?.let{
                binding.NamaBarang.text = it.namaProduk
                binding.StokBarang.text = it.stok.toString()
                binding.HargaBarang.text = it.harga.toString()
            }
        }


        return root  // Inflate the layout for this fragment
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}