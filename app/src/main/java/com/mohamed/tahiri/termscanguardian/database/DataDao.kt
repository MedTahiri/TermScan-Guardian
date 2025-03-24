package com.mohamed.tahiri.termscanguardian.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DataDao {
    @Query("SELECT * FROM data ORDER BY time DESC")
    fun getData(): Flow<List<Data>>

    @Insert
    fun insertData(vararg data: Data)

    @Delete
    fun deleteData(data: Data)

    @Query("SELECT * FROM data WHERE text LIKE '%' || :search || '%' ORDER BY text")
    fun searchData(search: String): Flow<List<Data>>
}