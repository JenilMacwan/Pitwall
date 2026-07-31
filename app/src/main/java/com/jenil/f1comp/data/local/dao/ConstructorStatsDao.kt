package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jenil.f1comp.data.local.entity.ConstructorsStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConstructorStatsDao {

    @Query("SELECT * FROM constructor_stats_table")
    fun getCachedConstructorStats(): Flow<List<ConstructorsStatsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConstructorStats(stats: List<ConstructorsStatsEntity>)

    @Query("DELETE FROM constructor_stats_table")
    suspend fun clearConstructorStats()

    @Transaction
    suspend fun refreshConstructorStats(stats: List<ConstructorsStatsEntity>) {
        clearConstructorStats()
        insertConstructorStats(stats)
    }
}
