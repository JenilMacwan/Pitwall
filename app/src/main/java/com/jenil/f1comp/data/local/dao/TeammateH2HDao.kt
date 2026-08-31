package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jenil.f1comp.data.local.entity.TeammateHeadtoHeadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeammateH2HDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeadToHead(headToHead: List<TeammateHeadtoHeadEntity>)

    @Query("SELECT * FROM head_to_head_table")
    fun getHeadToHead(): Flow<List<TeammateHeadtoHeadEntity>>

    @Query("DELETE FROM head_to_head_table")
    suspend fun clearHeadToHead()
}
