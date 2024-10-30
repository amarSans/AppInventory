package com.tugasmobile.inventory.ui.uiData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tugasmobile.inventory.databinding.FragmentButtonUkuranSheetBinding


class BottonUkuranSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentButtonUkuranSheetBinding? = null
    val binding get() = _binding!!

    interface SizeSelectionListener {
        fun onSizeSelected(selectedSizes: List<String>)
    }
    var listener: SizeSelectionListener? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentButtonUkuranSheetBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSimpanUkuran.setOnClickListener {
            // Mengambil ukuran yang dipilih dari setiap checkbox
            val selectedSizes = mutableListOf<String>()
            for (i in 19..44) {
                val checkbox = binding.root.findViewById<CheckBox>(resources.getIdentifier("checkbox$i", "id",  requireContext().packageName))
                if (checkbox.isChecked) selectedSizes.add(checkbox.text.toString())
            }
            if (binding.checkbox6.isChecked) selectedSizes.add(binding.checkbox6.text.toString())
            if (binding.checkbox65.isChecked) selectedSizes.add(binding.checkbox65.text.toString())
            if (binding.checkbox7.isChecked) selectedSizes.add(binding.checkbox7.text.toString())
            if (binding.checkbox75.isChecked) selectedSizes.add(binding.checkbox75.text.toString())
            if (binding.checkbox8.isChecked) selectedSizes.add(binding.checkbox8.text.toString())
            if (binding.checkbox85.isChecked) selectedSizes.add(binding.checkbox85.text.toString())
            if (binding.checkbox9.isChecked) selectedSizes.add(binding.checkbox9.text.toString())
            if (binding.checkbox95.isChecked) selectedSizes.add(binding.checkbox95.text.toString())
            if (binding.checkbox10.isChecked) selectedSizes.add(binding.checkbox10.text.toString())
            if (binding.checkbox105.isChecked) selectedSizes.add(binding.checkbox105.text.toString())
            if (binding.checkbox11.isChecked) selectedSizes.add(binding.checkbox11.text.toString())
            if (binding.checkbox115.isChecked) selectedSizes.add(binding.checkbox115.text.toString())

            // Mengirimkan data yang dipilih ke Activity melalui listener
            listener?.onSizeSelected(selectedSizes)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}