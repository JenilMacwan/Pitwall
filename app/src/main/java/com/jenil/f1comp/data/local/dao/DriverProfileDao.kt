package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jenil.f1comp.data.local.entity.DriverProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverProfileDao {
    @Query("SELECT * FROM driver_profile")
    fun getCachedDriverProfiles(): Flow<List<DriverProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun refreshDriverProfiles(profiles: List<DriverProfileEntity>)

    @Query("DELETE FROM driver_profile")
    suspend fun deleteAllDriverProfiles()
}
