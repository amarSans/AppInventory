package com.tugasmobile.inventory.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tugasmobile.inventory.data.DataSearch
import com.tugasmobile.inventory.data.SearchData
import com.tugasmobile.inventory.database.BrgDatabaseHelper

class SearchViewModel(application: Application, private val dbHelper: BrgDatabaseHelper)
    : AndroidViewModel(application)  {
    private val _searchResults = MutableLiveData<List<SearchData>>()
    val searchResults: LiveData<List<SearchData>> get() = _searchResults

    private val _dataSearch = MutableLiveData<List<DataSearch>>()
    val dataSearch:LiveData<List<DataSearch>> =_dataSearch


    fun search(keyword: String) {
        // Ganti dengan fungsi yang sesuai
        _searchResults.value = dbHelper.searchFlexible(keyword)
    }
}