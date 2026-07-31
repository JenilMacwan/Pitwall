package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jenil.f1comp.data.local.entity.CircuitInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CircuitInfoDao {

    @Query("SELECT * FROM circuits_table")
    fun getAllCircuits(): Flow<List<CircuitInfoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCircuits(circuits: List<CircuitInfoEntity>)

    @Query("DELETE FROM circuits_table")
    suspend fun clearCircuits()

    @Transaction
    suspend fun refreshCircuits(circuits: List<CircuitInfoEntity>) {
        clearCircuits()
        insertCircuits(circuits)
    }
}
