package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jenil.f1comp.data.local.entity.NextRaceEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface NextRaceDao {
    @Query("SELECT * FROM nextrace_table")
    fun getNextRace(): Flow<NextRaceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNextRace(nextRace: NextRaceEntity)

    @Query("DELETE FROM nextrace_table")
    suspend fun clearNextRace()

    @Transaction
    suspend fun refreshNextRace(nextRace: NextRaceEntity) {
        clearNextRace()
        insertNextRace(nextRace)
    }
}