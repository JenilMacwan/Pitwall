package com.jenil.f1comp.data.repository

import com.jenil.f1comp.data.local.dao.SprintResultDao
import com.jenil.f1comp.data.local.entity.SprintResultEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SprintResultRepository @Inject constructor(
    private val apiService: F1ApiService,
    private val sprintResultDao: SprintResultDao
){
    fun getCachedSprintResults(raceId: String): Flow<List<SprintResultEntity>> {
        return sprintResultDao.getSprintResults(raceId)
    }

    suspend fun refreshSprintResults(raceId: String, year: Int){
        val networkResults = apiService.getSprintResults(raceId, year)
        val sprintResultEntities = networkResults.results.map { networkModel ->
            SprintResultEntity(
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
        sprintResultDao.refreshSprintResults(raceId, sprintResultEntities)
    }
}