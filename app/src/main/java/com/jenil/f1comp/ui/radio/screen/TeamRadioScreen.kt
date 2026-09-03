package com.jenil.f1comp.ui.radio.screen

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.radio.components.EmptyRadioState
import com.jenil.f1comp.ui.radio.components.LazyRowChips
import com.jenil.f1comp.ui.radio.components.RadioCard
import com.jenil.f1comp.ui.radio.components.RadioClip
import com.jenil.f1comp.ui.radio.components.RadioLoadingState
import com.jenil.f1comp.util.toRadioClip
import com.jenil.f1comp.viewmodel.TeamRadioViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

const val ALL_DRIVERS = "ALL"

@Composable
fun TeamRadioScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: TeamRadioViewModel = hiltViewModel()
) {
    val cachedRadios by viewModel.teamRadios.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedDriverCode by viewModel.selectedDriverCode.collectAsStateWithLifecycle()

    val clips = remember(cachedRadios) {
        cachedRadios.map { it.toRadioClip() }
    }

    val sessionTitle = remember(cachedRadios) {
        val first = cachedRadios.firstOrNull { !it.eventName.isNullOrEmpty() || !it.sessionName.isNullOrEmpty() }
        if (first != null) {
            val event = first.eventName.orEmpty()
            val session = first.sessionName.orEmpty()
            when {
                event.isNotEmpty() && session.isNotEmpty() -> "$event · $session"
                event.isNotEmpty() -> event
                session.isNotEmpty() -> session
                else -> "Latest Session"
            }
        } else {
            "Latest Session"
        }
    }

    var playingClipId by remember { mutableStateOf<String?>(null) }
    var isBuffering by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }
    var playbackTimeLabel by remember { mutableStateOf<String?>(null) }
    var playbackError by remember { mutableStateOf<String?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    val cachedDurations = remember { mutableStateMapOf<String, String>() }

    LaunchedEffect(playbackError) {
        if (playbackError != null) {
            delay(2500.milliseconds)
            playbackError = null
        }
    }

    LaunchedEffect(playingClipId) {
        if (playingClipId != null) {
            while (isActive) {
                val player = mediaPlayer
                if (player != null && player.isPlaying) {
                    val dur = player.duration.coerceAtLeast(1)
                    val pos = player.currentPosition.coerceIn(0, dur)
                    playbackProgress = pos.toFloat() / dur.toFloat()

                    val curSec = pos / 1000
                    val totalSec = dur / 1000
                    playbackTimeLabel = String.format(
                        Locale.getDefault(),
                        "%d:%02d / %d:%02d",
                        curSec / 60, curSec % 60,
                        totalSec / 60, totalSec % 60
                    )
                }
                delay(50.milliseconds)
            }
        } else {
            playbackProgress = 0f
            playbackTimeLabel = null
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    fun resetPlaybackState() {
        playingClipId = null
        isBuffering = false
        playbackProgress = 0f
        playbackTimeLabel = null
    }

    fun togglePlayback(clip: RadioClip) {
        if (playingClipId == clip.id) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            resetPlaybackState()
            return
        }

        val url = clip.audioUrl
        if (url.isNullOrEmpty()) {
            playbackError = "No audio available for this clip"
            return
        }

        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        playingClipId = clip.id
        isBuffering = true
        playbackProgress = 0f

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener { mp ->
                    isBuffering = false
                    val dur = mp.duration.coerceAtLeast(1)
                    val totalSec = dur / 1000
                    val totalFormatted = String.format(
                        Locale.getDefault(),
                        "%d:%02d",
                        totalSec / 60, totalSec % 60
                    )
                    cachedDurations[clip.id] = totalFormatted
                    playbackTimeLabel = "0:00 / $totalFormatted"
                    mp.start()
                }
                setOnErrorListener { _, _, _ ->
                    resetPlaybackState()
                    playbackError = "Couldn't play this clip"
                    true
                }
                setOnCompletionListener {
                    resetPlaybackState()
                    release()
                    mediaPlayer = null
                }
                prepareAsync()
            }
        } catch (_: Exception) {
            resetPlaybackState()
            playbackError = "Couldn't play this clip"
        }
    }

    val driverOptions = remember(clips) {
        listOf(ALL_DRIVERS) + clips.map { it.driverCode }.distinct()
    }

    val filteredClips = remember(clips, selectedDriverCode) {
        if (selectedDriverCode == ALL_DRIVERS) clips
        else clips.filter { it.driverCode.equals(selectedDriverCode, ignoreCase = true) }
    }

    val listState = rememberLazyListState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = F1ScreenPadding.topPadding())
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "Team Radio",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sessionTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { viewModel.refreshTeamRadio() }) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp)
                            .semantics { contentDescription = "Refreshing radio clips" },
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Refresh radio clips",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        AnimatedVisibility(visible = playbackError != null, enter = fadeIn(), exit = fadeOut()) {
            playbackError?.let { message ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        LazyRowChips(
            driverOptions = driverOptions,
            selectedDriverCode = selectedDriverCode,
            onSelect = { viewModel.selectDriverCode(it) },
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                isLoading && clips.isEmpty() -> {
                    item { RadioLoadingState() }
                }
                filteredClips.isEmpty() -> {
                    item { EmptyRadioState(driverCode = selectedDriverCode) }
                }
                else -> {
                    items(filteredClips, key = { it.id }) { clip ->
                        val isItemPlaying = playingClipId == clip.id
                        val isItemBuffering = isItemPlaying && isBuffering
                        val knownDuration = cachedDurations[clip.id]

                        val currentDurationLabel = when {
                            isItemPlaying && playbackTimeLabel != null -> playbackTimeLabel!!
                            knownDuration != null -> knownDuration
                            clip.durationLabel.isNotEmpty() -> clip.durationLabel
                            else -> "Radio"
                        }
                        val currentProgress = if (isItemPlaying) playbackProgress else 0f

                        RadioCard(
                            clip = clip.copy(durationLabel = currentDurationLabel),
                            isPlaying = isItemPlaying && !isItemBuffering,
                            isBuffering = isItemBuffering,
                            playbackProgress = currentProgress,
                            onPlayClick = { togglePlayback(clip) }
                        )
                    }
                }
            }
        }
    }
}