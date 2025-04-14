package com.muammar.inventory.ui.editdata

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.muammar.inventory.data.BarangIn
import com.muammar.inventory.data.DataBarangAkses
import com.muammar.inventory.data.History
import com.muammar.inventory.data.ItemBarang
import com.muammar.inventory.data.Stok
import com.muammar.inventory.database.BrgDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel (application: Application, private val dbHelper: BrgDatabaseHelper)
    : AndroidViewModel(application)  {

    private val _currentBarang = MutableLiveData<ItemBarang?>()
    val currentBarang: LiveData<ItemBarang?> = _currentBarang

    private val _currentStok = MutableLiveData<Stok?>()
    val currentStok: LiveData<Stok?> = _currentStok

    private val _currentBarangIn = MutableLiveData<BarangIn?>()
    val currentBarangIn: LiveData<BarangIn?> = _currentBarangIn

    private val _Data_barangMasukList= MutableLiveData<List<DataBarangAkses>>()
    val dataBarangAksesList: LiveData<List<DataBarangAkses>> = _Data_barangMasukList

    private val _currentDataBarangMasuk = MutableLiveData<ItemBarang?>()
    val currentDataBarangMasuk: LiveData<ItemBarang?> = _currentDataBarangMasuk


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

    fun setCurrentBarang(id: String) {
        val (barang, stok, barangIn) = dbHelper.getBarangById(id)
        _currentBarang.value = barang
        _currentStok.value = stok
        _currentBarangIn.value = barangIn
    }

    fun deleteBarang(id: String) {
        dbHelper.deleteBarang(id)
        loadBarang()
    }

    fun insertHistory(history: History) {
        dbHelper.insertHistory(history)
    }
    private fun saveLowStockCountToSharedPreferences(context: Context, count: Int) {
        val sharedPreferences = context.getSharedPreferences("StokPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("low_stock_count", count)
        editor.apply()
    }
    private fun countLowStockItems(): Int {
        val barangList = _Data_barangMasukList.value ?: return 0
        return barangList.count { it.stok <= 2 }
    }
}