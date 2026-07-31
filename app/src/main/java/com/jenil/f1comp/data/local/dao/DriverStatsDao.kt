package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jenil.f1comp.data.local.entity.DriverStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverStatsDao {

    @Query(value = "SELECT * FROM driver_stats_table")
    fun getCachedDriverStats(): Flow<List<DriverStatsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriverStats(stats: List<DriverStatsEntity>)

    @Query("DELETE FROM driver_stats_table")
    suspend fun clearDriverStats()

    @Transaction
    suspend fun refreshDriverStats(stats: List<DriverStatsEntity>) {
        clearDriverStats()
        insertDriverStats(stats)
    }

}