package com.mohamed.tahiri.termscanguardian.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data")
data class Data(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "text") val text: String?,
    @ColumnInfo(name = "time") val time: Long?,
    @ColumnInfo(name = "Images") val images:String?
)