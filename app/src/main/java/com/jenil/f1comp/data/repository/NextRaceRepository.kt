package com.jenil.f1comp.data.repository

import android.content.Context
import com.jenil.f1comp.data.local.AppDatabase
import com.jenil.f1comp.data.local.dao.NextRaceDao
import com.jenil.f1comp.data.local.entity.CircuitInfoEntity
import com.jenil.f1comp.data.local.entity.NextRaceEntity
import com.jenil.f1comp.data.local.entity.ScheduleEntity
import com.jenil.f1comp.data.remote.F1ApiService
import com.jenil.f1comp.widget.NextRaceWidgetProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class NextRaceRepository @Inject constructor(
    private val apiService: F1ApiService,
    private val nextRaceDao: NextRaceDao,
    @ApplicationContext private val context: Context
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
                country = response.country,
                weather = response.weather,
                countdown = response.countdown.copy(calculatedAtEpochMillis = System.currentTimeMillis()),
                sessionName = response.sessionName,
                sprint = response.sprint,
                flagEmoji = response.flagEmoji,
                ongoingSession = response.ongoingSession ?: "N/A",
            )
        nextRaceDao.refreshNextRace(nextRaceEntity)

        runCatching {
            val db = AppDatabase.getInstance(context)
            if (db.circuitInfoDao.getAllCircuits().firstOrNull().isNullOrEmpty()) {
                val circuitResponse = apiService.getCircuitInfo()
                val circuitEntities = circuitResponse.circuits.map { networkModel ->
                    CircuitInfoEntity(
                        circuitId = networkModel.circuitId,
                        circuitName = networkModel.circuitName,
                        circuitLocation = networkModel.circuitLocation,
                        circuitCountry = networkModel.circuitCountry,
                        circuitLayout = networkModel.circuitLayout,
                        laps = networkModel.laps,
                        length = networkModel.length,
                        type = networkModel.type
                    )
                }
                db.circuitInfoDao.refreshCircuits(circuitEntities)
            }
            if (db.scheduleDao.getSchedule().firstOrNull().isNullOrEmpty()) {
                val scheduleResponse = apiService.getSchedule()
                val scheduleEntities = scheduleResponse.schedule.map { networkModel ->
                    ScheduleEntity(
                        round = networkModel.round,
                        flag = networkModel.flag,
                        raceName = networkModel.raceName,
                        circuitId = networkModel.circuitId,
                        circuitName = networkModel.circuitName,
                        circuitLocation = networkModel.circuitLocation,
                        circuitCountry = networkModel.circuitCountry,
                        grandPrix = networkModel.grandPrix,
                        time = networkModel.time,
                        firstPractice = networkModel.firstPractice,
                        secondPractice = networkModel.secondPractice,
                        thirdPractice = networkModel.thirdPractice,
                        qualifying = networkModel.qualifying,
                        sprint = networkModel.sprint,
                        sprintQualifying = networkModel.sprintQualifying,
                        isCompleted = networkModel.isCompleted
                    )
                }
                db.scheduleDao.refreshSchedule(scheduleEntities)
            }
        }

        NextRaceWidgetProvider.pushUpdateToWidgets(context)

    }
}
