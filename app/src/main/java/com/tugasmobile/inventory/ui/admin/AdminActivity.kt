package com.tugasmobile.inventory.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.tugasmobile.inventory.databinding.ActivityAdminBinding
import com.tugasmobile.inventory.ui.uiData.addData

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        binding.fabadmin.setOnClickListener {
            val intent= Intent(this, addData::class.java)
            startActivity(intent)
        }

    }
}