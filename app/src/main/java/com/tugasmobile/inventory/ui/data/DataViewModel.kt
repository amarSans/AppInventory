package com.tugasmobile.inventory.ui.data

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tugasmobile.inventory.data.BarangIn
import com.tugasmobile.inventory.data.DataBarangAkses
import com.tugasmobile.inventory.data.History
import com.tugasmobile.inventory.data.ItemBarang
import com.tugasmobile.inventory.data.Stok
import com.tugasmobile.inventory.data.StokUpdate
import com.tugasmobile.inventory.database.BrgDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataViewModel (application: Application, private val dbHelper: BrgDatabaseHelper)
    : AndroidViewModel(application)  {

    private val _currentBarang = MutableLiveData<ItemBarang?>()
    val currentBarang: LiveData<ItemBarang?> = _currentBarang

    private val _currentStok = MutableLiveData<Stok?>()
    val currentStok: LiveData<Stok?> = _currentStok

    private val _currentBarangIn = MutableLiveData<BarangIn?>()
    val currentBarangIn: LiveData<BarangIn?> = _currentBarangIn

    private val _barangExist = MutableLiveData<Boolean>()
    val barangExist: LiveData<Boolean> get() = _barangExist

    private val _Data_barangMasukList= MutableLiveData<List<DataBarangAkses>>()
    val dataBarangAksesList: LiveData<List<DataBarangAkses>> = _Data_barangMasukList


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
    fun updateBarang(barang: ItemBarang, stok: Stok, barangIn: BarangIn){
        dbHelper.updateBarang(barang,stok,barangIn)
        loadBarang()
    }
    fun setCurrentBarang(id: String) {
        val (barang, stok, barangIn) = dbHelper.getBarangById(id)
        _currentBarang.value = barang
        _currentStok.value = stok
        _currentBarangIn.value = barangIn
    }
    fun cekBarangExist(kodeBarang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val exists = dbHelper.isBarangExist(kodeBarang)
            _barangExist.postValue(exists)
        }
    }
    fun updatestok(update: StokUpdate){
        Log.d("UpdateStok", "Dipanggil dengan: $update")
        dbHelper.updateStok(update)
        loadBarang()
    }
    fun cekKodeBarangAda(kode: String): Boolean {
        return dbHelper.cekKodeBarangAda(kode)
    }
    fun insertHistory(history: History) {
        dbHelper.insertHistory(history)
    }
    fun insertInputBarang(barang: ItemBarang, stok: Stok, barangIn: BarangIn){
        dbHelper.insertInputBarang(barang,stok,barangIn)
        loadBarang()
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
    /*fun updateWarna(barangId: String, newColors: List<String>) {
        dbHelper.updateWarna(barangId, newColors)
    }*/
}