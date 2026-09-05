package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jenil.f1comp.data.local.entity.SprintResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SprintResultDao {

    @Query("SELECT * FROM sprint_result_table WHERE raceId = :targetedRaceId")
    fun getSprintResults(targetedRaceId: String): Flow<List<SprintResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSprintResults(sprintResults: List<SprintResultEntity>)

    @Query("DELETE FROM sprint_result_table WHERE raceId = :raceId")
    suspend fun clearSprintResults(raceId: String)

    @Transaction
    suspend fun refreshSprintResults(
        raceId: String,
        sprintResults: List<SprintResultEntity>
    ){
        clearSprintResults(raceId)
        insertSprintResults(sprintResults)
    }
}