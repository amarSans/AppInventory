package com.tugasmobile.inventory.ui.main.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tugasmobile.inventory.data.DataBarangHampirHabisHome
import com.tugasmobile.inventory.data.History
import com.tugasmobile.inventory.database.BrgDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(application: Application, private val dbHelper: BrgDatabaseHelper)
    : AndroidViewModel(application) {
    private val databaseHelper = BrgDatabaseHelper.getInstance(application)

    private val _totalBarang = MutableLiveData<Int>()
    val totalBarang: LiveData<Int> = _totalBarang

    // LiveData untuk stok rendah
    private val _stokRendah = MutableLiveData<Int>()
    val stokRendah: LiveData<Int> = _stokRendah

    private val _dataTertinggi = MutableLiveData<String?>()
    val dataTertinggi: LiveData<String?> = _dataTertinggi

    private val _barangHampirHabis = MutableLiveData<List<DataBarangHampirHabisHome>>()
    val barangHampirHabis: LiveData<List<DataBarangHampirHabisHome>> get() = _barangHampirHabis

    private val _historyTerbaru = MutableLiveData<List<History>>()
    val historyTerbaru: LiveData<List<History>> = _historyTerbaru

    fun loadLastThreeHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = databaseHelper.getLastThreeHistories()
            _historyTerbaru.postValue(data)
        }
    }

    fun loadBarangHampirHabis() {
            val data = dbHelper.getBarangDenganStokTerdekatHabis()
            _barangHampirHabis.value = data
    }

    // Fungsi ambil total barang
    fun getTotalBarang() {
        viewModelScope.launch(Dispatchers.IO) {
            val total = databaseHelper.getTotalBarang() // Buat fungsi ini di DBHelper
            _totalBarang.postValue(total)
        }
    }

    // Fungsi ambil stok rendah
    fun getStokRendah(threshold: Int = 5) {
        viewModelScope.launch(Dispatchers.IO) {
            val total = databaseHelper.getStokRendah(threshold) // Buat juga ini
            _stokRendah.postValue(total)
        }
    }

    fun getDataTertinggi() {
        val hasil = databaseHelper.getBarangStokTertinggi()
        _dataTertinggi.value = hasil
    }
}
