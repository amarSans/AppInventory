package com.muammar.inventory.ui.main.barang

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.muammar.inventory.data.BarangMasukItem
import com.muammar.inventory.data.BarangOut
import com.muammar.inventory.data.DataBarangAkses
import com.muammar.inventory.data.DataSearch
import com.muammar.inventory.data.History
import com.muammar.inventory.data.ItemBarang
import com.muammar.inventory.data.Stok
import com.muammar.inventory.database.BrgDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BarangViewModel(application: Application, private val dbHelper: BrgDatabaseHelper) : AndroidViewModel(application){


    private val _Data_barangMasukList= MutableLiveData<List<DataBarangAkses>>()
    val dataBarangAksesList: LiveData<List<DataBarangAkses>> = _Data_barangMasukList

    private val _dataSearch = MutableLiveData<List<DataSearch>>()
    val dataSearch:LiveData<List<DataSearch>> =_dataSearch

    private val _currentBarang = MutableLiveData<ItemBarang?>()
    val currentBarang: LiveData<ItemBarang?> = _currentBarang

    private val _currentStok = MutableLiveData<Stok?>()
    val currentStok: LiveData<Stok?> = _currentStok

    private val _currentBarangMasukItem = MutableLiveData<BarangMasukItem?>()
    val currentBarangMasukItem: LiveData<BarangMasukItem?> = _currentBarangMasukItem

    init {
        loadBarang()
    }
    fun loadBarang() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val barangList = dbHelper.getAllBarang()
                withContext(Dispatchers.Main) {
                    _Data_barangMasukList.value = barangList
                    val lowStockCount = countLowStockItems()
                    saveLowStockCountToSharedPreferences(getApplication(), lowStockCount)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun countLowStockItems(): Int {
        val barangList = _Data_barangMasukList.value ?: return 0
        return barangList.count { it.stok <= 2 }
    }
    private fun saveLowStockCountToSharedPreferences(context: Context, count: Int) {
        val sharedPreferences = context.getSharedPreferences("StokPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("low_stock_count", count)
        editor.apply()
    }
    fun searchBarang(query: String) {
        val results = dbHelper.searchBarang(query)
        _dataSearch.value = results
    }
    fun setCurrentBarang(id: String) {
        val (barang, stok, barangIn) = dbHelper.getBarangById(id)
        _currentBarang.value = barang
        _currentStok.value = stok
        _currentBarangMasukItem.value = barangIn
    }
    fun insertBarangKeluar(barangOut: BarangOut) {
        viewModelScope.launch(Dispatchers.IO) {
            dbHelper.insertBarangKeluar(barangOut)
            loadBarang()
        }
    }
    fun insertHistory(history: History) {
        dbHelper.insertHistory(history)
    }
}