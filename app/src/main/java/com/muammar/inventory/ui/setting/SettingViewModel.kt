package com.muammar.inventory.ui.setting

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.muammar.inventory.data.SettingData
import com.muammar.inventory.database.BrgDatabaseHelper
import com.muammar.inventory.ui.setting.transferData.exportData
import com.muammar.inventory.ui.setting.transferData.importData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingViewModel (application: Application, private val dbHelper: BrgDatabaseHelper)
    : AndroidViewModel(application) {

    private val _settingData = MutableLiveData<SettingData?>()
    val settingData: LiveData<SettingData?> get() = _settingData

    private val database = dbHelper.writableDatabase

    suspend fun exportDatabase(context: Context) {
        exportData(context, database)
    }

    fun importDatabase(context: Context, zipUri: Uri) {
        importData(context, zipUri,database)
    }
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
    fun saveSetting(settingData: SettingData) {
        viewModelScope.launch(Dispatchers.IO) {
            dbHelper.saveSetting(settingData)
            loadSetting()
        }
    }
    fun ambilSemuaUriDariDatabase():List<String> {
        return dbHelper.ambilSemuaUriDariDatabase()
    }

}