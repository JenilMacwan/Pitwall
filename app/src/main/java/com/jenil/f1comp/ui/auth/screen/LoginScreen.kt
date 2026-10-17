package com.jenil.f1comp.ui.auth.screen

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.jenil.f1comp.BuildConfig
import com.jenil.f1comp.R
import com.jenil.f1comp.ui.auth.components.LoginCard
import com.jenil.f1comp.ui.auth.components.PillButton
import com.jenil.f1comp.ui.auth.components.RegisterCard
import com.jenil.f1comp.ui.theme.F1CompTheme
import com.jenil.f1comp.ui.theme.F1Red
import com.jenil.f1comp.util.ProfileUtils
import com.jenil.f1comp.viewmodel.AuthUiState
import com.jenil.f1comp.viewmodel.AuthViewModel
import com.jenil.f1comp.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    initialTab: String = "Sign-In",
    viewModel: AuthViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    val authUiState by viewModel.authUiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTab by remember(initialTab) { mutableStateOf(initialTab) }
    val appName = ProfileUtils.multipleColorText

    fun launchGoogleSignIn(context: Context, onTokenReceived: (String) -> Unit) {
        val activity = context.findActivity()
        if (activity == null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Google Sign-In failed: Host activity not found.",
                    duration = SnackbarDuration.Short
                )
            }
            return
        }

        coroutineScope.launch {
            try {
                val webClientId = try {
                    context.getString(R.string.default_web_client_id)
                } catch (_: Exception) {
                    ""
                }

                if (webClientId.isBlank()) {
                    snackbarHostState.showSnackbar(
                        message = "Google Sign-In configuration error: Web Client ID missing.",
                        duration = SnackbarDuration.Short
                    )
                    return@launch
                }

                val credentialManager = CredentialManager.create(activity)
                val googleOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleOption)
                    .build()

                val result = credentialManager.getCredential(context = activity, request = request)
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                onTokenReceived(googleIdTokenCredential.idToken)
            } catch (_: GetCredentialCancellationException) {
                // User intentionally cancelled Google Sign-In flow; ignore gracefully.
            } catch (e: Exception) {
                snackbarHostState.showSnackbar(
                    message = "Google Sign-In failed: ${e.localizedMessage ?: "Unknown error"}",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    LaunchedEffect(user) {
        if (user != null) {
            settingsViewModel.setFirstLaunchCompleted()
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    LaunchedEffect(authUiState) {
        when (val state = authUiState) {
            is AuthUiState.Error -> {
                coroutineScope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            is AuthUiState.Success -> {
                coroutineScope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(
                        message = "Paddock Authentication Successful!",
                        duration = SnackbarDuration.Short
                    )
                }
            }
            AuthUiState.PasswordResetSent -> {
                coroutineScope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.auth_password_reset_sent),
                        duration = SnackbarDuration.Long
                    )
                    viewModel.resetAuthUiState()
                }
            }
            else -> {}
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_img),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .statusBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectedTab == "Sign-In") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App Logo Container with Ambient Red Glow
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        // Soft Outward Radial Glow Halo
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.40f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )

                        // Rounded Container with Glowing Primary Border
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Unspecified,
                            border = BorderStroke(2.dp, F1Red),
                            modifier = Modifier.size(48.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF201E1A),
                                            Color(0xFF070907)
                                        )
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.pitwall_logo),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                    Text(
                        text = appName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                    )
                }
                Text(
                        text = stringResource(R.string.auth_brand_tagline),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 1,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "◆",
                            color = F1Red,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PITWALL APEX",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = F1Red,
                            letterSpacing = 2.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.auth_feature_headline),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.auth_feature_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.primary,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            PillButton(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(28.dp))
            if (selectedTab == "Sign-In") {
                LoginCard(
                    authUiState = authUiState,
                    onLoginClick = { email, password ->
                        viewModel.signInWithEmailAndPassword(email, password)
                    },
                    onGoogleClick = {
                        launchGoogleSignIn(context) { idToken ->
                            viewModel.signInWithGoogle(idToken)
                        }
                    },
                    onForgotPasswordClick = { email ->
                        viewModel.forgotPassword(email)
                    },
                    onGuestClick = {
                        viewModel.signInAnonymously()
                    }
                )
            } else {
                RegisterCard(
                    authUiState = authUiState,
                    navController = navController,
                    onSignInClick = { selectedTab = "Sign-In" },
                    onGoogleClick = { callsign, team, driver ->
                        launchGoogleSignIn(context) { idToken ->
                            viewModel.signInWithGoogle(
                                idToken = idToken,
                                displayName = callsign,
                                favoriteTeam = team,
                                favoriteDriver = driver
                            )
                        }
                    },
                    onRegisterClick = { email, password, callsign, team, driver ->
                        viewModel.registerWithEmailAndPassword(email, password, callsign, team, driver)
                    }
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Pinned Bottom Footer with Solid Black Background
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(vertical = 2.dp)
            ) {
                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.15f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )

                if (selectedTab == "Sign-In") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PITWALL",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.6f),
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(F1Red, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SYSTEM - v${BuildConfig.VERSION_NAME}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f),
                                letterSpacing = 1.sp
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = F1Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.auth_secure_label),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }
        }

        // Custom Pitwall Sleek Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 12.dp, start = 16.dp, end = 16.dp)
        ) { snackbarData ->
            val isError = authUiState is AuthUiState.Error
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF14141B).copy(alpha = 0.95f),
                border = BorderStroke(1.dp, if (isError) F1Red else Color(0xFF00E676)),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(if (isError) F1Red else Color(0xFF00E676), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = snackbarData.visuals.message,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}


private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    F1CompTheme {
        LoginScreen(
            modifier = Modifier,
            navController = NavController(LocalContext.current)
        )
    }
}
