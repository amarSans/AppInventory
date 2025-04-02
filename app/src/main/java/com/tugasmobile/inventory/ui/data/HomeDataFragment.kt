package com.tugasmobile.inventory.ui.data

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.ui.ViewModel

class HomeDataFragment : Fragment() {
    private val dataViewModel: ViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_data, container, false)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.dark_background)

        val edtKode = view.findViewById<EditText>(R.id.edt_kode)
        val btnCekKode = view.findViewById<Button>(R.id.btn_kode)

        btnCekKode.setOnClickListener {
            var kodeBarang = edtKode.text.toString().trim()
            

            if (kodeBarang.isEmpty()) {
                generateKodeBarang { kodeUnik ->
                    kodeBarang = kodeUnik
                    cekDanPindahFragment(kodeBarang)
                }
            } else {
                cekDanPindahFragment(kodeBarang)
            }
        }

        return view
    }

    // === GENERATE KODE BARANG ===
    private fun generateKodeBarang(callback: (String) -> Unit) {
        val kode = "SND" + (1000..9999).random()

        dataViewModel.cekBarangExist(kode)
        dataViewModel.barangExist.observe(viewLifecycleOwner) { hasilPencarian ->

            if (hasilPencarian) {
                generateKodeBarang(callback)
            } else {
                callback(kode)
            }
        }
    }

    // === CEK DAN PINDAH FRAGMENT ===
    private fun cekDanPindahFragment(kodeBarang: String) {
        dataViewModel.cekBarangExist(kodeBarang)
        dataViewModel.barangExist.observe(viewLifecycleOwner) { hasilPencarian ->
            dataViewModel.barangExist.removeObservers(viewLifecycleOwner)
            if (hasilPencarian) {
                pindahKeFragment(TambahStokFragment.newInstance(kodeBarang))
            } else {
                pindahKeFragment(TambahBarangFragment.newInstance(kodeBarang))
            }
        }
    }

    // === PINDAH FRAGMENT ===
    private fun pindahKeFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}
