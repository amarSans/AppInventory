package com.tugasmobile.inventory.ui.Barang

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.adapter.AdafterTransaksiBarangKeluar
import com.tugasmobile.inventory.data.DaftarBarangKeluar
import com.tugasmobile.inventory.databinding.FragmentBarangKeluarBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.ui.simpleItem.BarangKeluarDialogFragment
import kotlinx.coroutines.selects.select


class BarangKeluar : Fragment() {
    private var _binding: FragmentBarangKeluarBinding? = null
    private val binding get() = _binding!!
    private lateinit var barangKeluarViewModel: ViewModel
    private lateinit var autoCompletxTextadapter: ArrayAdapter<String>
    private val daftarBarangKeluar = mutableListOf<DaftarBarangKeluar>()
    private lateinit var adapterTransaksi: AdafterTransaksiBarangKeluar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarangKeluarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barangKeluarViewModel = ViewModelProvider(this).get(ViewModel::class.java)
        adapterTransaksi = AdafterTransaksiBarangKeluar(daftarBarangKeluar)
        binding.recyclerViewBarangKeluar.adapter = adapterTransaksi
        binding.recyclerViewBarangKeluar.layoutManager = LinearLayoutManager(requireContext())
        setupAutoCompleteTextView()
        observeSearchResults()
        setupItemSelection()
        adapterTransaksi.onTotalHargaUpdated = { total_bayar ->
            binding.totalBayar.text = total_bayar.toString()
            Log.d("Transaksi", "Total Bayar: $total_bayar")
            val dibayarText = binding.uangDibayar.text.toString()
            val dibayar=if (dibayarText.isNotEmpty()) dibayarText.toInt() else 0
            val kembalian = dibayar - total_bayar
            binding.totalBayar.text=total_bayar.toString()
            binding.kembalian.text=kembalian.toString()
            binding.uangDibayar.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val dibayarBaru = if (!s.isNullOrEmpty()) s.toString().toInt() else 0
                    val kembalianBaru = dibayarBaru - total_bayar
                    binding.kembalian.text = kembalianBaru.toString()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun setupAutoCompleteTextView() {
        autoCompletxTextadapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        binding.autoCompleteBarang.setAdapter(autoCompletxTextadapter)

        binding.autoCompleteBarang.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    barangKeluarViewModel.searchBarang(s.toString())
                } else {
                    autoCompletxTextadapter.clear()
                    autoCompletxTextadapter.notifyDataSetChanged()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeSearchResults() {
        barangKeluarViewModel.dataSearch.observe(viewLifecycleOwner) { result ->
            autoCompletxTextadapter.clear()
            if (result.isNullOrEmpty()) {
                autoCompletxTextadapter.add("Barang Tidak Ditemukan")
            } else {
                autoCompletxTextadapter.addAll(result.map { "${it.kodeBarang} / ${it.namaBarang} / ${it.nama_toko}" })
            }
            autoCompletxTextadapter.notifyDataSetChanged()
            binding.autoCompleteBarang.showDropDown()
        }
    }
    private fun setupItemSelection() {
        binding.autoCompleteBarang.setOnItemClickListener { _, _, position, _ ->
            val selectedItemText = autoCompletxTextadapter.getItem(position)

            if (selectedItemText == "Barang Tidak Ditemukan") {
                binding.autoCompleteBarang.setText("")
            } else {
                val selectedItem = barangKeluarViewModel.dataBarangMasukList.value?.find {
                    "${it.kodeBarang} / ${it.namaBarang} / ${it.nama_toko}" == selectedItemText
                }

                selectedItem?.let {
                    binding.autoCompleteBarang.setText(it.namaBarang)
                    barangKeluarViewModel.setCurrentBarang(it.id)
                    showBarangKeluarDialog(it.kodeBarang, it.namaBarang, it.harga.toString(), it.warna.toString(), it.ukuran)
                }
            }
        }
    }

    private fun showBarangKeluarDialog(kode: String, nama: String, harga: String, warna: String, ukuran: String) {
        val dialog = BarangKeluarDialogFragment.newInstance(kode, nama, harga, warna, ukuran)
        dialog.setBarangKeluarListener(object : BarangKeluarDialogFragment.BarangKeluarListener {
            override fun onBarangKeluarSaved(
                kodeBarang: String,
                namaBarang: String,
                hargaBarang: String,
                warnaTerpilih: List<String>,
                ukuranTerpilih: List<String>,
                stokKeluar: Int,
                hargaBeli: Int
            ) {
                Log.d("BarangKeluar", "Data disimpan: Kode: $kodeBarang, Nama: $namaBarang, Harga: $hargaBarang, Warna: $warnaTerpilih, Ukuran: $ukuranTerpilih, Stok: $stokKeluar,hargabeli: $hargaBeli")
                // Bisa update UI atau simpan ke database di sini
                val barangKeluar = DaftarBarangKeluar(
                    kodeBarang = kodeBarang,
                    stok = stokKeluar,
                    ukuran = ukuranTerpilih.joinToString(", "), // Gabungkan list ukuran menjadi satu string
                    hargaJual = hargaBeli,
                    hargaTotal = 0
                )

                // Tambahkan ke daftar dan update adapter
                daftarBarangKeluar.add(barangKeluar)
                adapterTransaksi.notifyDataSetChanged()

            }
        })
        dialog.show(parentFragmentManager, "BarangKeluarDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
