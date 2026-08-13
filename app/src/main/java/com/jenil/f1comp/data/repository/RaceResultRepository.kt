package com.jenil.f1comp.data.repository

import com.jenil.f1comp.data.local.dao.RaceResultDao
import com.jenil.f1comp.data.local.entity.RaceResultEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RaceResultRepository @Inject constructor(
    private val apiService: F1ApiService,
    private val resultDao: RaceResultDao
) {
    fun getCachedRaceResults(raceId: String): Flow<List<RaceResultEntity>> {
        return resultDao.getRaceResults(raceId)
    }

    suspend fun refreshRaceResults(raceId: String, year: Int) {
        val networkResults = apiService.getRaceResult(raceId, year)

        val raceResultEntities = networkResults.results.map { networkModel ->
            RaceResultEntity(
                raceId = raceId,
                raceName = networkResults.raceName,
                position = networkModel.position,
                positionText = networkModel.positionText ?: networkModel.position,
                driver = networkModel.driver,
                driverId = null,
                driverImage = networkModel.driverImage,
                constructor = networkModel.constructor,
                points = networkModel.points,
                grid = networkModel.grid,
                status = networkModel.status,
                time = networkModel.time ?: "",
                fastestLap = networkModel.fastestLap ?: ""
            )
        }
        // raceId now passed through so only this race's rows get cleared.
        resultDao.refreshRaceResult(raceId, raceResultEntities)
    }
}