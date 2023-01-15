package com.gakk.noorlibrary.data.roomdb

import androidx.room.*
import com.gakk.noorlibrary.model.zakat.ZakatDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ZakatDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(data: ZakatDataModel)

    @Query("SELECT * FROM zakat_table")
    fun getAllData(): Flow<List<ZakatDataModel>>

    @Delete
    suspend fun deleteData(data: ZakatDataModel)
}