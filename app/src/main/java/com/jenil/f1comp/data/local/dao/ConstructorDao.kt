package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.jenil.f1comp.data.local.entity.ConstructorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConstructorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConstructors(constructors: List<ConstructorEntity>)

    @Query(value = "SELECT * FROM constructor_table")
    fun getAllConstructors(): Flow<List<ConstructorEntity>>

    @Query("DELETE FROM constructor_table")
    suspend fun clearConstructors()

    @Transaction
    suspend fun refreshConstructors(constructors: List<ConstructorEntity>){
        clearConstructors()
        insertConstructors(constructors)
    }
}