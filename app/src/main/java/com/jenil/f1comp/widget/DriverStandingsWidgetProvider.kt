package com.jenil.f1comp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatDelegate
import com.jenil.f1comp.MainActivity
import com.jenil.f1comp.R
import com.jenil.f1comp.data.local.AppDatabase
import com.jenil.f1comp.data.local.entity.DriverStandingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Locale

class DriverStandingsWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val standings = db.driverStandingsDao.getDriverStandings().firstOrNull().orEmpty()

                for (appWidgetId in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId, standings)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            standings: List<DriverStandingsEntity>
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_driver_standings)
            val localizedContext = getLocalizedContext(context)

            views.setTextViewText(R.id.tv_title, localizedContext.getString(R.string.widget_driver_standings_title))

            val posIds = arrayOf(R.id.tv_pos_1, R.id.tv_pos_2, R.id.tv_pos_3)
            val driverIds = arrayOf(R.id.tv_driver_1, R.id.tv_driver_2, R.id.tv_driver_3)
            val teamIds = arrayOf(R.id.tv_team_1, R.id.tv_team_2, R.id.tv_team_3)
            val ptsIds = arrayOf(R.id.tv_pts_1, R.id.tv_pts_2, R.id.tv_pts_3)

            val top3 = standings.take(3)

            for (i in 0 until 3) {
                if (i < top3.size) {
                    val driver = top3[i]
                    val ptsString = if (driver.points % 1.0 == 0.0) {
                        driver.points.toInt().toString()
                    } else {
                        driver.points.toString()
                    }

                    views.setTextViewText(posIds[i], "${driver.position}")
                    views.setTextViewText(driverIds[i], driver.name)
                    views.setTextViewText(teamIds[i], driver.team ?: "F1")
                    views.setTextViewText(ptsIds[i], localizedContext.getString(R.string.widget_pts_format, ptsString))
                } else {
                    views.setTextViewText(posIds[i], "${i + 1}")
                    views.setTextViewText(driverIds[i], "--")
                    views.setTextViewText(teamIds[i], "--")
                    views.setTextViewText(ptsIds[i], localizedContext.getString(R.string.widget_pts_format, "0"))
                }
            }

            // Click widget to launch MainActivity with deep link or intent
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getLocalizedContext(context: Context): Context {
            val locales = AppCompatDelegate.getApplicationLocales()
            val locale = if (!locales.isEmpty) {
                locales.get(0) ?: Locale.getDefault()
            } else {
                Locale.getDefault()
            }
            val config = context.resources.configuration
            config.setLocale(locale)
            return context.createConfigurationContext(config)
        }

        fun pushUpdateToWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, DriverStandingsWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            if (appWidgetIds.isNotEmpty()) {
                val intent = Intent(context, DriverStandingsWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                }
                context.sendBroadcast(intent)
            }
        }
    }
}
