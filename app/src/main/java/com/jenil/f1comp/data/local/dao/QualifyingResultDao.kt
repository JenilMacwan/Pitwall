package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jenil.f1comp.data.local.entity.QualifyingResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QualifyingResultDao {
    @Query("SELECT * FROM qualifying_result_table WHERE raceId = :targetedRaceId")
    fun getQualifyingResults(targetedRaceId: String): Flow<List<QualifyingResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQualifyingResults(qualifyingResults: List<QualifyingResultEntity>)

    @Query("DELETE FROM qualifying_result_table WHERE raceId = :raceId")
    suspend fun clearQualifyingResults(raceId: String)

    @Transaction
    suspend fun refreshQualifyingResults(
        raceId: String,
        qualifyingResults: List<QualifyingResultEntity>
    ) {
        clearQualifyingResults(raceId)
        insertQualifyingResults(qualifyingResults)
    }
}

