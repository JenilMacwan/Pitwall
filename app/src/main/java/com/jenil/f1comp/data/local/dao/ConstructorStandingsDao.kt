package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jenil.f1comp.data.local.entity.ConstructorStandingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConstructorStandingsDao {
    @Query("SELECT * FROM constructors_standings ORDER BY position ASC")
    fun getConstructorStandings(): Flow<List<ConstructorStandingsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConstructorStandings(standings: List<ConstructorStandingsEntity>)

    @Query("DELETE FROM constructors_standings")
    suspend fun clearConstructorStandings()

    @Transaction
    suspend fun refreshConstructorStandings(standings: List<ConstructorStandingsEntity>) {
        clearConstructorStandings()
        insertConstructorStandings(standings)
    }
}