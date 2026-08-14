package com.jenil.f1comp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jenil.f1comp.data.local.entity.ConstructorProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConstructorProfileDao {
    @Query("SELECT * FROM constructor_profile")
    fun getCachedConstructorProfiles(): Flow<List<ConstructorProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun refreshConstructorProfiles(profiles: List<ConstructorProfileEntity>)

    @Query("DELETE FROM constructor_profile")
    suspend fun deleteAllConstructorProfiles()
}
