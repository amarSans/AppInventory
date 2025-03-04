package com.tugasmobile.inventory.ui.simpleItem

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tugasmobile.inventory.databinding.FragmentButtonUkuranSheetBinding


class BottonUkuranSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentButtonUkuranSheetBinding? = null
    val binding get() = _binding!!

    interface SizeSelectionListener {
        fun onSizeSelected(selectedSizes: List<String>)
    }
    var listener: SizeSelectionListener? = null
    private var selectedSizes: MutableList<String> = mutableListOf()
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
        private const val ARG_STOK = "stok"

        fun newInstance(selectedSizes: List<String>,stok: Int): BottonUkuranSheet {
            val fragment = BottonUkuranSheet()
            val args = Bundle()
            args.putStringArrayList(ARG_SELECTED_SIZES, ArrayList(selectedSizes))
            args.putInt(ARG_STOK, stok)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedSizes = arguments?.getStringArrayList(ARG_SELECTED_SIZES)?.toMutableList() ?: mutableListOf()
        val stok = arguments?.getInt(ARG_STOK, 0) ?: 0
        setupCheckboxes(stok)
        binding.buttonSimpanUkuran.setOnClickListener {
            listener?.onSizeSelected(selectedSizes)
            dismiss()
        }


    }
    private fun setupCheckboxes(stok: Int) {
        val checkboxes = mutableListOf<CheckBox>()
        var checkedCount = 0

        // Tambahkan semua checkbox ke dalam list
        for (i in 19..44) {
            val checkbox = binding.root.findViewById<CheckBox>(
                resources.getIdentifier("checkbox_$i", "id", requireContext().packageName)
            )
            checkboxes.add(checkbox)
        }

        // Tambahkan checkbox khusus (6, 6.5, 7, dll.)
        checkboxes.addAll(
            listOf(
                binding.checkbox6,
                binding.checkbox65,
                binding.checkbox7,
                binding.checkbox75,
                binding.checkbox8,
                binding.checkbox85,
                binding.checkbox9,
                binding.checkbox95,
                binding.checkbox10,
                binding.checkbox105,
                binding.checkbox11,
                binding.checkbox115
            )
        )

        // Batasi jumlah checkbox yang dapat dipilih
        for (checkbox in checkboxes) {
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (checkedCount < stok) {
                        checkedCount++
                        selectedSizes.add(checkbox.text.toString())
                    } else {
                        checkbox.post {
                            checkbox.isChecked = false // Gunakan post agar tidak memicu listener lagi
                        }
                        Toast.makeText(requireContext(), "Anda hanya dapat memilih $stok ukuran", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    selectedSizes.remove(checkbox.text.toString())
                }
            }
        }

        // Set status checkbox berdasarkan selectedSizes
        for (checkbox in checkboxes) {
            if (selectedSizes.contains(checkbox.text.toString())) {
                checkbox.isChecked = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}