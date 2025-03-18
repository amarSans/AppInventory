package com.tugasmobile.inventory.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tugasmobile.inventory.data.BarangIn
import com.tugasmobile.inventory.data.BarangOut
import com.tugasmobile.inventory.data.DataBarangAkses
import com.tugasmobile.inventory.data.DataSearch
import com.tugasmobile.inventory.data.History
import com.tugasmobile.inventory.data.ItemBarang
import com.tugasmobile.inventory.data.ItemNotifikasi
import com.tugasmobile.inventory.data.SettingData
import com.tugasmobile.inventory.data.Stok
import com.tugasmobile.inventory.database.BrgDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModel(application: Application) : AndroidViewModel(application) {

    private val databaseHelper= BrgDatabaseHelper.getInstance(application)

    private val _Data_barangMasukList= MutableLiveData<List<DataBarangAkses>>()
    val dataBarangAksesList: LiveData<List<DataBarangAkses>> = _Data_barangMasukList

    private val _dataSearch = MutableLiveData<List<DataSearch>>()
    val dataSearch:LiveData<List<DataSearch>> =_dataSearch

    private val _currentDataBarangMasuk = MutableLiveData<ItemBarang?>()
    val currentDataBarangMasuk: LiveData<ItemBarang?> = _currentDataBarangMasuk

    private val _currentBarang = MutableLiveData<ItemBarang?>()
    val currentBarang: LiveData<ItemBarang?> = _currentBarang

    private val _currentStok = MutableLiveData<Stok?>()
    val currentStok: LiveData<Stok?> = _currentStok

    private val _currentBarangIn = MutableLiveData<BarangIn?>()
    val currentBarangIn: LiveData<BarangIn?> = _currentBarangIn

    private val _insertResult = MutableLiveData<BarangOut?>()
    val insertResult: LiveData<BarangOut?> = _insertResult

    private val _lowStockItems = MutableLiveData<List<ItemNotifikasi>>()
    val lowStockItems: LiveData<List<ItemNotifikasi>> = _lowStockItems

    private val _settingData = MutableLiveData<SettingData?>()
    val settingData: LiveData<SettingData?> get() = _settingData

    private val _stokData = MutableLiveData<List<ItemNotifikasi>>()
    val stokData: LiveData<List<ItemNotifikasi>> get() = _stokData

    private val _historyData = MutableLiveData<List<History>>()
    val allHistory: LiveData<List<History>> get() = _historyData

    init {
        loadBarang()
        loadSetting()
        loadHistory()
    }
    fun loadHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Ambil data pengaturan dari database
                val history = databaseHelper.getAllHistoryItems()

                // Perbarui LiveData di thread utama
                withContext(Dispatchers.Main) {
                    _historyData.value = history
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ViewModel", "Gagal memuat history: ${e.message}", e)
            }
        }
    }

    fun loadSetting() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Ambil data pengaturan dari database
                val setting = databaseHelper.getSetting()

                // Perbarui LiveData di thread utama
                withContext(Dispatchers.Main) {
                    _settingData.value = setting
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadBarang() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val barangList = databaseHelper.getAllBarang()
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


    fun insertBarangKeluar(barangOut: BarangOut) {
        viewModelScope.launch(Dispatchers.IO) {
           databaseHelper.insertBarangKeluar(barangOut)
            loadBarang()
        }
    }

    fun insertInputBarang(barang:ItemBarang, stok:Stok, barangIn: BarangIn){
        databaseHelper.insertInputBarang(barang,stok,barangIn)
        loadBarang()
    }
    fun searchBarang(query: String) {
        val results = databaseHelper.searchBarang(query)
        _dataSearch.value = results
    }

    fun deleteBarang(id: String) {
        databaseHelper.deleteBarang(id)
        loadBarang()
    }
    fun updateBarang(barang: ItemBarang, stok: Stok, barangIn: BarangIn){
        databaseHelper.updateBarang(barang,stok,barangIn)
        loadBarang()
    }
    fun setCurrentBarang(id: String) {
        val (barang, stok, barangIn) = databaseHelper.getBarangById(id)
        _currentBarang.value = barang
        _currentStok.value = stok
        _currentBarangIn.value = barangIn
    }
    fun updateWarna(barangId: String, newColors: List<String>) {
        databaseHelper.updateWarna(barangId, newColors)
    }

    fun cekKodeBarangAda(kode: String): Boolean {
        return databaseHelper.cekKodeBarangAda(kode)
    }


    fun saveSetting(settingData: SettingData) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseHelper.saveSetting(settingData)
            loadSetting()
        }
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
    fun insertHistory(history: History) {
        databaseHelper.insertHistory(history)
    }

}