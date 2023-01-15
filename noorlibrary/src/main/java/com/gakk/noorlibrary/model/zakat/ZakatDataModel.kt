package com.gakk.noorlibrary.model.zakat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zakat_table")
data class ZakatDataModel(

    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "asset") val asset: String?,
    @ColumnInfo(name = "zakat") val zakat: String?
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var Id: Int? = null
}
