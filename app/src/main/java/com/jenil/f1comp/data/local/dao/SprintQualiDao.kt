package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jenil.f1comp.data.local.entity.SprintQualifyingResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SprintQualiDao {
    @Query("SELECT * FROM sprint_quali_result_table WHERE raceId = :targetedRaceId")
    fun getSprintQualifyingResults(targetedRaceId: String): Flow<List<SprintQualifyingResultEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSprintQualifyingResults(sprintQualifyingResults: List<SprintQualifyingResultEntity>)
    @Query("DELETE FROM sprint_quali_result_table WHERE raceId = :raceId")
    suspend fun clearSprintQualifyingResults(raceId: String)

    @Transaction
    suspend fun refreshSprintQualifyingResults(
        raceId: String,
        sprintQualifyingResults: List<SprintQualifyingResultEntity>
    ){
        clearSprintQualifyingResults(raceId)
        insertSprintQualifyingResults(sprintQualifyingResults)
    }
}