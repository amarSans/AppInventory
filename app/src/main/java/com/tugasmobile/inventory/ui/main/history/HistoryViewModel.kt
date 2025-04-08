package com.tugasmobile.inventory.ui.main.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tugasmobile.inventory.data.History
import com.tugasmobile.inventory.database.BrgDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel(application: Application, private val dbHelper: BrgDatabaseHelper) : AndroidViewModel(application) {
    private val _historyData = MutableLiveData<List<History>>()
    val allHistory: LiveData<List<History>> get() = _historyData
    init {
        loadHistory()
    }
    fun loadHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Ambil data pengaturan dari database
                val history = dbHelper.getAllHistoryItems()

                // Perbarui LiveData di thread utama
                withContext(Dispatchers.Main) {
                    _historyData.value = history
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }
}