package com.gakk.noorlibrary.data.roomdb

import androidx.annotation.WorkerThread
import com.gakk.noorlibrary.model.zakat.ZakatDataModel
import kotlinx.coroutines.flow.Flow

class RoomRepository(private val zakatDao: ZakatDao) {

    val allDataZakat: Flow<List<ZakatDataModel>> = zakatDao.getAllData()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(dataModel: ZakatDataModel) {
        zakatDao.insert(dataModel)
    }

    @WorkerThread
    suspend fun delete(dataModel: ZakatDataModel) {
        zakatDao.deleteData(dataModel)
    }
}