package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jenil.f1comp.data.local.entity.ScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: List<ScheduleEntity>)

    @Query("SELECT * FROM schedule_table")
    fun getSchedule(): Flow<List<ScheduleEntity>>

    @Query("DELETE FROM schedule_table")
    suspend fun clearSchedule()

    @Transaction
    suspend fun refreshSchedule(schedule: List<ScheduleEntity>) {
        clearSchedule()
        insertSchedule(schedule)
    }
}