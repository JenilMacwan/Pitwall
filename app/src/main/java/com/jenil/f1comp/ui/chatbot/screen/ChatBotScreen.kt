package com.jenil.f1comp.ui.chatbot.screen

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.jenil.f1comp.R
import com.jenil.f1comp.data.model.APEX_ACCOUNT_REQUIRED
import com.jenil.f1comp.data.model.ChatMessage
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.chatbot.component.ChatBubble
import com.jenil.f1comp.ui.chatbot.component.QuickPromptBar
import com.jenil.f1comp.ui.chatbot.component.ThinkBubble
import com.jenil.f1comp.viewmodel.ChatUiState
import com.jenil.f1comp.viewmodel.ChatViewModel
import java.util.Locale

private const val TAG = "ChatbotScreen"

@Composable
fun ChatbotScreen(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel(),
    navController: NavController,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sessionId by viewModel.activeSessionId.collectAsStateWithLifecycle()
    val storedMessages by viewModel.messages.collectAsStateWithLifecycle()
    val responseStyle by viewModel.responseStyle.collectAsStateWithLifecycle()

    val welcomeText = stringResource(R.string.chatbot_welcome)
    val errorPrefixText = stringResource(R.string.chatbot_error)
    val accountRequiredText = stringResource(R.string.chatbot_account_required)
    val welcomeMessage = remember(welcomeText) {
        ChatMessage(id = "welcome", text = welcomeText, isUser = false, timestamp = "")
    }

    val context = LocalContext.current
    var text by rememberSaveable { mutableStateOf("") }
    val voiceInputLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val recognized = result.data
                ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()
                .orEmpty()
            if (recognized.isNotBlank()) text = listOf(text, recognized).filter(String::isNotBlank).joinToString(" ")
        }
    }
    var speechEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        lateinit var engine: TextToSpeech
        engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) engine.setLanguage(Locale.getDefault())
        }
        speechEngine = engine
        onDispose {
            engine.stop()
            engine.shutdown()
            speechEngine = null
        }
    }
    var transientError by remember { mutableStateOf<String?>(null) }
    var lastSubmittedQuery by rememberSaveable { mutableStateOf<String?>(null) }
    val baseMessages = storedMessages.ifEmpty { listOf(welcomeMessage) }
    val messages = if (transientError == null) baseMessages else baseMessages + ChatMessage(
        id = "chat-error",
        text = transientError.orEmpty(),
        isUser = false,
        timestamp = "",
        retryQuery = lastSubmittedQuery
    )

    var isThinking by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ChatUiState.Loading -> {
                isThinking = true
                transientError = null
            }
            is ChatUiState.Success -> {
                isThinking = false
                transientError = null
            }
            is ChatUiState.Error -> {
                isThinking = false
                Log.e(TAG, "[UI ERROR] Chat failed: ${state.message}")
                transientError = if (state.message == APEX_ACCOUNT_REQUIRED) {
                    accountRequiredText
                } else {
                    "$errorPrefixText ${state.message}"
                }
            }
            is ChatUiState.Idle -> {
                isThinking = false
            }
        }
    }

    LaunchedEffect(messages.size, isThinking) {
        val lastIndex = messages.size - if (isThinking) 0 else 1
        if (lastIndex >= 0) {
            listState.animateScrollToItem(lastIndex.coerceAtLeast(0))
        }
    }

    fun sendMessage(promptText: String? = null) {
        if (isThinking) {
            Log.w(TAG, "[UI USER ACTION] Ignored - Apex is currently thinking...")
            return
        }

        val finalMessage = promptText ?: text
        val trimmed = finalMessage.trim()

        if (trimmed.isEmpty() || sessionId == null) return

        transientError = null
        lastSubmittedQuery = trimmed

        if (promptText == null) {
            text = ""
        }

        viewModel.sendMessage(sessionId!!, trimmed)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "status_pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = F1ScreenPadding.topPadding())
            .imePadding()
    ) {
        // topbar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent,
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
                        contentDescription = stringResource(R.string.common_back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.title_chatbot),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.chatbot_beta),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .graphicsLayer(
                                        scaleX = scale,
                                        scaleY = scale
                                    )
                                    .alpha(pulseAlpha)
                                    .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), shape = CircleShape)
                            )

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                    Text(
                        text = stringResource(R.string.chatbot_powered_by),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        sessionId?.let(viewModel::clearSession)
                        transientError = null
                        text = ""
                        isThinking = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = stringResource(R.string.chatbot_clear_chat),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Message List Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatBubble(
                    message = message,
                    onCopy = { response ->
                        val clipboard = context.getSystemService(ClipboardManager::class.java)
                        clipboard.setPrimaryClip(ClipData.newPlainText("Apex response", response))
                        Toast.makeText(context, R.string.chatbot_copied, Toast.LENGTH_SHORT).show()
                    },
                    onShare = { response ->
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, response)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.chatbot_share_response)))
                    },
                    onReadAloud = { response -> speechEngine?.speak(response, TextToSpeech.QUEUE_FLUSH, null, "apex-response") },
                    onRetry = { query ->
                        transientError = null
                        isThinking = true
                        viewModel.retryMessage(sessionId.orEmpty(), query)
                    },
                    onOpenRelatedData = { route -> navController.navigate(route) }
                )
            }

            if (isThinking) {
                item(key = "thinking_bubble") {
                    ThinkBubble()
                }
            }
        }
        androidx.compose.material3.AssistChip(
            onClick = { viewModel.toggleResponseStyle() },
            label = { Text(stringResource(if (responseStyle == "concise") R.string.chatbot_concise else R.string.chatbot_detailed)) },
            modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
        )
        QuickPromptBar(
            onPromptClick = { prompt ->
                sendMessage(prompt)
            }
        )

        // Bottom Input Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = { Text(stringResource(R.string.chatbot_placeholder)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = { sendMessage() }
                        ),
                        maxLines = 4
                    )
                    IconButton(onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.chatbot_voice_input))
                        }
                        runCatching { voiceInputLauncher.launch(intent) }.onFailure {
                            Toast.makeText(context, R.string.chatbot_voice_unavailable, Toast.LENGTH_SHORT).show()
                        }
                    }, enabled = !isThinking, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Rounded.Mic, contentDescription = stringResource(R.string.chatbot_voice_input))
                    }
                    IconButton(
                        onClick = {
                            if (isThinking) {
                                viewModel.cancelRequest()
                                isThinking = false
                            } else sendMessage()
                        },
                        enabled = isThinking || (text.trim().isNotEmpty() && sessionId != null),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (isThinking || text.trim().isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isThinking || text.trim().isNotEmpty()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            if (isThinking) Icons.Rounded.Stop else Icons.AutoMirrored.Filled.Send,
                            contentDescription = stringResource(if (isThinking) R.string.chatbot_stop_response else R.string.chatbot_send_message)
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.chatbot_disclaimer),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 6.dp)
                )
            }
        }
    }
}
