package com.muammar.inventory.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.muammar.inventory.data.SettingData
import com.muammar.inventory.database.BrgDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application, private val dbHelper: BrgDatabaseHelper)
    : AndroidViewModel(application) {

    private val _settingData = MutableLiveData<SettingData?>()
    val settingData: LiveData<SettingData?> get() = _settingData

    init {
        loadSetting()
    }
    fun loadSetting() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val setting = dbHelper.getSetting()

                withContext(Dispatchers.Main) {
                    _settingData.value = setting
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}