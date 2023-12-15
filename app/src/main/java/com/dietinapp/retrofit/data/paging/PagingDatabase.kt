package com.dietinapp.retrofit.data.paging

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dietinapp.retrofit.response.HistoryItem

@Database(
    entities = [HistoryItem::class, RemoteKeys::class], version = 1, exportSchema = false
)
abstract class HistoriesPagingDatabase : RoomDatabase() {

    abstract fun historiesPagingDao(): HistoriesPagingDao

    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: HistoriesPagingDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): HistoriesPagingDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    HistoriesPagingDatabase::class.java,
                    "histories_database"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}