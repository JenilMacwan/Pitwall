package com.jenil.f1comp.data.repository

import android.util.Log
import com.jenil.f1comp.data.local.dao.DriverStandingsDao
import com.jenil.f1comp.data.local.entity.DriverStandingsEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DriverStandingsRepository @Inject constructor (
    private val apiService: F1ApiService,
    private val driverStandingsDao: DriverStandingsDao
) {
    fun getCachedDriverStandings(): Flow<List<DriverStandingsEntity>> {
        return driverStandingsDao.getDriverStandings()
    }

    suspend fun refreshDriverStandings() {
        Log.d("F1Debug", "1. Repository: Starting API fetch...")
        val response = apiService.getDriverStandings()
        Log.d("F1Debug", "2. Repository: API Success! Fetched  drivers.")
        val driverStandingsEntities = response.driversStandings.map { networkModel ->
            DriverStandingsEntity(
                driverId = networkModel.driverId ?: "unknown",
                position = networkModel.position.toIntOrNull()?: 0,
                points = networkModel.points.toDoubleOrNull()?: 0.0,
                name = networkModel.name,
                nationality = networkModel.nationality,
                url = networkModel.url ?: "unknown",
                team = networkModel.team ?: "N/A",
                driverImage = networkModel.driverImage
            )
        }
        Log.d("F1Debug", "3. Repository: Saving to Room Database...")
        driverStandingsDao.refreshDriverStandings(driverStandingsEntities)
        Log.d("F1Debug", "3. Repository: Database updated successfully.")

    }
}