package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jenil.f1comp.data.local.entity.DriverStandingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverStandingsDao {
    @Query("SELECT * FROM driver_standings ORDER BY position ASC")
    fun getDriverStandings(): Flow<List<DriverStandingsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriverStandings(standings: List<DriverStandingsEntity>)

    @Query("DELETE FROM driver_standings")
    suspend fun clearDriverStandings()

    @Transaction
    suspend fun refreshDriverStandings(standings: List<DriverStandingsEntity>) {
        clearDriverStandings()
        insertDriverStandings(standings)
    }
}