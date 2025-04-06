package com.tugasmobile.inventory.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tugasmobile.inventory.database.BrgDatabaseHelper
import com.tugasmobile.inventory.ui.main.home.HomeViewModel

class InventoryViewModelFactory private constructor(
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dbHelper = BrgDatabaseHelper.getInstance(application)

        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(application, dbHelper) as T
            }
            // Tambahkan ViewModel lain di sini kalau ada
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: InventoryViewModelFactory? = null

        fun getInstance(application: Application): InventoryViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: InventoryViewModelFactory(application).also { INSTANCE = it }
            }
        }
    }
}