package com.jenil.f1comp.notification

import android.content.Context
import com.jenil.f1comp.data.local.entity.ScheduleEntity
import com.jenil.f1comp.data.model.RaceSchedule
import com.jenil.f1comp.data.model.RaceSession
import com.jenil.f1comp.util.NotificationTimeUtils

object RaceNotificationCoordinator {

    private const val SESSION_RACE = 0
    private const val SESSION_FP1 = 1
    private const val SESSION_FP2 = 2
    private const val SESSION_FP3 = 3
    private const val SESSION_QUALIFYING = 4
    private const val SESSION_SPRINT_QUALIFYING = 5
    private const val SESSION_SPRINT = 6

    fun syncAllAlarms(
        context: Context,
        schedule: List<ScheduleEntity>,
        isRaceRemindersEnabled: Boolean,
        isSessionRemindersEnabled: Boolean
    ) {
        val scheduler = AndroidAlarmScheduler(context)

        schedule.forEach { race ->
            val roundInt = race.round.toIntOrNull() ?: return@forEach

            // 1. MAIN GRAND PRIX
            val raceAlarm = RaceAlarmItem(
                id = generateId(roundInt, SESSION_RACE),
                timeInMillis = NotificationTimeUtils.calculateNotificationTime(race.grandPrix, race.time),
                title = "${race.raceName} 🏁",
                message = "Grand Prix starts in 15 minutes! Lights out soon."
            )
            if (isRaceRemindersEnabled && !race.isCompleted) {
                scheduler.schedule(raceAlarm)
            } else {
                scheduler.cancel(raceAlarm)
            }

            // 2. TIMETABLE SESSIONS (FP1-3, Quali, Sprint)
            val sessionAlarms = mutableListOf<RaceAlarmItem>()

            race.firstPractice?.let {
                sessionAlarms.add(createSessionItem(roundInt, SESSION_FP1, race.raceName, "Practice 1", it))
            }
            race.secondPractice?.let {
                sessionAlarms.add(createSessionItem(roundInt, SESSION_FP2, race.raceName, "Practice 2", it))
            }
            race.thirdPractice?.let {
                sessionAlarms.add(createSessionItem(roundInt, SESSION_FP3, race.raceName, "Practice 3", it))
            }
            race.qualifying?.let {
                sessionAlarms.add(createSessionItem(roundInt, SESSION_QUALIFYING, race.raceName, "Qualifying", it))
            }
            race.sprintQualifying?.let {
                sessionAlarms.add(createSessionItem(roundInt, SESSION_SPRINT_QUALIFYING, race.raceName, "Sprint Qualifying", it))
            }
            race.sprint?.let {
                sessionAlarms.add(createSessionItem(roundInt, SESSION_SPRINT, race.raceName, "Sprint Race", it))
            }

            sessionAlarms.forEach { sessionItem ->
                if (isSessionRemindersEnabled && !race.isCompleted) {
                    scheduler.schedule(sessionItem)
                } else {
                    scheduler.cancel(sessionItem)
                }
            }
        }
    }

    private fun createSessionItem(
        round: Int,
        sessionType: Int,
        raceName: String,
        sessionName: String,
        session: RaceSession
    ): RaceAlarmItem {
        return RaceAlarmItem(
            id = generateId(round, sessionType),
            timeInMillis = NotificationTimeUtils.calculateNotificationTime(session.date, session.time),
            title = "$raceName - $sessionName",
            message = "$sessionName begins in 15 minutes."
        )
    }

    private fun generateId(round: Int, sessionType: Int): Int = (round * 10) + sessionType
}