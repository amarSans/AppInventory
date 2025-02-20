package com.tugasmobile.inventory.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tugasmobile.inventory.data.Barang1
import com.tugasmobile.inventory.data.BarangIn
import com.tugasmobile.inventory.data.DataBarangMasuk
import com.tugasmobile.inventory.data.BrgDatabaseHelper
import com.tugasmobile.inventory.data.Stok
import com.tugasmobile.inventory.ui.Barang.BarangMasuk

class ViewModel(application: Application) : AndroidViewModel(application) {
    //private val databaseHelper= BarangDatabaseHelper(application)
    private val databaseHelper= BrgDatabaseHelper(application)
    private val _Data_barangMasukList= MutableLiveData<List<DataBarangMasuk>>()
    val dataBarangMasukList: LiveData<List<DataBarangMasuk>> = _Data_barangMasukList
    private val _currentDataBarangMasuk = MutableLiveData<Barang1?>()
    val currentDataBarangMasuk: LiveData<Barang1?> = _currentDataBarangMasuk
    private val _currentBarang = MutableLiveData<Barang1?>()
    val currentBarang: LiveData<Barang1?> = _currentBarang

    private val _currentStok = MutableLiveData<Stok?>()
    val currentStok: LiveData<Stok?> = _currentStok

    private val _currentBarangIn = MutableLiveData<BarangIn?>()
    val currentBarangIn: LiveData<BarangIn?> = _currentBarangIn


    init {
        loadBarang()
    }
    fun loadBarang(){
        _Data_barangMasukList.value=databaseHelper.getAllBarang()
    }
    fun insertInputBarang(barang:Barang1,stok:Stok,barangIn: BarangIn){
        databaseHelper.insertInputBarang(barang,stok,barangIn)
        loadBarang()
    }

    fun deleteBarang(id: Long) {
        databaseHelper.deleteBarang(id)
        loadBarang()
    }
    fun updateBarang(barang: Barang1, stok: Stok, barangIn: BarangIn){
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