package com.muammar.inventory.ui.main.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.muammar.inventory.data.DataBarangHampirHabisHome
import com.muammar.inventory.data.History
import com.muammar.inventory.data.SearchData
import com.muammar.inventory.database.BrgDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application,  private val dbHelper: BrgDatabaseHelper)
    : AndroidViewModel(application) {

    private val _totalBarang = MutableLiveData<Int>()
    val totalBarang: LiveData<Int> = _totalBarang


    private val _stokRendah = MutableLiveData<Int>()
    val stokRendah: LiveData<Int> = _stokRendah

    private val _dataTertinggi = MutableLiveData<String?>()
    val dataTertinggi: LiveData<String?> = _dataTertinggi

    private val _barangHampirHabis = MutableLiveData<List<DataBarangHampirHabisHome>>()
    val barangHampirHabis: LiveData<List<DataBarangHampirHabisHome>> get() = _barangHampirHabis

    private val _historyTerbaru = MutableLiveData<List<History>>()
    val historyTerbaru: LiveData<List<History>> = _historyTerbaru

    private val _searchResults = MutableLiveData<List<SearchData>>()
    val searchResults: LiveData<List<SearchData>> = _searchResults

    fun loadLastThreeHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = dbHelper.getLastThreeHistories()
            _historyTerbaru.postValue(data)
        }
    }

    fun loadBarangHampirHabis() {
            val data = dbHelper.getBarangDenganStokTerdekatHabis()
            _barangHampirHabis.value = data
    }


    fun getTotalBarang() {
        viewModelScope.launch(Dispatchers.IO) {
            val total = dbHelper.getTotalBarang()
            _totalBarang.postValue(total)
        }
    }


    fun getStokRendah() {
        viewModelScope.launch(Dispatchers.IO) {
            val total = dbHelper.getStokRendah()
            _stokRendah.postValue(total)
        }
    }

    fun getDataTertinggi() {
        val hasil = dbHelper.getBarangStokTertinggi()
        _dataTertinggi.value = hasil
    }

    fun search(query: String) {
        viewModelScope.launch {
            val results = dbHelper.searchFlexible(query)
            _searchResults.postValue(results)
        }
    }
}
