package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jenil.f1comp.data.local.entity.DriverEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrivers(drivers: List<DriverEntity>)

    @Query("SELECT * FROM driver_table")
    fun getAllDrivers(): Flow<List<DriverEntity>>

    @Query("DELETE FROM driver_table")
    suspend fun clearDrivers()

    @Transaction
    suspend fun refreshDrivers(drivers: List<DriverEntity>) {
        clearDrivers()
        insertDrivers(drivers)
    }

}