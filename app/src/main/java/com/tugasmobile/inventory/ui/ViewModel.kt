package com.tugasmobile.inventory.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tugasmobile.inventory.data.ItemBarang
import com.tugasmobile.inventory.data.BarangIn
import com.tugasmobile.inventory.data.BarangOut
import com.tugasmobile.inventory.data.DataBarangMasuk
import com.tugasmobile.inventory.data.BrgDatabaseHelper
import com.tugasmobile.inventory.data.DataSearch
import com.tugasmobile.inventory.data.Stok
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModel(application: Application) : AndroidViewModel(application) {

    private val databaseHelper= BrgDatabaseHelper.getInstance(application)

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

    private val _insertResult = MutableLiveData<BarangOut?>()
    val insertResult: LiveData<BarangOut?> = _insertResult


    init {
        loadBarang()
    }

    fun loadBarang() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val barangList = databaseHelper.getAllBarang()
                withContext(Dispatchers.Main) {
                    _Data_barangMasukList.value = barangList
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

}