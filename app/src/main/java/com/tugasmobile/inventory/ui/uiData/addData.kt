package com.tugasmobile.inventory.ui.uiData

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tugasmobile.inventory.MainActivity
import com.tugasmobile.inventory.R
import com.tugasmobile.inventory.data.Barang
import com.tugasmobile.inventory.databinding.ActivityAddDataBinding

class addData : AppCompatActivity() {
    private val viewModel:addDataViewModel by viewModels()
    private lateinit var binding: ActivityAddDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnSimpanProduk.setOnClickListener{
            val namaProduk=binding.inputNamaProduk.text.toString()
            val stokProduk=binding.inputStokProduk.text.toString().toInt()
            val hargaProduk=binding.inputHargaProduk.text.toString().toDouble()
            val barang = Barang(id = 0, namaProduk = namaProduk, stok = stokProduk, harga = hargaProduk)
            viewModel.insertLaporan(barang)
            Toast.makeText(this,"data berhasil ditambahkan",Toast.LENGTH_SHORT).show()
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}