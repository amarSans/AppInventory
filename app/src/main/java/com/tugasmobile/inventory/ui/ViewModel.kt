package com.tugasmobile.inventory.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tugasmobile.inventory.data.BarangPrototype
import com.tugasmobile.inventory.data.BarangDatabaseHelper

class ViewModel(application: Application) : AndroidViewModel(application) {
    private val databaseHelper= BarangDatabaseHelper(application)
    private val _barangPrototypeList= MutableLiveData<List<BarangPrototype>>()
    val barangPrototypeList: LiveData<List<BarangPrototype>> = _barangPrototypeList
    private val _currentBarangPrototype = MutableLiveData<BarangPrototype?>()
    val currentBarangPrototype: LiveData<BarangPrototype?> = _currentBarangPrototype
    init {
        loadLaporan()
    }
    fun loadLaporan(){
        _barangPrototypeList.value=databaseHelper.getAllLaporan()
    }
    fun insertLaporan(barangPrototype: BarangPrototype){
        databaseHelper.insertLaporan(barangPrototype)
        loadLaporan()
    }
    fun deleteLaporan(id: Long) {
        databaseHelper.deleteLaporan(id)
        loadLaporan()
    }
    fun updateLaporan(barangPrototype: BarangPrototype){
        databaseHelper.updateLaporan(barangPrototype)
        loadLaporan()
    }
    fun setCurrentBarang(id: Long) {
        _currentBarangPrototype.value = databaseHelper.getLaporanById(id)
    }
    fun updateWarna(barangId: Long, newColors: List<String>) {
        databaseHelper.updateWarna(barangId, newColors)
    }
}