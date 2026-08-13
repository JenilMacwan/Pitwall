package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jenil.f1comp.data.local.entity.RaceResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RaceResultDao {

    @Query(value = "SELECT * FROM raceresult_table WHERE raceId = :targetedRaceId")
    fun getRaceResults(targetedRaceId: String): Flow<List<RaceResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRaceResults(raceResults: List<RaceResultEntity>)

    @Query("DELETE FROM raceresult_table WHERE raceId = :raceId")
    suspend fun clearRaceResult(raceId: String)

    @Transaction
    suspend fun refreshRaceResult(raceId: String, raceResults: List<RaceResultEntity>) {
        clearRaceResult(raceId)
        insertRaceResults(raceResults)
    }
}