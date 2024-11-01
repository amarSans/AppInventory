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
    private var selectedSizes: List<String> = emptyList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentButtonUkuranSheetBinding.inflate(inflater, container, false)
        return binding.root
    }
    companion object {
        const val TAG = "ModalBottomSheet"
        private const val ARG_SELECTED_SIZES = "selected_sizes"

        fun newInstance(selectedSizes: List<String>): BottonUkuranSheet {
            val fragment = BottonUkuranSheet()
            val args = Bundle()
            args.putStringArrayList(ARG_SELECTED_SIZES, ArrayList(selectedSizes))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedSizes = arguments?.getStringArrayList(ARG_SELECTED_SIZES) ?: emptyList()
        for (i in 19..44) {
            val checkbox = binding.root.findViewById<CheckBox>(
                resources.getIdentifier("checkbox_$i", "id", requireContext().packageName)
            )
            if (selectedSizes.contains(checkbox.text.toString())) {
                checkbox.isChecked = true
            }
        }

        if (selectedSizes.contains(binding.checkbox6.text.toString())) binding.checkbox6.isChecked = true
        if (selectedSizes.contains(binding.checkbox65.text.toString())) binding.checkbox65.isChecked = true
        if (selectedSizes.contains(binding.checkbox7.text.toString())) binding.checkbox7.isChecked = true
        if (selectedSizes.contains(binding.checkbox75.text.toString())) binding.checkbox75.isChecked = true
        if (selectedSizes.contains(binding.checkbox8.text.toString())) binding.checkbox8.isChecked = true
        if (selectedSizes.contains(binding.checkbox85.text.toString())) binding.checkbox85.isChecked = true
        if (selectedSizes.contains(binding.checkbox9.text.toString())) binding.checkbox9.isChecked = true
        if (selectedSizes.contains(binding.checkbox95.text.toString())) binding.checkbox95.isChecked = true
        if (selectedSizes.contains(binding.checkbox10.text.toString())) binding.checkbox10.isChecked = true
        if (selectedSizes.contains(binding.checkbox105.text.toString())) binding.checkbox105.isChecked = true
        if (selectedSizes.contains(binding.checkbox11.text.toString())) binding.checkbox11.isChecked = true
        if (selectedSizes.contains(binding.checkbox115.text.toString())) binding.checkbox115.isChecked = true

        binding.buttonSimpanUkuran.setOnClickListener {
            // Mengambil ukuran yang dipilih dari setiap checkbox
            val selectedSizes = mutableListOf<String>()
            for (i in 19..44) {
                val checkbox = binding.root.findViewById<CheckBox>(resources.getIdentifier("checkbox_$i", "id",  requireContext().packageName))
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

}