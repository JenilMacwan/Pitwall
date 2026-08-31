package com.jenil.f1comp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jenil.f1comp.data.repository.DataStoreRepository
import com.jenil.f1comp.data.repository.ScheduleRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RaceBootReceiver : BroadcastReceiver() {
    @Inject
    lateinit var scheduleRepository: ScheduleRepository

    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val isRaceEnabled = dataStoreRepository.isRaceRemindersEnabled.first() ?: false
                    val isSessionEnabled = dataStoreRepository.isSessionRemindersEnabled.first() ?: false

                    if (isRaceEnabled || isSessionEnabled) {
                        val schedule = scheduleRepository.getCachedScheduleInfo().first()
                        RaceNotificationCoordinator.syncAllAlarms(
                            context = context,
                            schedule = schedule,
                            isRaceRemindersEnabled = isRaceEnabled,
                            isSessionRemindersEnabled = isSessionEnabled
                        )
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}