package com.jenil.f1comp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import coil.imageLoader
import coil.request.ImageRequest
import com.jenil.f1comp.MainActivity
import com.jenil.f1comp.R
import com.jenil.f1comp.data.local.AppDatabase
import com.jenil.f1comp.data.local.entity.NextRaceEntity
import com.jenil.f1comp.data.model.remainingSeconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.time.Duration.Companion.milliseconds

class NextRaceWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val nextRace = db.nextRaceDao.getNextRace().firstOrNull()
                val trackBitmap = nextRace?.let { loadTrackBitmap(context, it) }

                for (appWidgetId in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId, nextRace, trackBitmap)
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
            nextRaceEntity: NextRaceEntity?,
            trackBitmap: Bitmap?
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_next_race)
            val localizedContext = getLocalizedContext(context)

            if (nextRaceEntity != null) {
                views.setTextViewText(R.id.tv_flag_emoji, nextRaceEntity.flagEmoji.ifBlank { "🏁" })
                views.setTextViewText(R.id.tv_race_name, nextRaceEntity.raceName.ifBlank { "Grand Prix" })

                val circuitText = if (nextRaceEntity.country.isNotBlank() && !nextRaceEntity.circuit.contains(nextRaceEntity.country, ignoreCase = true)) {
                    "${nextRaceEntity.circuit} · ${nextRaceEntity.country}"
                } else {
                    nextRaceEntity.circuit
                }
                views.setTextViewText(R.id.tv_circuit, circuitText)

                val activeSession = nextRaceEntity.ongoingSession?.takeIf {
                    it.isNotBlank() && !it.equals("N/A", ignoreCase = true)
                }

                if (activeSession != null) {
                    val headerText = formatSessionHeader(activeSession, isLiveBadge = true, localizedContext = localizedContext)
                    views.setTextViewText(R.id.tv_session_name, headerText)
                    views.setInt(
                        R.id.tv_session_name,
                        "setBackgroundResource",
                        R.drawable.widget_next_race_chip_live
                    )
                } else {
                    val headerText = formatSessionHeader(nextRaceEntity.sessionName, isLiveBadge = false, localizedContext = localizedContext)
                    views.setTextViewText(R.id.tv_session_name, headerText)
                    views.setInt(
                        R.id.tv_session_name,
                        "setBackgroundResource",
                        R.drawable.widget_next_race_chip
                    )
                }

                if (trackBitmap != null) {
                    views.setImageViewBitmap(R.id.iv_track_layout, trackBitmap)
                    views.setViewVisibility(R.id.iv_track_layout, View.VISIBLE)
                    views.setViewVisibility(R.id.tv_track_fallback, View.GONE)
                } else {
                    views.setViewVisibility(R.id.iv_track_layout, View.GONE)
                    views.setViewVisibility(R.id.tv_track_fallback, View.VISIBLE)
                }

                // Calculate time remaining in milliseconds from countdown data
                val countdown = nextRaceEntity.countdown
                val totalMillisRemaining = countdown.remainingSeconds() * 1000L

                val isRaceSession = nextRaceEntity.sessionName.contains("Race", ignoreCase = true) &&
                        !nextRaceEntity.sessionName.contains("Sprint", ignoreCase = true)
                val labelText = if (isRaceSession) {
                    localizedContext.getString(R.string.widget_lights_out_in)
                } else {
                    localizedContext.getString(R.string.widget_session_starts_in)
                }

                if (totalMillisRemaining > 0) {
                    val totalHours = (totalMillisRemaining / (3600 * 1000L)).toInt()
                    val days = totalHours / 24
                    val hours = totalHours % 24

                    val formattedCountdown = when {
                        days > 0 -> "${days}d ${hours}h"
                        hours > 0 -> "${hours}h"
                        else -> "< 1h"
                    }

                    views.setTextViewText(R.id.tv_countdown_label, labelText)
                    views.setTextViewText(R.id.tv_countdown_value, formattedCountdown)
                } else {
                    views.setTextViewText(R.id.tv_countdown_label, localizedContext.getString(R.string.widget_live_session))
                    views.setTextViewText(R.id.tv_countdown_value, localizedContext.getString(R.string.widget_in_progress))
                }
            } else {
                views.setTextViewText(R.id.tv_flag_emoji, "🏁")
                views.setTextViewText(R.id.tv_race_name, localizedContext.getString(R.string.widget_no_data_title))
                views.setTextViewText(R.id.tv_circuit, localizedContext.getString(R.string.widget_no_data_subtitle))
                views.setTextViewText(R.id.tv_session_name, localizedContext.getString(R.string.widget_next_session_title))
                views.setTextViewText(R.id.tv_countdown_label, localizedContext.getString(R.string.widget_session_starts_in))
                views.setTextViewText(R.id.tv_countdown_value, "--d --h")
            }

            // Click widget to launch MainActivity
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

        private suspend fun loadTrackBitmap(context: Context, nextRace: NextRaceEntity): Bitmap? {
            return try {
                val db = AppDatabase.getInstance(context)
                val scheduleList = db.scheduleDao.getSchedule().firstOrNull().orEmpty()
                val schedule = scheduleList.firstOrNull { it.round == nextRace.round }
                    ?: scheduleList.firstOrNull { it.raceName.equals(nextRace.raceName, ignoreCase = true) }

                val circuitInfos = db.circuitInfoDao.getAllCircuits().firstOrNull().orEmpty()
                val circuit = schedule?.circuitId?.let { id -> circuitInfos.firstOrNull { it.circuitId == id } }
                    ?: circuitInfos.firstOrNull { info ->
                        val raceCircuit = nextRace.circuit.normalizedCircuitName()
                        val infoName = info.circuitName.normalizedCircuitName()
                        raceCircuit.isNotEmpty() && infoName.isNotEmpty() &&
                            (raceCircuit == infoName || raceCircuit.contains(infoName) || infoName.contains(raceCircuit))
                    }

                val imageUrl = circuit?.circuitLayout?.takeIf { it.isNotBlank() } ?: return null

                val request = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .size(480, 320)
                    .allowHardware(false)
                    .build()

                val rawBitmap = withTimeoutOrNull(3_500L.milliseconds) {
                    context.imageLoader.execute(request).drawable?.toBitmap(config = Bitmap.Config.ARGB_8888)
                } ?: return null

                if (rawBitmap.width <= 0 || rawBitmap.height <= 0) return null

                val highContrastBitmap = rawBitmap.toHighContrastTrackMap()
                val scale = minOf(360f / highContrastBitmap.width, 240f / highContrastBitmap.height, 1f)
                if (scale < 1f) {
                    highContrastBitmap.scale(
                        (highContrastBitmap.width * scale).toInt().coerceAtLeast(1),
                        (highContrastBitmap.height * scale).toInt().coerceAtLeast(1)
                    )
                } else highContrastBitmap
            } catch (_: Exception) {
                null
            }
        }

        private fun Bitmap.toHighContrastTrackMap(): Bitmap {
            val result = createBitmap(width, height)
            val canvas = Canvas(result)
            val paint = Paint().apply {
                colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            }
            canvas.drawBitmap(this, 0f, 0f, paint)
            return result
        }

        private fun formatSessionHeader(rawSessionName: String, isLiveBadge: Boolean, localizedContext: Context): String {
            val cleanName = parseSessionNameOnly(rawSessionName)
            val localTime = parseAndFormatToLocalTime(rawSessionName)

            return when {
                isLiveBadge -> {
                    val liveFormat = localizedContext.getString(R.string.widget_live_prefix, cleanName)
                    if (localTime != null) "$liveFormat ($localTime)" else liveFormat
                }
                localTime != null -> {
                    "$cleanName  ·  $localTime"
                }
                else -> cleanName
            }
        }

        private fun parseSessionNameOnly(raw: String): String {
            var s = raw.trim()
            val sessionIdx = s.indexOf("session name", ignoreCase = true)
            if (sessionIdx != -1) {
                s = s.substring(sessionIdx + "session name".length).trim()
                if (s.startsWith(":")) {
                    s = s.substring(1).trim()
                }
            }
            val timeIdx = s.indexOf("time", ignoreCase = true)
            if (timeIdx != -1) {
                s = s.substring(0, timeIdx).trim()
            }
            if (s.startsWith(":")) {
                s = s.substring(1).trim()
            }
            return if (s.isBlank()) "PRACTICE 1" else s.uppercase()
        }

        private fun parseAndFormatToLocalTime(raw: String): String? {
            return try {
                val regex = Regex("""\d{4}-\d{2}-\d{2}\s+\d{2}:\d{2}""")
                val match = regex.find(raw)?.value ?: return null

                val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val date = utcFormat.parse(match) ?: return null

                val localFormat = SimpleDateFormat("EEE h:mm a", Locale.getDefault()).apply {
                    timeZone = TimeZone.getDefault()
                }
                localFormat.format(date).uppercase()
            } catch (_: Exception) {
                null
            }
        }

        private fun String.normalizedCircuitName(): String =
            lowercase().filter { it.isLetterOrDigit() }

        fun pushUpdateToWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, NextRaceWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            if (appWidgetIds.isNotEmpty()) {
                val intent = Intent(context, NextRaceWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                }
                context.sendBroadcast(intent)
            }
        }
    }
}
