package com.jenil.f1comp.data.repository

import android.util.Log
import com.jenil.f1comp.data.local.dao.ScheduleDao
import com.jenil.f1comp.data.local.entity.ScheduleEntity
import com.jenil.f1comp.data.remote.F1ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ScheduleRepository @Inject constructor(
    private val apiService: F1ApiService,
    private val scheduleDao: ScheduleDao
) {
    fun getCachedScheduleInfo(): Flow<List<ScheduleEntity>> {
        return scheduleDao.getSchedule()
    }

    suspend fun refreshSchedule() {
        Log.d("ScheduleRepository", "Refreshing schedule from API...")
        val response = apiService.getSchedule()
        val scheduleEntities = response.schedule.map { networkModel ->
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
        Log.d("ScheduleRepository", "Fetched ${scheduleEntities.size} races. Updating database...")
        scheduleDao.refreshSchedule(scheduleEntities)
        Log.d("ScheduleRepository", "$scheduleEntities")
        Log.d("ScheduleRepository", "Database updated successfully.")
    }
}
