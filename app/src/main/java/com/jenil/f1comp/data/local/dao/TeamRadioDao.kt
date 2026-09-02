package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jenil.f1comp.data.local.entity.TeamRadioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamRadioDao {

    @Query("SELECT * FROM team_radio_table ORDER BY timestamp DESC")
    fun getAllTeamRadio(): Flow<List<TeamRadioEntity>>

    @Query("SELECT * FROM team_radio_table WHERE driverCode = :driverCode ORDER BY timestamp DESC")
    fun getTeamRadioByDriver(driverCode: String): Flow<List<TeamRadioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamRadios(radios: List<TeamRadioEntity>)

    @Query("DELETE FROM team_radio_table")
    suspend fun deleteAllTeamRadio()

    @Transaction
    suspend fun refreshTeamRadios(radios: List<TeamRadioEntity>) {
        deleteAllTeamRadio()
        insertTeamRadios(radios)
    }
}
