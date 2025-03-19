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
import com.bumptech.glide.Glide
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.AdapterColorOut
import com.tugasmobile.inventory.data.BarangOut
import com.tugasmobile.inventory.data.History
import com.tugasmobile.inventory.databinding.PopupBarangKeluarBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.utils.DateUtils
import com.tugasmobile.inventory.utils.HargaUtils
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
            val currentStock = binding.ppedtStokKeluar.text.toString().toIntOrNull() ?: 0
            BarangKeluarViewModel.currentStok.value?.let { barang ->
                if (currentStock >= barang.stokBarang) {
                    Toast.makeText(requireContext(), "Stok sudah mencapai batas!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                binding.ppedtStokKeluar.setText((currentStock + 1).toString())
            }
        }
        binding.ppoimgButtonRemove.setOnClickListener {
            updateStock(-1)
        }
        val kodeBarang = arguments?.getString("kodeBarang") ?: "Tidak ada"
        BarangKeluarViewModel.setCurrentBarang(kodeBarang)
        BarangKeluarViewModel.currentBarang.observe(viewLifecycleOwner) { barang ->
            barang?.let {
                val kodeBarangid = it.id_barang
                val namaBarang = it.nama_barang
                val image=it.gambar

                // Tampilkan data di UI
                binding.ppKodeBarang.text = "Kode: $kodeBarangid"
                binding.ppNamaBarang.text = "Nama: $namaBarang"
                // Tampilkan gambar menggunakan Glide
                Glide.with(requireContext())
                    .load(image) // Pastikan image ini berupa URL atau path yang valid
                    .placeholder(R.drawable.baseline_account_circle_24) // Gambar sementara saat loading
                    .error(R.drawable.baseline_account_circle_24) // Gambar jika gagal memuat
                    .into(binding.ppImageView)

            }
        }
        BarangKeluarViewModel.currentBarangIn.observe(viewLifecycleOwner) { barang ->
            barang?.let {
                val hargaBarang = it.Harga_Modal
                val hargaBarangFormatted = HargaUtils.formatHarga(hargaBarang)
                binding.ppHargaBarang.text = "Harga: Rp. $hargaBarangFormatted"

            }
        }

        BarangKeluarViewModel.currentStok.observe(viewLifecycleOwner) { barang ->
            barang?.let {
                val ukuranWarnaBarang = it.ukuranwarna
                colorAdapter = AdapterColorOut(requireContext())
                binding.pprvUkuranwarnaSendal.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                binding.pprvUkuranwarnaSendal.adapter = colorAdapter
                if (ukuranWarnaBarang.isNotEmpty()) {
                    setupRecyclerViewUkuranWarna(ukuranWarnaBarang)
                }
            }
        }

        binding.ppClose.setOnClickListener {
            dismiss()
        }


        binding.ppbuttonSave.setOnClickListener {
            val namaBarang = binding.ppNamaBarang.text.toString().trim()
            val hargaBarang = binding.ppHargaBarang.text.toString().trim()
            val hargaModal = hargaBarang.toIntOrNull() ?: 0
            val ukuranWarnaTerpilih = colorAdapter.getSelectedColors()
            val stokKeluar = binding.ppedtStokKeluar.text.toString().toIntOrNull() ?: 0
            val hargaBeliText = binding.ppedtHargaBeli.text.toString().replace(".", "")

            val hargaBeli = hargaBeliText.toIntOrNull() ?: 0
            val tanggalKeluar = DateUtils.getCurrentDate()
            if (ukuranWarnaTerpilih.isEmpty() || stokKeluar == 0 || hargaBeli == 0
            ) {
                Toast.makeText(requireContext(), "Semua data harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (hargaBeli < hargaModal) {
                Toast.makeText(
                    requireContext(),
                    "Harga jual lebih kecil dari modal! Anda rugi!",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            BarangKeluarViewModel.currentStok.observe(viewLifecycleOwner) { barang ->
                barang?.let {
                    if (ukuranWarnaTerpilih.size != stokKeluar) {
                        Toast.makeText(
                            requireContext(),
                            "Jumlah ukuran warna harus sesuai dengan stok keluar!",
                            Toast.LENGTH_LONG
                        ).show()
                        return@observe
                    }

                    // Jika stok cukup, lanjutkan penyimpanan
                    val barangOut = BarangOut(
                        IdBrgKeluar = 0,
                        id_barang = kodeBarang,
                        ukuran_warna = ukuranWarnaTerpilih.joinToString(","),
                        stok_keluar = stokKeluar,
                        Tgl_Keluar = tanggalKeluar,
                        Hrg_Beli = hargaBeli
                    )
                    val harga_history = HargaUtils.formatHarga(hargaBeli)
                    val history= History(
                        id = 0,
                        waktu = DateUtils.getCurrentDate(),
                        kodeBarang = kodeBarang,
                        stok = stokKeluar.toString(),
                        ukuranWarna = ukuranWarnaTerpilih.joinToString(","),
                        harga = harga_history,
                        jenisData = false
                    )
                    simpanDatabase(barangOut,history)
                    listener?.onBarangKeluarSaved(
                        kodeBarang,
                        namaBarang,
                        hargaBarang,
                        ukuranWarnaTerpilih,
                        stokKeluar,
                        hargaBeli
                    )

                    dismiss()
                }
            }
        }
    }

    private fun simpanDatabase(barangOut: BarangOut,history: History) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                BarangKeluarViewModel.insertBarangKeluar(barangOut)
                BarangKeluarViewModel.insertHistory(history)
                Log.d("BarangKeluar", "Data berhasil disimpan ke database")
            } catch (e: Exception) {
                Log.e("BarangKeluar", "Gagal menyimpan data ke database", e)
            }
        }
    }

    private fun setupRecyclerViewUkuranWarna(warnaBarang: List<String>) {

        val colorNames = resources.getStringArray(R.array.daftar_nama_warna)
        val colorValues = resources.getStringArray(R.array.daftar_warna)

        val colorMap =
            colorNames.indices.associate { index -> colorNames[index] to colorValues[index] }

        val warnaOnly = warnaBarang.map { item ->
            item.split(" ").last().trim() // Ambil bagian terakhir (warna)
        }

        val selectedColorValues = warnaOnly.mapNotNull { colorMap[it] }

        if (warnaBarang.isNotEmpty() && selectedColorValues.isNotEmpty()) {
            colorAdapter.updateColors(warnaBarang, selectedColorValues)
        } else {
            Toast.makeText(
                requireContext(),
                "Tidak ada data warna yang valid",
                Toast.LENGTH_SHORT
            ).show()
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
            kodeBarang: String
        ): BarangKeluarDialogFragment {
            val fragment = BarangKeluarDialogFragment()
            val args = Bundle().apply {
                putString("kodeBarang", kodeBarang)
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