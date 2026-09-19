package com.jenil.f1comp.ui.profile.screen

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jenil.f1comp.BuildConfig
import com.jenil.f1comp.R
import com.jenil.f1comp.ui.F1ScreenPadding
import com.jenil.f1comp.ui.theme.F1Red
import com.jenil.f1comp.ui.theme.PodiumGold
import com.jenil.f1comp.viewmodel.AuthUiState
import com.jenil.f1comp.viewmodel.AuthViewModel
import com.jenil.f1comp.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun UserProfileScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsStateWithLifecycle()
    val authUiState by authViewModel.authUiState.collectAsStateWithLifecycle()
    val userProfile by authViewModel.currentUserProfile.collectAsStateWithLifecycle()

    val favoriteTeamPref by settingsViewModel.favoriteTeam.collectAsStateWithLifecycle()
    val favoriteDriverPref by settingsViewModel.favoriteDriver.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showSignOutConfirm by remember { mutableStateOf(false) }

    var editCallsign by remember(currentUser, userProfile) {
        mutableStateOf(currentUser?.displayName?.ifBlank { null } ?: userProfile?.displayName ?: "")
    }
    var editPhotoUrl by remember(currentUser, userProfile) {
        mutableStateOf(currentUser?.photoUrl?.toString()?.ifBlank { null } ?: userProfile?.photoUrl ?: "")
    }

    val currentFavTeam = userProfile?.favoriteTeam?.ifBlank { null } ?: favoriteTeamPref ?: "Scuderia Ferrari HP"
    val currentFavDriver = userProfile?.favoriteDriver?.ifBlank { null } ?: favoriteDriverPref ?: "Charles Leclerc #16"

    var selectedTeam by remember(currentFavTeam) { mutableStateOf(currentFavTeam) }
    var teamExpanded by remember { mutableStateOf(false) }

    var selectedDriver by remember(currentFavDriver) { mutableStateOf(currentFavDriver) }
    var driverExpanded by remember { mutableStateOf(false) }

    // --- System Image Picker Launcher ---
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            editPhotoUrl = uri.toString()
            authViewModel.updateProfileImage(uri.toString())
            scope.launch {
                snackbarHostState.showSnackbar("Updating profile avatar...")
            }
        }
    }

    val teams = listOf(
        "Scuderia Ferrari HP",
        "Oracle Red Bull Racing",
        "Mercedes-AMG Petronas F1 Team",
        "McLaren Formula 1 Team",
        "Aston Martin Aramco F1 Team",
        "BWT Alpine F1 Team",
        "Williams Racing",
        "Visa Cash App RB F1 Team",
        "Stake F1 Team Kick Sauber",
        "MoneyGram Haas F1 Team"
    )

    val drivers = listOf(
        "Charles Leclerc #16",
        "Lewis Hamilton #44",
        "Max Verstappen #1",
        "Isack Hadjar #6",
        "Lando Norris #4",
        "Oscar Piastri #81",
        "Carlos Sainz #55",
        "George Russell #63",
        "Fernando Alonso #14",
        "Liam Lawson #30",
        "Yuki Tsunoda #22",
        "Valtteri Bottas #77",
        "Pierre Gasly #10",
        "Alexander Albon #23",
        "Lance Stroll #18",
        "Sergio Pérez #11",
        "Esteban Ocon #31"
    )

    val neonGreen = Color(0xFF00E676)

    LaunchedEffect(authUiState) {
        when (val state = authUiState) {
            is AuthUiState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        duration = SnackbarDuration.Short
                    )
                }
                authViewModel.resetAuthUiState()
            }
            is AuthUiState.Success -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Profile Updated Successfully!",
                        duration = SnackbarDuration.Short
                    )
                }
                authViewModel.resetAuthUiState()
            }
            else -> {}
        }
    }

    // --- Edit Profile Dialog ---
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "EDIT PADDOCK PROFILE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column {
                    Text(
                        text = "CALLSIGN / DISPLAY NAME",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = editCallsign,
                        onValueChange = { editCallsign = it },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = F1Red,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "PROFILE AVATAR",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = "Pick Image",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SELECT IMAGE FROM GALLERY", style = MaterialTheme.typography.labelMedium)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEditProfileDialog = false
                        if (editCallsign.isNotBlank() && editCallsign != currentUser?.displayName) {
                            authViewModel.updateProfile(editCallsign.trim())
                        }
                    }
                ) {
                    Text("SAVE CHANGES", color = F1Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("CANCEL", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // --- Sign Out Confirmation Dialog ---
    if (showSignOutConfirm) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirm = false },
            containerColor = MaterialTheme.colorScheme.surface,
            icon = {
                Icon(
                    imageVector = Icons.Rounded.WarningAmber,
                    contentDescription = null,
                    tint = F1Red
                )
            },
            title = {
                Text(
                    text = "DISCONNECT FROM PITWALL?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Signing out will clear active paddock session tokens on this device.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutConfirm = false
                        authViewModel.signOut()
                        scope.launch {
                            snackbarHostState.showSnackbar("Signed out successfully.")
                        }
                    }
                ) {
                    Text("SIGN OUT", color = F1Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirm = false }) {
                    Text("CANCEL", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- Header Top Bar ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "PADDOCK DRIVER PROFILE",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // --- Scrollable Content ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // ==========================================
                // 1. USER HERO CARD (Logged In vs Guest)
                // ==========================================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (currentUser != null) {
                            val user = currentUser!!
                            val displayName = when {
                                !user.displayName.isNullOrBlank() -> user.displayName!!
                                !userProfile?.displayName.isNullOrBlank() -> userProfile!!.displayName
                                else -> "Paddock Driver"
                            }
                            val photoUrl = user.photoUrl ?: userProfile?.photoUrl?.let { if (it.isNotBlank()) it.toUri() else null }

                            // Profile Image Container with Glow (Clickable to pick image)
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clickable { imagePickerLauncher.launch("image/*") }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(88.dp)
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    F1Red.copy(alpha = 0.8f),
                                                    F1Red.copy(alpha = 0.3f),
                                                    Color.Transparent
                                                )
                                            ),
                                            shape = CircleShape
                                        )
                                )
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(2.dp, F1Red),
                                    modifier = Modifier.size(76.dp)
                                ) {
                                    if (photoUrl != null) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(photoUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "User Avatar",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_profile),
                                                contentDescription = "Default Avatar",
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Callsign & Verification Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = displayName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Outlined.Verified,
                                    contentDescription = "Verified Driver",
                                    tint = neonGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = user.email ?: "No email linked",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Badge Row (PADDOCK PASS ACTIVE)
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = F1Red.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, F1Red.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(neonGreen, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "PADDOCK PASS ACTIVE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action Buttons: Edit Profile & Sign Out
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showEditProfileDialog = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surface,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Edit Profile", style = MaterialTheme.typography.labelMedium)
                                }

                                OutlinedButton(
                                    onClick = { showSignOutConfirm = true },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, F1Red.copy(alpha = 0.6f)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = F1Red.copy(alpha = 0.1f),
                                        contentColor = F1Red
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                                        contentDescription = null,
                                        tint = F1Red,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Sign Out", style = MaterialTheme.typography.labelMedium, color = F1Red)
                                }
                            }
                        } else {
                            // GUEST USER CARD
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.size(72.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_profile),
                                            contentDescription = "Guest Avatar",
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "GUEST PADDOCK PASS",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Sign in or initialize your Driver Profile to sync telemetry, alerts, and custom preferences across devices.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { navController.navigate("login") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = F1Red,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "///  SIGN IN / INITIALIZE PASS   ->",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 2. FAVORITE CONSTRUCTOR & DRIVER SELECTION
                // ==========================================
                ProfileSectionHeader(title = "FAVORITE CONSTRUCTOR & DRIVER")

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // 1. Favorite Team
                        Text(
                            text = "FAVORITE CONSTRUCTOR / TEAM",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { teamExpanded = true }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(F1Red, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = selectedTeam,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Outlined.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = teamExpanded,
                                onDismissRequest = { teamExpanded = false },
                                modifier = Modifier
                                    .widthIn(max = 340.dp)
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                teams.forEach { team ->
                                    DropdownMenuItem(
                                        text = { Text(team, color = MaterialTheme.colorScheme.onSurface) },
                                        onClick = {
                                            selectedTeam = team
                                            teamExpanded = false
                                            settingsViewModel.setFavoriteTeam(team)
                                            authViewModel.updateFavoriteTeamAndDriver(favoriteTeam = team)
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Favorite Constructor set to $team")
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 2. Favorite Driver
                        Text(
                            text = "FAVORITE RACER / DRIVER",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { driverExpanded = true }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(PodiumGold, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = selectedDriver,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Outlined.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = driverExpanded,
                                onDismissRequest = { driverExpanded = false },
                                modifier = Modifier
                                    .widthIn(max = 340.dp)
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                drivers.forEach { driver ->
                                    DropdownMenuItem(
                                        text = { Text(driver, color = MaterialTheme.colorScheme.onSurface) },
                                        onClick = {
                                            selectedDriver = driver
                                            driverExpanded = false
                                            settingsViewModel.setFavoriteDriver(driver)
                                            authViewModel.updateFavoriteTeamAndDriver(favoriteDriver = driver)
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Favorite Driver set to $driver")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 3. QUICK PREFERENCES & APP SETTINGS
                // ==========================================
                ProfileSectionHeader(title = "PREFERENCES & SETTINGS")

                ProfileMenuItem(
                    icon = Icons.Outlined.Settings,
                    title = "App Settings",
                    subtitle = "Notifications, Calendar Sync, & Local Data",
                    onClick = { navController.navigate("settings") }
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.Palette,
                    title = "Appearance & Theme",
                    subtitle = "Theme colors, constructor branding, dark mode",
                    onClick = { navController.navigate("theme_settings") }
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.Language,
                    title = "Language & Region",
                    subtitle = "Driver name display & date formatting",
                    onClick = { navController.navigate("language_settings") }
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.Security,
                    title = "Live Telemetry & Security",
                    subtitle = "Paddock security passkeys & telemetry options",
                    onClick = { navController.navigate("telemetry") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 4. HELP, SUPPORT & COMMUNITY
                // ==========================================
                ProfileSectionHeader(title = "HELP & SUPPORT")

                // Email Support
                ProfileMenuItem(
                    icon = Icons.Outlined.Email,
                    title = "Email Support",
                    subtitle = "Contact PitWall development team via email",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:support.pitwall@gmail.com".toUri()
                            putExtra(Intent.EXTRA_SUBJECT, "PitWall App Support [v${BuildConfig.VERSION_NAME}]")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "No email client found", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // WhatsApp Support / Community
                ProfileMenuItem(
                    icon = Icons.Outlined.SupportAgent,
                    title = "WhatsApp Support & Community",
                    subtitle = "Join WhatsApp paddock channel or chat with support",
                    onClick = {
                        val uri = "https://wa.me/?text=Hello%20PitWall%20Support!".toUri()
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "Could not open WhatsApp", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // Rate App
                ProfileMenuItem(
                    icon = Icons.Outlined.StarRate,
                    title = "Rate PitWall on Play Store",
                    subtitle = "Enjoying the app? Leave us a 5-star review",
                    onClick = {
                        val uri = "market://details?id=${context.packageName}".toUri()
                        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                            setPackage("com.android.vending")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    "https://play.google.com/store/apps/details?id=${context.packageName}".toUri()
                                )
                            )
                        }
                    }
                )

                // Open Source Licenses
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Outlined.HelpOutline,
                    title = "Open Source Licenses",
                    subtitle = "Third-party libraries & license attributions",
                    onClick = { navController.navigate("licenses") }
                )

                // Privacy Policy
                ProfileMenuItem(
                    icon = Icons.Outlined.Policy,
                    title = "Privacy Policy",
                    subtitle = "How telemetry & authentication data is protected",
                    onClick = { navController.navigate("privacy_policy") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 5. OFFICIAL SOCIAL LINKS & COMMUNITY
                // ==========================================
                ProfileSectionHeader(title = "PITWALL SOCIALS & COMMUNITY")

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SocialButton(
                                label = "Twitter / X",
                                onClick = {
                                    openUrl(context, "https://x.com/f1")
                                }
                            )
                            SocialButton(
                                label = "Instagram",
                                onClick = {
                                    openUrl(context, "https://instagram.com/f1")
                                }
                            )
                            SocialButton(
                                label = "Share App",
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "Check out PitWall F1 Companion: https://play.google.com/store/apps/details?id=${context.packageName}")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share PitWall"))
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ==========================================
                // 6. MODERN TELEMETRY FOOTER WITH TAGLINE
                // ==========================================
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, F1Red.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(F1Red, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PITWALL TELEMETRY ENGINE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = 2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Tagline Badge: "Built for the fans with love in India 🇮🇳"
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Built for the fans with ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "❤️ ",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "in India ",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "🇮🇳",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "F1Companion v${BuildConfig.VERSION_NAME} • All Rights Reserved",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(F1ScreenPadding.bottomPadding()))
            }
        }
    }
}

@Composable
private fun ProfileSectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = F1Red,
            letterSpacing = 1.5.sp
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "->",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = F1Red
            )
        }
    }
}

@Composable
private fun SocialButton(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

private fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Cannot open link", Toast.LENGTH_SHORT).show()
    }
}
