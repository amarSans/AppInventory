package com.tugasmobile.inventory.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tugasmobile.inventory.database.BrgDatabaseHelper
import com.tugasmobile.inventory.ui.data.DataViewModel
import com.tugasmobile.inventory.ui.editdata.DetailViewModel
import com.tugasmobile.inventory.ui.main.MainViewModel
import com.tugasmobile.inventory.ui.main.barang.BarangViewModel
import com.tugasmobile.inventory.ui.main.history.HistoryViewModel
import com.tugasmobile.inventory.ui.main.home.HomeViewModel
import com.tugasmobile.inventory.ui.main.monitoring.MonitoringViewModel
import com.tugasmobile.inventory.ui.setting.SettingViewModel
import com.tugasmobile.inventory.ui.search.SearchViewModel

class InventoryViewModelFactory private constructor(
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dbHelper = BrgDatabaseHelper.getInstance(application)

        return when (modelClass) {
            MainViewModel::class.java -> MainViewModel(application, dbHelper) as T
            HomeViewModel::class.java -> HomeViewModel(application, dbHelper) as T
            BarangViewModel::class.java -> BarangViewModel(application, dbHelper) as T
            HistoryViewModel::class.java -> HistoryViewModel(application, dbHelper) as T
            SearchViewModel::class.java -> SearchViewModel(application, dbHelper) as T
            DetailViewModel::class.java -> DetailViewModel(application, dbHelper) as T
            DataViewModel::class.java -> DataViewModel(application, dbHelper) as T
            MonitoringViewModel::class.java -> MonitoringViewModel(application, dbHelper) as T
            SettingViewModel::class.java -> SettingViewModel(application, dbHelper) as T
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