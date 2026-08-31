package com.jenil.f1comp.notification


data class RaceAlarmItem(
    val id: Int,
    val timeInMillis: Long,
    val title: String,
    val message: String
)

interface AlarmScheduler {
    fun schedule(item: RaceAlarmItem)
    fun cancel(item: RaceAlarmItem)
}