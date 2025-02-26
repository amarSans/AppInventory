package com.tugasmobile.inventory.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tugasmobile.inventory.data.ItemBarang
import com.tugasmobile.inventory.data.BarangIn
import com.tugasmobile.inventory.data.DataBarangMasuk
import com.tugasmobile.inventory.data.BrgDatabaseHelper
import com.tugasmobile.inventory.data.DataSearch
import com.tugasmobile.inventory.data.Stok
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModel(application: Application) : AndroidViewModel(application) {
    //private val databaseHelper= BarangDatabaseHelper(application)
    private val databaseHelper= BrgDatabaseHelper(application)
    private val _Data_barangMasukList= MutableLiveData<List<DataBarangMasuk>>()
    val dataBarangMasukList: LiveData<List<DataBarangMasuk>> = _Data_barangMasukList
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


    init {
        loadBarang()
    }

    fun loadBarang() {
        viewModelScope.launch(Dispatchers.IO) {
            val barangList = databaseHelper.getAllBarang()
            withContext(Dispatchers.Main) {
                _Data_barangMasukList.value = barangList
            }
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

    fun deleteBarang(id: Long) {
        databaseHelper.deleteBarang(id)
        loadBarang()
    }
    fun updateBarang(barang: ItemBarang, stok: Stok, barangIn: BarangIn){
        databaseHelper.updateBarang(barang,stok,barangIn)
        loadBarang()
    }
    fun setCurrentBarang(id: Long) {
        val (barang, stok, barangIn) = databaseHelper.getBarangById(id)
        _currentBarang.value = barang
        _currentStok.value = stok
        _currentBarangIn.value = barangIn
    }

    fun updateWarna(barangId: Long, newColors: List<String>) {
        databaseHelper.updateWarna(barangId, newColors)
    }
}