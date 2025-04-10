package com.tugasmobile.inventory.ui.main.monitoring

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tugasmobile.inventory.data.BarangMonitor
import com.tugasmobile.inventory.database.BrgDatabaseHelper
import com.tugasmobile.inventory.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MonitoringViewModel(application: Application, private val dbHelper: BrgDatabaseHelper)
    : AndroidViewModel(application) {

    private val _monitoringList = MutableLiveData<List<BarangMonitor>>() // data asli
    val monitoringList: LiveData<List<BarangMonitor>> get() = _monitoringList

    private val _filterOption = MutableLiveData<String>("Terbaru") // default
    val filterOption: LiveData<String> get() = _filterOption

    private val _filteredList = MutableLiveData<List<BarangMonitor>>() // hasil filter
    val filteredList: LiveData<List<BarangMonitor>> get() = _filteredList

    private var allBarang: List<BarangMonitor> = emptyList()

    init {
        loadBarangTertingal()
    }
    fun loadBarangTertingal() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = dbHelper.getBarangTertinggal(dbHelper.readableDatabase)
            allBarang = result
            _filteredList.postValue(result) // Default: tampilkan semua
        }
    }

    fun setFilter(filter: String) {
        val filtered = when (filter) {
            "Terbaru" -> allBarang.sortedByDescending {
                it.lastUpdate?.let { date -> DateUtils.getDaysSince(date)<=30 }
            }
            "30 Hari" -> allBarang.filter {
                it.lastUpdate?.let { d -> DateUtils.getDaysSince(d) in 30..59 } == true
            }
            "60 Hari" -> allBarang.filter {
                it.lastUpdate?.let { d -> DateUtils.getDaysSince(d) in 60..364 } == true
            }
            "1 Tahun Lebih" -> allBarang.filter {
                it.lastUpdate?.let { d -> DateUtils.getDaysSince(d) > 365 } == true
            }
            else -> allBarang
        }

        _filteredList.value = filtered
    }
}
