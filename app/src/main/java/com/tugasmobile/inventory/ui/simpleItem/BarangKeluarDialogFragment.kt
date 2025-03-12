package com.tugasmobile.inventory.ui.simpleItem

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterColorOut
import com.tugasmobile.inventory.data.BarangOut
import com.tugasmobile.inventory.databinding.PopupBarangKeluarBinding
import com.tugasmobile.inventory.ui.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BarangKeluarDialogFragment : DialogFragment() {
    private var _binding: PopupBarangKeluarBinding? = null
    private var listener: BarangKeluarListener? = null
    private val BarangKeluarViewModel: ViewModel by viewModels()
    private val binding get() = _binding!!
    private lateinit var colorAdapter: AdapterColorOut
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = PopupBarangKeluarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HargaUtils.setupHargaTextWatcher(binding.ppedtHargaBeli)

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
        val ukuranWarnaBarang = arguments?.getString("ukuranWarnaBarang") ?: ""
        Log.d("BarangKeluarDialog", "Data ukuranWarnaBarang diterima: $ukuranWarnaBarang")

        val hargaBarangFormatted = HargaUtils.formatHarga(hargaBarang.toIntOrNull() ?: 0)

        // Tampilkan data di UI
        binding.ppKodeBarang.text = "Kode: $kodeBarang"
        binding.ppNamaBarang.text = "Nama: $namaBarang"
        binding.ppHargaBarang.text = "Harga: Rp. $hargaBarangFormatted"
        colorAdapter = AdapterColorOut(requireContext())
        binding.pprvUkuranwarnaSendal.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.pprvUkuranwarnaSendal.adapter = colorAdapter
        if (ukuranWarnaBarang.isNotEmpty()) {
            setupRecyclerViewUkuranWarna(ukuranWarnaBarang)
        }
        binding.ppClose.setOnClickListener {
            dismiss()
        }


        binding.ppbuttonSave.setOnClickListener {
            val kodeBarang = arguments?.getString("kodeBarang") ?: ""
            val namaBarang = arguments?.getString("namaBarang") ?: ""
            val hargaBarang = arguments?.getString("hargaBarang") ?: "0"
            val ukuranWarnaTerpilih = colorAdapter.getSelectedColors()
            val stokKeluar = binding.ppedtStokKeluar.text.toString().toIntOrNull() ?: 0
            val hargaBeliText = binding.ppedtHargaBeli.text.toString().replace(".", "")
            val hargaBeli = hargaBeliText.toIntOrNull() ?: 0

            val tanggalKeluar= DateUtils.getCurrentDate()
            val barangOut = BarangOut(
                IdBrgKeluar=0,
                id_barang = kodeBarang,
                ukuran_warna = ukuranWarnaTerpilih.joinToString(","),
                stok_keluar = stokKeluar,
                Tgl_Keluar = tanggalKeluar,
                Hrg_Beli = hargaBeli
            )
            Log.d("BarangKeluarDialog", "Warna yang dipilih sebelum disimpan: $ukuranWarnaTerpilih")
            Log.d("BarangKeluarDialog", "Data barang yang akan disimpan: id=$kodeBarang, ukuran_warna=${ukuranWarnaTerpilih.joinToString(", ")}, stok_keluar=$stokKeluar, harga_beli=$hargaBeli")

            simpanDatabase(barangOut)
            listener?.onBarangKeluarSaved(kodeBarang, namaBarang, hargaBarang, ukuranWarnaTerpilih, stokKeluar,hargaBeli)

            dismiss()
        }
    }
    private fun simpanDatabase(barangOut: BarangOut){
        CoroutineScope(Dispatchers.IO).launch {
            BarangKeluarViewModel.insertBarangKeluar(barangOut)
        }
    }

    private fun setupRecyclerViewUkuranWarna(warnaBarang: String) {

        val colorNames = resources.getStringArray(R.array.daftar_nama_warna)
        val colorValues = resources.getStringArray(R.array.daftar_warna)

        val colorMap = colorNames.indices.associate { index -> colorNames[index] to colorValues[index] }

        val warnaFromDb = warnaBarang.removeSurrounding("[", "]").split(",").map { it.trim() }
        val warnaOnly = warnaFromDb.map { item ->
            item.split(" ").last().trim() // Ambil bagian terakhir (warna)
        }
        val selectedColorValues = warnaOnly.mapNotNull { colorMap[it] }
        Log.d("BarangKeluarDialog", "Warna dari DB (mentah): $warnaBarang")
        Log.d("BarangKeluarDialog", "Warna setelah diproses: $warnaFromDb")
        Log.d("BarangKeluarDialog", "Warna yang dipilih (hanya warna): $warnaOnly")
        Log.d("BarangKeluarDialog", "Warna yang cocok di daftar: $selectedColorValues")

       if (warnaFromDb.isNotEmpty() && selectedColorValues.isNotEmpty()) {
            colorAdapter.updateColors(warnaFromDb, selectedColorValues)
        } else {
            Log.e("BarangKeluarDialog", "Tidak ada data warna yang valid")
            Toast.makeText(requireContext(), "Tidak ada data warna yang valid", Toast.LENGTH_SHORT).show()
        }
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
            ukuranWarnaBarang: String,
        ): BarangKeluarDialogFragment {
            val fragment = BarangKeluarDialogFragment()
            val args = Bundle().apply {
                putString("kodeBarang", kodeBarang)
                putString("namaBarang", namaBarang)
                putString("hargaBarang", hargaBarang)
                putString("ukuranWarnaBarang", ukuranWarnaBarang)
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
            ukuranWarnaTerpilih: List<String>,
            stokKeluar: Int,
            hargaBeli: Int
        )
    }
}