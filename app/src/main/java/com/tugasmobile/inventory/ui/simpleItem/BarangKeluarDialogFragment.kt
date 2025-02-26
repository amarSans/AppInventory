package com.tugasmobile.inventory.ui.simpleItem

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterColorOut
import com.tugasmobile.inventory.adapter.AdapterUkuranOut
import com.tugasmobile.inventory.data.UkuranItem
import com.tugasmobile.inventory.databinding.PopupBarangKeluarBinding

class BarangKeluarDialogFragment : DialogFragment() {
    private var _binding: PopupBarangKeluarBinding? = null
    private var listener: BarangKeluarListener? = null
    private val binding get() = _binding!!
    private lateinit var colorAdapter: AdapterColorOut
    private lateinit var ukuranAdapter: AdapterUkuranOut

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = PopupBarangKeluarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ppimgButtonAdd.setOnClickListener {
            updateStock(1)
        }
        binding.ppoimgButtonRemove.setOnClickListener {
            updateStock(-1)
        }
        // Menampilkan data barang
        val kodeBarang = arguments?.getString("kodeBarang") ?: "Tidak ada"
        val namaBarang = arguments?.getString("namaBarang") ?: "Tidak ada"
        val hargaBarang = arguments?.getString("hargaBarang") ?: "Tidak ada"
        val warnaBarang = arguments?.getString("warnaBarang") ?: ""
        val ukuranBarang = arguments?.getString("ukuran") ?: ""

        // Tampilkan data di UI
        binding.ppKodeBarang.text = "Kode: $kodeBarang"
        binding.ppNamaBarang.text = "Nama: $namaBarang"
        binding.ppHargaBarang.text = "Harga: Rp. $hargaBarang"
        if (warnaBarang.isNotEmpty()) {
            setupRecyclerViewWarna(warnaBarang)
        }
        if (ukuranBarang.isNotEmpty()) {
            setupRecyclerViewUkuran(ukuranBarang)
        }
        binding.ppClose.setOnClickListener {
            dismiss()
        }
        binding.ppbuttonSave.setOnClickListener {
            val kodeBarang = arguments?.getString("kodeBarang") ?: ""
            val namaBarang = arguments?.getString("namaBarang") ?: ""
            val hargaBarang = arguments?.getString("hargaBarang") ?: ""
            val warnaTerpilih = colorAdapter.getSelectedColors()

            val ukuranTerpilih = ukuranAdapter.getSelectedUkuran()

            val stokKeluar = binding.ppedtStokKeluar.text.toString().toIntOrNull() ?: 0

            val hargaBeli = binding.ppedtHargaBeli.text.toString().toIntOrNull() ?: 0

            listener?.onBarangKeluarSaved(kodeBarang, namaBarang, hargaBarang, warnaTerpilih, ukuranTerpilih, stokKeluar,hargaBeli)

            dismiss()
        }
    }

    private fun setupRecyclerViewUkuran(ukuranBarang: String) {

        // Memproses ukuran menjadi list
        val ukuranList = ukuranBarang.split(",").map { UkuranItem(it.trim()) }

        ukuranAdapter = AdapterUkuranOut(ukuranList)
        binding.pprvUkuranSendal.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.pprvUkuranSendal.adapter = ukuranAdapter
    }

    private fun setupRecyclerViewWarna(warnaBarang: String) {
        val colorNames = resources.getStringArray(R.array.daftar_nama_warna)
        val colorValues = resources.getStringArray(R.array.daftar_warna)

        val colorMap = colorNames.indices.associate { index -> colorNames[index] to colorValues[index] }
        val warnaFromDb = warnaBarang.removeSurrounding("[", "]").split(",").map { it.trim() }
        val selectedColorValues = warnaFromDb.mapNotNull { colorMap[it] }

        colorAdapter = AdapterColorOut(requireContext(), warnaFromDb.toMutableList())

        binding.pprvWarnaSendal.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.pprvWarnaSendal.adapter = colorAdapter
        colorAdapter.updateColors(warnaFromDb,selectedColorValues)
    }

    private fun updateStock(change: Int) {
        val currentStock = binding.ppedtStokKeluar.text.toString().toInt()
        val newStock = (currentStock + change).coerceAtLeast(0) // Biar stok tidak negatif
        binding.ppedtStokKeluar.text = newStock.toString()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        fun newInstance(
            kodeBarang: String,
            namaBarang: String,
            hargaBarang: String,
            warnaBarang: String,
            ukuranBarang: String,
        ): BarangKeluarDialogFragment {
            val fragment = BarangKeluarDialogFragment()
            val args = Bundle().apply {
                putString("kodeBarang", kodeBarang)
                putString("namaBarang", namaBarang)
                putString("hargaBarang", hargaBarang)
                putString("warnaBarang", warnaBarang)
                putString("ukuran", ukuranBarang)
            }
            fragment.arguments = args
            return fragment
        }
    }
    fun setBarangKeluarListener(listener: BarangKeluarListener) {
        this.listener = listener
    }
    interface BarangKeluarListener {
        fun onBarangKeluarSaved(
            kodeBarang: String,
            namaBarang: String,
            hargaBarang: String,
            warnaTerpilih: List<String>,
            ukuranTerpilih: List<String>,
            stokKeluar: Int,
            hargaBeli: Int
        )
    }
}