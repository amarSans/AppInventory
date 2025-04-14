package com.muammar.inventory.ui.data

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.muammar.inventory.R
import com.muammar.inventory.adapter.AdapterColorIn
import com.muammar.inventory.data.History
import com.muammar.inventory.data.StokUpdate
import com.muammar.inventory.databinding.FragmentTambahStokBinding
import com.muammar.inventory.ui.InventoryViewModelFactory
import com.muammar.inventory.utils.AnimationHelper
import com.muammar.inventory.utils.DateUtils
import com.muammar.inventory.utils.FormatAngkaUtils
import com.muammar.inventory.utils.HargaUtils


private const val ARG_KODE_BARANG = "kodeBarang"

class TambahStokFragment : Fragment() {
    private var kodeBarang: String? = null
    private lateinit var binding: FragmentTambahStokBinding
    private val NewStokViewModel: DataViewModel by viewModels {
        InventoryViewModelFactory.getInstance(requireActivity().application)
    }
    private lateinit var colorAdapter: AdapterColorIn
    private var selectedImageUri: Uri? = null
    private var stokBarang = 0
    private var stokhistory =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            kodeBarang = it.getString(ARG_KODE_BARANG)
            Log.d("TambahStokFragment", "Kode Barang diterima: $kodeBarang")
        }
        if (kodeBarang?.isNotEmpty() == true) kodeBarang?.let { NewStokViewModel.setCurrentBarang(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTambahStokBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnimationHelper.animateItems(binding.constrainttambahstok,requireContext())
        binding.imgViewBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        kodeBarang?.let {
            NewStokViewModel.loadBarang()
        }

        setupObservers()
        setupUI()
    }

    private fun setupUI() {
        binding.buttonAddStok.setOnClickListener { tambahStok() }
        binding.buttonRemoveStok.setOnClickListener { kurangiStok() }
        binding.btnsave.setOnClickListener {
            saveChanges()
        }
        val colorNames = resources.getStringArray(R.array.daftar_nama_warna)
        val colorValues = resources.getStringArray(R.array.daftar_warna)
        colorAdapter = AdapterColorIn(requireContext(), colorNames, colorValues)
        binding.editStokBarang.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val stokBarangText = s?.toString()?.trim() ?: ""
                val stokBarang = stokBarangText.toIntOrNull() ?: 0

                val currentText = binding.editTextUkuranwarna.text.toString()
                val jumlahKombinasi = if (currentText.isEmpty()) 0 else currentText.split(",").size

                binding.iconCheck.isEnabled = stokBarang > jumlahKombinasi
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.edtUkuran.addTextChangedListener(object : TextWatcher {
            val daftarUkuran = resources.getStringArray(R.array.daftar_ukuran_valid).toSet()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().trim()
                if (input.isNotEmpty() && input !in daftarUkuran) {
                    binding.edtUkuran.error = "Ukuran tidak valid. Gunakan ukuran standar!"
                } else {
                    binding.edtUkuran.error = null
                }
            }
        })
        setupSpinners()
    }

    private fun setupSpinners() {
        val daftarUkuran = resources.getStringArray(R.array.daftar_ukuran_valid).toSet()
        val warnaList = resources.getStringArray(R.array.daftar_nama_warna)

        val warnaAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, warnaList)
        warnaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWarna.adapter = warnaAdapter

        binding.iconClose.setOnClickListener {
            binding.edtUkuran.setText("")
            binding.spinnerWarna.setSelection(0)
        }

        binding.iconCheck.setOnClickListener {

            val selectedUkuranText = binding.edtUkuran.text.toString().trim()
            val selectedWarna = binding.spinnerWarna.selectedItem as String

            if (selectedUkuranText.isEmpty()) {
                binding.edtUkuran.error = "Ukuran tidak boleh kosong"
                return@setOnClickListener
            }

            val selectedUkuran = selectedUkuranText.toDoubleOrNull()
                ?.let { it1 -> FormatAngkaUtils.formatAngka(it1) }
            if (selectedUkuranText !in daftarUkuran) {
                binding.edtUkuran.error = "Ukuran tidak valid. Gunakan ukuran standar!"
                return@setOnClickListener
            }
            if (selectedWarna.equals("kosong", ignoreCase = true)) {
                Toast.makeText(requireContext(), "Pilih warna dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val stokBarangText = binding.editStokBarang.text.toString().trim()
            val stokBarang = stokBarangText.toIntOrNull() ?: 0

            val currentText = binding.editTextUkuranwarna.text.toString()
            val jumlahKombinasi = if (currentText.isEmpty()) 0 else currentText.split(",").size

            if (stokBarang == jumlahKombinasi) {
                Toast.makeText(
                    requireContext(),
                    "Tambahkan stok jika ingin menambahkan ukuran dan warna",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val newEntry = "$selectedUkuran $selectedWarna"

            val updatedText = if (currentText.isEmpty()) {
                newEntry
            } else {
                "$currentText, $newEntry"
            }

            binding.editTextUkuranwarna.setText(updatedText)

            binding.edtUkuran.setText("")
            binding.spinnerWarna.setSelection(0)
        }
        binding.removeUkuranwarna.setOnClickListener {
            val currentText = binding.editTextUkuranwarna.text.toString()

            if (currentText.isNotEmpty()) {
                val listEntries = currentText.split(", ").toMutableList()
                listEntries.removeLastOrNull()
                val updatedText = listEntries.joinToString(", ")
                binding.editTextUkuranwarna.setText(updatedText)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Tidak ada ukuran-warna untuk dihapus",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupObservers() {
        NewStokViewModel.currentBarang.observe(viewLifecycleOwner) { barang ->
            Log.d("TambahStokFragment", "Data Barang: $barang")
            barang?.let {
                binding.tvkodebarang.setText(it.id_barang)
                selectedImageUri = if (it.gambar.isNullOrEmpty()) null else Uri.parse(it.gambar)
                binding.imgTambahstok.setImageURI(selectedImageUri)
            } ?: run {
                Toast.makeText(requireContext(), "Data barang tidak ditemukan", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        NewStokViewModel.currentStok.observe(viewLifecycleOwner) { stok ->
            stok?.let {
                stokhistory = it.stokBarang
                if (it.stokBarang == 0) {
                    binding.editTextUkuranwarna.setText("")
                    binding.editStokBarang.setText("")
                } else{
                binding.editTextUkuranwarna.setText(
                    it.ukuranwarna
                        .replace("[", "")
                        .replace("]", "")
                        .trim()
                )
                binding.editStokBarang.setText(it.stokBarang.toString())}
                stokBarang = it.stokBarang
            } ?: run {
                Toast.makeText(requireContext(), "Data stok tidak ditemukan", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        NewStokViewModel.currentBarangIn.observe(viewLifecycleOwner) { barangIn ->
            barangIn?.let {
                it.Tgl_Masuk=DateUtils.getCurrentDate()
                binding.editTextHargaBarangEdit.setText(HargaUtils.formatHarga(it.Harga_Modal))
                binding.edtNamaTokoEdit.setText(it.Nama_Toko)
            } ?: run {
                Toast.makeText(
                    requireContext(),
                    "Data barang masuk tidak ditemukan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveChanges() {
        val stokBarangText = binding.editStokBarang.text.toString().trim()
        val stokBarang = stokBarangText.toIntOrNull() ?: 0

        val ukuranWarna = binding.editTextUkuranwarna.text.toString().trim()
        val ukuranWarnaList =
            if (ukuranWarna.isEmpty()) emptyList() else ukuranWarna.split(",").map { it.trim() }
        val jumlahKombinasi = ukuranWarnaList.size
        if (stokBarang != jumlahKombinasi) {
            binding.editTextUkuranwarna.error =
                "Jumlah stok ($stokBarang) harus sama dengan jumlah kombinasi ukuran dan warna ($jumlahKombinasi)"
            return
        }

        val hargaBarang =
            binding.editTextHargaBarangEdit.text.toString().replace(".", "").toIntOrNull() ?: 0
        val namaToko = binding.edtNamaTokoEdit.text.toString()

        val currentStok = NewStokViewModel.currentStok.value
        if (currentStok == null) {
            Toast.makeText(requireContext(), "Data stok tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedStok = StokUpdate(
            kodeBarang = currentStok.id_barang,
            ukuranWarnaBaru = ukuranWarnaList.joinToString(","),
            hargaJualBaru = hargaBarang,
            tanggalMasukBaru = DateUtils.getCurrentDate(),
            namaTokoBaru = namaToko,
            stokBaru = stokBarang
        )
        stokhistory= stokBarang-stokhistory
        val history= History(
            id = 0,
            waktu = DateUtils.getCurrentDate(),
            kodeBarang = currentStok.id_barang,
            stok = stokhistory.toString(),
            jenisData = "stokmasuk"
        )
        NewStokViewModel.insertHistory(history)
        NewStokViewModel.updatestok(updatedStok)

        NewStokViewModel.loadBarang()
        requireActivity().finish()
    }

    private fun tambahStok() {
        stokBarang++
        binding.editStokBarang.setText(stokBarang.toString())
    }

    private fun kurangiStok() {
        if (stokBarang > 0) stokBarang--
        binding.editStokBarang.setText(stokBarang.toString())
    }

    companion object {
        @JvmStatic
        fun newInstance(kodeBarang: String) =
            TambahStokFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_KODE_BARANG, kodeBarang)
                }
            }
    }
}