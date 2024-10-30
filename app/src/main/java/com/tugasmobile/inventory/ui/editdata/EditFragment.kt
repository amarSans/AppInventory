package com.tugasmobile.inventory.ui.editdata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.databinding.FragmentEditBinding
import com.tugasmobile.inventory.ui.ViewModel

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var editViewModel: ViewModel
    private var barangId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        editViewModel = ViewModelProvider(this).get(ViewModel::class.java)

        _binding = FragmentEditBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Mendapatkan ID barang dari argument dan mengatur data yang benar pada ViewModel
        barangId = arguments?.getLong("ID_BARANG", 0) ?: 0L
        if (barangId != 0L) {
            editViewModel.setCurrentBarang(barangId)
        }

        // Mengisi field dengan data barang yang diambil dari ViewModel
        editViewModel.currentBarang.observe(viewLifecycleOwner) { barang ->
            barang?.let {
                binding.editTextNamaBarang.setText(it.namaBarang)
                binding.editStokBarang.setText(it.stok.toString())
                binding.editTextHargaBarang.setText(it.harga.toString())
                val categories = resources.getStringArray(R.array.daftar_pilihan)
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.SpinnerKategori.adapter = adapter
                //setCheckBoxState(it.warna)
                /*val selectedPosition = categories.indexOf(it.kategori)
                binding.SpinnerKategori.setSelection(if (selectedPosition >= 0) selectedPosition else 0)

                binding.edtUkuran.setText(it.ukuran)*/
            }

        }
        /*binding.SpinnerKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = binding.SpinnerKategori.selectedItem.toString()
                editViewModel.currentBarang.value?.kategori = selectedCategory
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }*/

        // Menyimpan perubahan ketika tombol save ditekan
        binding.buttonSave.setOnClickListener {
            val updatedBarang = editViewModel.currentBarang.value?.copy(
                namaBarang = binding.editTextNamaBarang.text.toString(),
                stok = binding.editStokBarang.text.toString().toInt(),
                harga = binding.editTextHargaBarang.text.toString().toInt(),
                kodeBarang = binding.editTextKodeBarang.text.toString(),
                //warna = getSelectedColors(),
               /* kategori = binding.SpinnerKategori.selectedItem.toString(), // Use the selected item

                ukuran = binding.edtUkuran.text.toString()*/
            )

            updatedBarang?.let {
                editViewModel.updateLaporan(it)
                activity?.supportFragmentManager?.popBackStack()
            }
        }
        return root
    }
    private fun setCheckBoxState(selectedColors: List<String>) {
        val colorCheckBoxes = mapOf(
            "merah" to binding.root.findViewById<CheckBox>(R.id.box_item_1),
            "biru" to binding.root.findViewById<CheckBox>(R.id.box_item_2),
            "kuning" to binding.root.findViewById<CheckBox>(R.id.box_item_3),
            "hijau" to binding.root.findViewById<CheckBox>(R.id.box_item_4)
        )

        colorCheckBoxes.forEach { (color, checkBox) ->
            checkBox.isChecked = selectedColors.contains(color)
        }
    }
    private fun getSelectedColors(): List<String> {
        val selectedColors = mutableListOf<String>()
        if (binding.root.findViewById<CheckBox>(R.id.box_item_1).isChecked) {
            selectedColors.add("merah")
        }
        if (binding.root.findViewById<CheckBox>(R.id.box_item_2).isChecked) {
            selectedColors.add("biru")
        }
        if (binding.root.findViewById<CheckBox>(R.id.box_item_3).isChecked) {
            selectedColors.add("kuning")
        }
        if (binding.root.findViewById<CheckBox>(R.id.box_item_4).isChecked) {
            selectedColors.add("hijau")
        }
        return selectedColors
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
