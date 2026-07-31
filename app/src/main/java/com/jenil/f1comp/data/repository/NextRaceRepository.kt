package com.jenil.f1comp.data.repository


import com.jenil.f1comp.data.local.dao.NextRaceDao
import com.jenil.f1comp.data.local.entity.NextRaceEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class NextRaceRepository @Inject constructor (
    private val apiService: F1ApiService,
    private val nextRaceDao: NextRaceDao
) {
    fun getCachedNextRaceInfo(): Flow<NextRaceEntity?> {
        return nextRaceDao.getNextRace()
    }

    suspend fun refreshNextRaceInfo() {

        val response = apiService.getNextRace()
        val nextRaceEntity =
            NextRaceEntity(
                id = 1,
                round = response.round,
                raceName = response.raceName,
                circuit = response.circuit,
                weather = response.weather,
                countdown = response.countdown,
                sessionName = response.sessionName,
                sprint = response.sprint,
                flagEmoji = response.flagEmoji,
                ongoingSession = response.ongoingSession ?: "N/A",
            )
        nextRaceDao.refreshNextRace(nextRaceEntity)

    }
}