package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jenil.f1comp.data.local.entity.NewsEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface NewsDao {
    @Query("SELECT * FROM news_table ORDER BY cachedAt DESC")
    fun getCachedNews(): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: List<NewsEntity>)

    @Query("DELETE FROM news_table")
    suspend fun deleteAllNews()

    @Query("DELETE FROM news_table WHERE cachedAt < :threshold")
    suspend fun deleteOldNews(threshold: Long)

    @Transaction
    suspend fun refreshNews(news: List<NewsEntity>) {
        deleteAllNews()
        insertNews(news)
    }
}