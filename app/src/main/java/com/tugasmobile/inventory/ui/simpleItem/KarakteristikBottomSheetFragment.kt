package com.tugasmobile.inventory.ui.simpleItem

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.adapter.KarakteristikAdapter
import com.tugasmobile.inventory.databinding.FragmentKarakteristikBottomSheetBinding

class KarakteristikBottomSheetFragment(
    private val selectedItems: MutableSet<String>,
    private val onSelectionChanged: (MutableSet<String>) -> Unit
) : BottomSheetDialogFragment() {
    private lateinit var adapter: KarakteristikAdapter
    private lateinit var binding: FragmentKarakteristikBottomSheetBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding= FragmentKarakteristikBottomSheetBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupButton()
    }

    private fun setupRecyclerView() {
        val karakteristikList = resources.getStringArray(R.array.karakteristik_barang).toList()
        adapter = KarakteristikAdapter(karakteristikList, selectedItems) { updatedItems ->
            selectedItems.clear()
            selectedItems.addAll(updatedItems)
        }
        binding.recyclerView.adapter = adapter
        val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW // Item ditampilkan dalam baris
            justifyContent = JustifyContent.FLEX_START // Item berbaris dari kiri ke kanan
        }

        binding.recyclerView.layoutManager = flexboxLayoutManager
    }
    private fun setupButton() {
        binding.btnSimpan.setOnClickListener {
            Log.d("KarakteristikBottomSheet", "Selected Items Sebelum Dikirim: $selectedItems")
            onSelectionChanged(HashSet(selectedItems)) // 🔥 Kirim hasil ke Fragment utama
            dismiss()
        }
    }
}
