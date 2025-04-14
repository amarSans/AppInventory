package com.muammar.inventory.ui.data

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.muammar.inventory.R
import com.muammar.inventory.ui.InventoryViewModelFactory

class HomeDataFragment : Fragment() {
    private val dataViewModel: DataViewModel by viewModels {
        InventoryViewModelFactory.getInstance(requireActivity().application)
    }

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
            val kodeBarang = edtKode.text.toString().trim()
            prosesKodeBarang(kodeBarang)
        }
        return view
    }
    private fun prosesKodeBarang(kodeInput: String) {
        if (kodeInput.isEmpty()) {
            val kodeUnik = "SND" + (1000..9999).random()
            // Langsung arahkan ke tambah barang tanpa cek ulang
            cekDanPindahFragment(kodeUnik)
        } else {
            // Cek apakah barang sudah ada
            cekDanPindahFragment(kodeInput)
        }
    }

    private fun cekDanPindahFragment(kodeBarang: String) {
        dataViewModel.cekBarangExist(kodeBarang){ hasilPencarian ->
            if (hasilPencarian) {
                pindahKeFragment(TambahStokFragment.newInstance(kodeBarang))
            } else {
                pindahKeFragment(TambahBarangFragment.newInstance(kodeBarang))

            }
        }
    }



    private fun pindahKeFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }



}
