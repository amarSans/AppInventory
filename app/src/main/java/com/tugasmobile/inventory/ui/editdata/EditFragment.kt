package com.tugasmobile.inventory.ui.editdata

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tugasmobile.inventory.databinding.FragmentEditBinding
import com.tugasmobile.inventory.ui.ViewModel
import com.tugasmobile.inventory.ui.uiData.addData


class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val editViewModel = ViewModelProvider(this).get(ViewModel::class.java)

        _binding = FragmentEditBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.fabAddData.setOnClickListener{
            val intent= Intent(requireContext(), addData::class.java)
            startActivity(intent)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}