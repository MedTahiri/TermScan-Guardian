package com.mohamed.tahiri.termscanguardian.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataModelViewModel(private val dao: DataDao) : ViewModel() {
    fun getData(): Flow<List<Data>> {
        return dao.getData()
    }

    fun addData(data: Data) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.insertData(data)
            }
        }
    }

    fun deleteData(data: Data) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dao.deleteData(data)
            }
        }
    }

    fun searchData(search: String): Flow<List<Data>> {
        return dao.searchData(search)
    }
}