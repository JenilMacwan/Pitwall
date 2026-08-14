package com.jenil.f1comp.data.repository

import android.util.Log
import com.jenil.f1comp.data.local.dao.ConstructorStandingsDao
import com.jenil.f1comp.data.local.entity.ConstructorStandingsEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConstructorStandingsRepository @Inject constructor (
    private val apiService: F1ApiService,
    private val constructorStandingsDao: ConstructorStandingsDao
) {
    fun getCachedConstructorStanding(): Flow<List<ConstructorStandingsEntity>> {
        return constructorStandingsDao.getConstructorStandings()
    }

    suspend fun refreshConstructorStandings() {
        Log.d("F1Debug", "1. Constructor Repository: Starting API fetch...")
        val response = apiService.getConstructorStandings()
        Log.d("F1Debug", "2. Constructor Repository: API Success! Fetched drivers.")
        val constructorStandingsEntities = response.constructorsStandings.map { networkModel ->
            ConstructorStandingsEntity(
                position = networkModel.position.toIntOrNull()?: 0,
                points = networkModel.points.toDoubleOrNull()?: 0.0,
                name = networkModel.name,
                drivers = networkModel.drivers?.joinToString(" · ") ?: "",
                nationality = networkModel.nationality,
                constructorLogo = networkModel.constructorLogo,
            )
        }
        Log.d("F1Debug", "3. Constructor Repository: Saving to Room Database...")
        constructorStandingsDao.refreshConstructorStandings(constructorStandingsEntities)
        Log.d("F1Debug", "3. Constructor Repository: Saved to Room Database...")

    }
}