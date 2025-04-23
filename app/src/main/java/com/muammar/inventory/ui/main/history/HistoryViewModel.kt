package com.muammar.inventory.ui.main.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.muammar.inventory.data.History
import com.muammar.inventory.database.BrgDatabaseHelper
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

                val history = dbHelper.getAllHistoryItems()

                withContext(Dispatchers.Main) {
                    _historyData.value = history
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }
}