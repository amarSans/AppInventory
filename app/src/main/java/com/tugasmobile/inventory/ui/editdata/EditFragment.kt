package com.tugasmobile.inventory.ui.editdata

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tugasmobile.inventory.data.Barang
import com.tugasmobile.inventory.databinding.FragmentEditBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.ui.uiData.addData


class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var editViewModel: ViewModel
    private var barangId:Long=0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         editViewModel = ViewModelProvider(this).get(ViewModel::class.java)

        _binding = FragmentEditBinding.inflate(inflater, container, false)
        val root: View = binding.root
        barangId=arguments?.getLong("ID_BARANG",0)?:0L
        if (barangId != 0L) {
            editViewModel.setCurrentBarang(barangId) // Memastikan ViewModel memuat barang yang benar
        }
        editViewModel.currentBarang.observe(viewLifecycleOwner) { barang ->
            barang?.let {
                binding.inputNamaProduk.setText(it.namaProduk)
                binding.inputStok.setText(it.stok.toString())
                binding.inputHarga.setText(it.harga.toString())
            }
        }

        binding.btnSave.setOnClickListener {
            val updatedBarang = editViewModel.currentBarang.value?.copy(
                namaProduk = binding.inputNamaProduk.text.toString(),
                stok = binding.inputStok.text.toString().toInt(),
                harga = binding.inputHarga.text.toString().toDouble()
            )
            updatedBarang?.let {
                editViewModel.updateLaporan(it)
                activity?.supportFragmentManager?.popBackStack()
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}