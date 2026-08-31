package com.jenil.f1comp.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import kotlin.jvm.java

class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun schedule(item: RaceAlarmItem) {
        // Do not schedule alarms for past sessions
        if (item.timeInMillis <= System.currentTimeMillis()) return

        val intent = Intent(context, RaceAlarmReceiver::class.  java).apply {
            putExtra(RaceAlarmReceiver.EXTRA_TITLE, item.title)
            putExtra(RaceAlarmReceiver.EXTRA_MESSAGE, item.message)
            putExtra(RaceAlarmReceiver.EXTRA_NOTIFICATION_ID, item.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    item.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    item.timeInMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                item.timeInMillis,
                pendingIntent
            )
        }
    }

    override fun cancel(item: RaceAlarmItem) {
        val intent = Intent(context, RaceAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}