package com.tugasmobile.inventory.ui.Barang

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdafterTransaksiBarangKeluar
import com.tugasmobile.inventory.data.DaftarBarangKeluar
import com.tugasmobile.inventory.databinding.FragmentBarangKeluarBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.ui.simpleItem.BarangKeluarDialogFragment
import com.tugasmobile.inventory.utils.HargaUtils

class BarangKeluar : Fragment() {
    private var _binding: FragmentBarangKeluarBinding? = null
    private val binding get() = _binding!!
    private lateinit var barangKeluarViewModel: ViewModel
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>
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
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_main, menu)
                menu.findItem(R.id.action_filter)?.isVisible = false
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> {

                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        setupViewModel()
        setupRecyclerView()
        setupAutoCompleteTextView()
        observeSearchResults()
        setupItemSelection()
        setupTotalBayarListener()
        setupClearButton()
        HargaUtils.setupHargaTextWatcher(binding.uangDibayar)
        binding.btnBarangKeluar.setOnClickListener {
            resetUI() // Reset semua tampilan data
        }
    }


    private fun setupViewModel() {
        barangKeluarViewModel = ViewModelProvider(this).get(ViewModel::class.java)
    }

    private fun setupRecyclerView() {
        adapterTransaksi = AdafterTransaksiBarangKeluar(daftarBarangKeluar)
        binding.recyclerViewBarangKeluar.apply {
            adapter = adapterTransaksi
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupAutoCompleteTextView() {
        autoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, mutableListOf())
        binding.autoCompleteBarang.setAdapter(autoCompleteAdapter)
        binding.autoCompleteBarang.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) barangKeluarViewModel.searchBarang(s.toString())
                else {
                    autoCompleteAdapter.clear()
                    autoCompleteAdapter.notifyDataSetChanged()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeSearchResults() {
        barangKeluarViewModel.dataSearch.observe(viewLifecycleOwner) { result ->
            autoCompleteAdapter.clear()
            autoCompleteAdapter.addAll(
                if (result.isNullOrEmpty()) listOf("Barang Tidak Ditemukan")
                else result.map { "${it.id} / ${it.namaBarang} / ${it.nama_toko}" }
            )
            autoCompleteAdapter.notifyDataSetChanged()
            binding.autoCompleteBarang.showDropDown()
        }
    }

    private fun setupItemSelection() {
        binding.autoCompleteBarang.setOnItemClickListener { _, _, position, _ ->
            val selectedItemText = autoCompleteAdapter.getItem(position)
            if (selectedItemText == "Barang Tidak Ditemukan") {
                binding.autoCompleteBarang.setText("")
                return@setOnItemClickListener
            }
            val selectedItem = barangKeluarViewModel.dataBarangAksesList.value?.find {
                "${it.id} / ${it.namaBarang} / ${it.nama_toko}" == selectedItemText
            }
            selectedItem?.let {
                barangKeluarViewModel.setCurrentBarang(it.id)
                showBarangKeluarDialog(it.id)
                binding.autoCompleteBarang.postDelayed({ binding.autoCompleteBarang.text.clear() }, 100)
            }
        }
    }

    private fun setupTotalBayarListener() {
        adapterTransaksi.onTotalHargaUpdated = { totalBayar ->
            binding.totalBayar.text = "Rp. ${HargaUtils.formatHarga(totalBayar)}"
            updateKembalian(totalBayar)
            binding.uangDibayar.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateKembalian(totalBayar)
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun updateKembalian(totalBayar: Int) {
        val dibayarText = binding.uangDibayar.text.toString().replace(".", "")
        val dibayar = dibayarText.toIntOrNull() ?: 0
        val kembalian = dibayar - totalBayar

        // Format kembalian sebagai Rupiah
        binding.kembalian.text = "Rp. ${HargaUtils.formatHarga(kembalian)}"
    }

    private fun setupClearButton() {
        binding.autoCompleteBarang.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableStart = binding.autoCompleteBarang.compoundDrawables[0]
                drawableStart?.let {
                    val drawableWidth = it.bounds.width()
                    if (event.rawX <= (binding.autoCompleteBarang.left + drawableWidth + binding.autoCompleteBarang.paddingStart)) {
                        binding.autoCompleteBarang.text.clear()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }

    private fun showBarangKeluarDialog(kode: String) {
        val dialog = BarangKeluarDialogFragment.newInstance(kode)
        dialog.setBarangKeluarListener(object : BarangKeluarDialogFragment.BarangKeluarListener {
            override fun onBarangKeluarSaved(
                kodeBarang: String,
                namaBarang: String,
                hargaBarang: String,
                ukuranWarnaTerpilih: List<String>,
                stokKeluar: Int,
                hargaBeli: Int
            ) {
                val barangKeluar = DaftarBarangKeluar(kodeBarang, stokKeluar, ukuranWarnaTerpilih, hargaBeli, 0)
                daftarBarangKeluar.add(barangKeluar)
                adapterTransaksi.notifyDataSetChanged()
            }
        })
        dialog.show(parentFragmentManager, "BarangKeluarDialog")
    }
    private fun resetUI() {
        // Reset AutoCompleteTextView
        binding.autoCompleteBarang.text.clear()

        // Reset RecyclerView
        daftarBarangKeluar.clear()
        adapterTransaksi.notifyDataSetChanged()

        // Reset TextView untuk total bayar dan kembalian
        binding.totalBayar.text = "0"
        binding.kembalian.text = "0"

        // Reset EditText untuk uang dibayar
        binding.uangDibayar.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
