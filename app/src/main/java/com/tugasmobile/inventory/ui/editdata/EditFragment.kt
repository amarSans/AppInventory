package com.tugasmobile.inventory.ui.editdata

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterColorIn
import com.tugasmobile.inventory.databinding.FragmentEditBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.ui.uiData.BottonUkuranSheet

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var editViewModel: ViewModel
    private var barangId: Long = 0
    private var stokBarang = 0
    private lateinit var recyclerView: RecyclerView
    private var selectedSizesList: List<String> = emptyList()
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
        binding.buttonAddStok.setOnClickListener { tambahStok() }
        binding.buttonRemoveStok.setOnClickListener { kurangiStok() }
        val colorNames = resources.getStringArray(R.array.daftar_nama_warna)
        val colorValues = resources.getStringArray(R.array.daftar_warna)

        // Inisialisasi RecyclerView dan Adapter
        recyclerView = binding.recyclerViewColorsEdit
        val colorAdapter = AdapterColorIn(requireContext(), colorNames, colorValues)

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = colorAdapter
        binding.edtUkuran.setOnClickListener {
            val bottonUkuranSheet = BottonUkuranSheet.newInstance(selectedSizesList) // Kirim ukuran yang dipilih
            bottonUkuranSheet.listener = object : BottonUkuranSheet.SizeSelectionListener {
                override fun onSizeSelected(selectedSizes: List<String>) {
                    selectedSizesList = selectedSizes
                    binding.edtUkuran.text = selectedSizes.joinToString(", ")
                }
            }
            bottonUkuranSheet.show(parentFragmentManager, BottonUkuranSheet.TAG)
        }

        // Mengisi field dengan data barang yang diambil dari ViewModel
        editViewModel.currentBarang.observe(viewLifecycleOwner) { barang ->
            barang?.let {
                binding.editTextNamaBarang.setText(it.namaBarang)
                binding.editStokBarang.setText(it.stok.toString())
                binding.editTextHargaBarang.setText(it.harga.toString())
                binding.editTextKodeBarang.setText(it.kodeBarang)
                stokBarang=it.stok
                val warnaFromDb = it.warna // Pastikan ini mengembalikan List<String> warna
                colorAdapter.setSelectedColors(warnaFromDb)
                val categories = resources.getStringArray(R.array.daftar_pilihan)
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.SpinnerKategori.adapter = adapter
                binding.editTextDate.setText(it.waktu)
                val selectedPosition = categories.indexOf(it.kategori)
                binding.SpinnerKategori.setSelection(if (selectedPosition >= 0) selectedPosition else 0)


                binding.edtUkuran.text = it.ukuran
                selectedSizesList = it.ukuran.split(",").map { size -> size.trim() }

            }

        }
        binding.SpinnerKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = binding.SpinnerKategori.selectedItem.toString()
                editViewModel.currentBarang.value?.kategori = selectedCategory
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        // Menyimpan perubahan ketika tombol save ditekan
        binding.buttonSave.setOnClickListener {
            val selectedColors = colorAdapter.getSelectedColors().toSet() // Set untuk mencegah duplikasi

            // Dapatkan warna dari data yang sudah ada
            val currentColors = editViewModel.currentBarang.value?.warna?.toSet() ?: emptySet()

            // Menghapus warna yang tidak terpilih
            val colorsToRemove = currentColors - selectedColors // Warna yang ada di database tetapi tidak dipilih
            val colorsToAdd = selectedColors - currentColors
            val updatedBarang = editViewModel.currentBarang.value?.copy(
                namaBarang = binding.editTextNamaBarang.text.toString(),
                stok = binding.editStokBarang.text.toString().toInt(),
                harga = binding.editTextHargaBarang.text.toString().toInt(),
                kodeBarang = binding.editTextKodeBarang.text.toString(),
                warna = selectedColors.toList(),
                kategori = binding.SpinnerKategori.selectedItem.toString(), // Use the selected item
                ukuran = binding.edtUkuran.text.toString()
            )

            updatedBarang?.let {
                editViewModel.updateWarna(it.id, selectedColors.toList())
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
    private fun tambahStok() {

        stokBarang += 1
        binding.editStokBarang.setText(stokBarang.toString())
    }

    // Fungsi untuk mengurangi stok (tidak kurang dari 0)
    private fun kurangiStok() {
        if (stokBarang > 0) {
            stokBarang -= 1
        }
        binding.editStokBarang.setText(stokBarang.toString())
    }
}
