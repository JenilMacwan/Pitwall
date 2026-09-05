package com.jenil.f1comp.data.repository

import com.jenil.f1comp.data.local.dao.QualifyingResultDao
import com.jenil.f1comp.data.local.entity.QualifyingResultEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QualifyingResultRepository @Inject constructor(
    private val apiService: F1ApiService,
    private val qualifyingResultDao: QualifyingResultDao
) {

    fun getCachedQualifyingResults(raceId: String): Flow<List<QualifyingResultEntity>> {
        return qualifyingResultDao.getQualifyingResults(raceId)
    }

    suspend fun refreshQualifyingResults(raceId: String, year: Int) {
        val networkResults = apiService.getQualifyingResults(raceId, year)
        val qualifyingResultEntities = networkResults.results.map { networkModel ->
            QualifyingResultEntity(
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
        qualifyingResultDao.refreshQualifyingResults(raceId, qualifyingResultEntities)
    }
}