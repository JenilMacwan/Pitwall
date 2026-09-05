package com.jenil.f1comp.data.repository

import com.jenil.f1comp.data.local.dao.SprintQualiDao
import com.jenil.f1comp.data.local.entity.SprintQualifyingResultEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SprintQualiResultRepository @Inject constructor(
    private val apiService: F1ApiService,
    private val sprintQualiDao: SprintQualiDao
) {
    fun getCachedSprintQualifyingResults(raceId: String): Flow<List<SprintQualifyingResultEntity>> {
        return sprintQualiDao.getSprintQualifyingResults(raceId)
    }

    suspend fun refreshSprintQualifyingResults(raceId: String, year: Int) {
        val networkResults = apiService.getSprintQualifyingResults(raceId, year)
        val sprintQualifyingResultEntities = networkResults.results.map { networkModel ->
            SprintQualifyingResultEntity(
                raceId = raceId,
                season = networkResults.season,
                round = networkResults.round,
                raceName = networkResults.raceName,
                position = networkModel.position,
                driver = networkModel.driver,
                driverImage = networkModel.driverImage,
                constructor = networkModel.constructor,
                q1 = networkModel.q1 ?: "",
                q2 = networkModel.q2 ?: "",
                q3 = networkModel.q3 ?: "",
            )
        }
        sprintQualiDao.refreshSprintQualifyingResults(raceId, sprintQualifyingResultEntities)
    }
}