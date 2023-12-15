package com.dietinapp.retrofit.data.paging

import androidx.paging.PagingSource
import androidx.room.*
import com.dietinapp.retrofit.response.HistoryItem

@Dao
interface HistoriesPagingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistories(histories: List<HistoryItem>)

    @Query("SELECT * FROM histories WHERE " +
            "foodName LIKE '%' || :foodName || '%' " +
            "AND createdAt LIKE '%' || :createdAt || '%' ")
    fun getAllHistories(foodName: String, createdAt: String): PagingSource<Int, HistoryItem>

    @Query("DELETE FROM histories")
    fun deleteAll()
}