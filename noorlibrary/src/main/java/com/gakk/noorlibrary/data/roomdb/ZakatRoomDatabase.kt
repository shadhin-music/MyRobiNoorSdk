package com.gakk.noorlibrary.data.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gakk.noorlibrary.model.zakat.ZakatDataModel
import kotlinx.coroutines.CoroutineScope

/**
 * This is the backend. The database. This used to be done by the OpenHelper.
 * The fact that this has very few comments emphasizes its coolness.
 */
@Database(entities = [ZakatDataModel::class], version = 1)
abstract class ZakatRoomDatabase : RoomDatabase() {

    abstract fun zakatDao(): ZakatDao

    companion object {
        @Volatile
        private var INSTANCE: ZakatRoomDatabase? = null

        fun getDatabase(
            context: Context
        ): ZakatRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZakatRoomDatabase::class.java,
                    "zakat_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    // .addCallback(ZakatDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
