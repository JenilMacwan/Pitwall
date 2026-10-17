package com.jenil.f1comp.ui.auth.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jenil.f1comp.R
import com.jenil.f1comp.data.local.entity.ConstructorProfileEntity
import com.jenil.f1comp.data.local.entity.DriverProfileEntity
import com.jenil.f1comp.ui.home.components.DriverProfileCircle
import com.jenil.f1comp.ui.home.components.TeamLogoCircle
import com.jenil.f1comp.ui.theme.F1Red
import com.jenil.f1comp.util.LegalConstants
import com.jenil.f1comp.util.ProfileUtils.defaultFallBackDrivers
import com.jenil.f1comp.util.ProfileUtils.defaultFallBackTeams
import com.jenil.f1comp.util.TeamUtils
import com.jenil.f1comp.util.openCustomTabUrl
import com.jenil.f1comp.viewmodel.AuthErrorField
import com.jenil.f1comp.viewmodel.AuthUiState

@Composable
fun RegisterCard(
    modifier: Modifier = Modifier,
    authUiState: AuthUiState = AuthUiState.Idle,
    navController: NavController? = null,
    onSignInClick: () -> Unit = {},
    onGoogleClick: (callsign: String, team: String?, driver: String?) -> Unit = { _, _, _ -> },
    onRegisterClick: (email: String, pass: String, callsign: String, team: String?, driver: String?) -> Unit = { _, _, _, _, _ -> },
    driverProfiles: List<DriverProfileEntity> = emptyList(),
    constructorProfiles: List<ConstructorProfileEntity> = emptyList()
) {
    val context = LocalContext.current
    var callsignState by rememberSaveable { mutableStateOf("") }
    var emailState by rememberSaveable { mutableStateOf("") }
    var passwordState by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }

    val isEmailError = authUiState is AuthUiState.Error && (authUiState.targetField == AuthErrorField.EMAIL || authUiState.targetField == AuthErrorField.GENERAL)
    val isPasswordError = authUiState is AuthUiState.Error && (authUiState.targetField == AuthErrorField.PASSWORD || authUiState.targetField == AuthErrorField.GENERAL)

    var selectedTeam by rememberSaveable { mutableStateOf<String?>(null) }
    var teamExpanded by rememberSaveable { mutableStateOf(false) }

    var selectedDriver by rememberSaveable { mutableStateOf<String?>(null) }
    var driverExpanded by rememberSaveable { mutableStateOf(false) }

    var showPersonalization by rememberSaveable { mutableStateOf(false) }
    var termsChecked by rememberSaveable { mutableStateOf(false) }
    var registrationStep by rememberSaveable { mutableStateOf(1) }

    val passwordStrength = remember(passwordState) { calculatePasswordStrength(passwordState) }

    LaunchedEffect(authUiState) {
        if (authUiState is AuthUiState.Error &&
            authUiState.targetField in setOf(AuthErrorField.EMAIL, AuthErrorField.PASSWORD)
        ) {
            registrationStep = 1
        }
    }

    val teams = remember(constructorProfiles) {
        if (constructorProfiles.isNotEmpty()) {
            constructorProfiles.map { it.fullName }
        } else {
            defaultFallBackTeams
        }
    }

    val drivers = remember(driverProfiles) {
        if (driverProfiles.isNotEmpty()) {
            driverProfiles.map { driver ->
                if (driver.number.isNotBlank()) "${driver.fullName} #${driver.number}" else driver.fullName
            }
        } else {
            defaultFallBackDrivers
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(if (registrationStep == 1) R.string.signup_step_account else R.string.signup_step_personalize),
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = if (registrationStep == 1) "1 / 2" else "2 / 2",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.72f)
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .animateContentSize(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0F0F14).copy(alpha = 0.90f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (registrationStep == 2) {
                    OutlinedButton(
                        onClick = { registrationStep = 1 },
                        modifier = Modifier.align(Alignment.Start),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) { Text(stringResource(R.string.signup_back_account)) }
                    Text(
                        text = listOf(callsignState.takeIf { it.isNotBlank() }, emailState).filterNotNull().joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f),
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
                    )
                }

                if (registrationStep == 1) {
                // 1. CALLSIGN / FULL NAME
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.auth_name_optional),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = callsignState,
                    onValueChange = { callsignState = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = Color.DarkGray
                        )
                    },
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLeadingIconColor = Color.DarkGray,
                        unfocusedLeadingIconColor = Color.DarkGray,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(10.dp))

                // 2. OFFICIAL EMAIL ADDRESS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.auth_email_address),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = emailState,
                    onValueChange = { emailState = it },
                    isError = isEmailError,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null,
                            tint = Color.DarkGray
                        )
                    },
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = if (isEmailError) F1Red else Color.White,
                        unfocusedBorderColor = if (isEmailError) F1Red else Color.White,
                        errorBorderColor = F1Red,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLeadingIconColor = Color.DarkGray,
                        unfocusedLeadingIconColor = Color.DarkGray,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    maxLines = 1
                )
                if (isEmailError && authUiState is AuthUiState.Error && authUiState.targetField == AuthErrorField.EMAIL) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = authUiState.message,
                        color = F1Red,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                }

                if (registrationStep == 2) {
                OutlinedButton(
                    onClick = { showPersonalization = !showPersonalization },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text(stringResource(if (showPersonalization) R.string.auth_hide_optional_favorites else R.string.auth_add_optional_favorites))
                }
                Text(
                    text = stringResource(R.string.auth_favorites_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 4.dp, bottom = 8.dp)
                )

                if (showPersonalization) {
                // Optional personalization: account creation does not depend on these choices.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.auth_favorite_team),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF141418),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { teamExpanded = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TeamLogoCircle(
                                logoUrl = TeamUtils.getTeamLogoUrl(selectedTeam, constructorProfiles),
                                teamName = selectedTeam ?: "Team",
                                size = 28.dp,
                                containerColor = Color.White
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = selectedTeam ?: stringResource(R.string.auth_select_team),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selectedTeam != null) Color.White else Color.White.copy(alpha = 0.5f),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = teamExpanded,
                        onDismissRequest = { teamExpanded = false },
                        modifier = Modifier
                            .widthIn(min = 240.dp, max = 340.dp)
                            .heightIn(max = 420.dp),
                        shape = RoundedCornerShape(16.dp),
                        containerColor = Color(0xFF1A1A22),
                        tonalElevation = 4.dp,
                        shadowElevation = 8.dp,
                    ) {
                        DropdownMenuItem(
                            enabled = false,
                            text = {
                                Text(
                                    "Choose constructor",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            onClick = {},
                        )

                        teams.forEach { team ->
                            val logoUrl =
                                TeamUtils.getTeamLogoUrl(team, constructorProfiles)
                            val selected = team == selectedTeam

                            DropdownMenuItem(
                                leadingIcon = {
                                    TeamLogoCircle(
                                        logoUrl = logoUrl,
                                        teamName = team,
                                        size = 32.dp,
                                        containerColor = Color.White,
                                    )
                                },
                                text = {
                                    Text(
                                        text = team,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                },
                                trailingIcon = {
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = stringResource(R.string.common_selected),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                },
                                onClick = {
                                    selectedTeam = team
                                    teamExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // 4. FAVORITE DRIVER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.auth_favorite_driver),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF141418),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { driverExpanded = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DriverProfileCircle(
                                imageUrl = TeamUtils.getDriverImageUrl(selectedDriver,driverProfiles),
                                driverName = selectedDriver ?: "Driver",
                                size = 28.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = selectedDriver ?: stringResource(R.string.auth_select_driver),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selectedDriver != null) Color.White else Color.White.copy(alpha = 0.5f),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = driverExpanded,
                        onDismissRequest = { driverExpanded = false },
                        modifier = Modifier
                            .widthIn(min = 240.dp, max = 340.dp)
                            .heightIn(max = 420.dp),
                        shape = RoundedCornerShape(16.dp),
                        containerColor = Color(0xFF1A1A22),
                        tonalElevation = 4.dp,
                        shadowElevation = 8.dp,
                    ) {
                        DropdownMenuItem(
                            enabled = false,
                            text = {
                                Text(
                                    "Choose driver",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            onClick = {},
                        )
                        drivers.forEach { driver ->
                            val driverImg =
                                TeamUtils.getDriverImageUrl(driver, driverProfiles)
                            val selected = driver == selectedDriver
                            DropdownMenuItem(
                                leadingIcon = {
                                    DriverProfileCircle(
                                        imageUrl = driverImg,
                                        driverName = driver,
                                        size = 24.dp,
                                        driverProfiles = driverProfiles,
                                        containerColor = Color.White
                                    )
                                },
                                text = {
                                    Text(
                                        text = driver,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                },
                                trailingIcon = {
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = stringResource(R.string.common_selected),
                                            tint = MaterialTheme.colorScheme.primary,
                                        )
                                    }
                                },
                                onClick = {
                                    selectedDriver = driver
                                    driverExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                }
                }

                if (registrationStep == 1) {
                // 5. CREATE PASSWORD
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.auth_password),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = passwordState,
                    onValueChange = { passwordState = it },
                    isError = isPasswordError,
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = Color.DarkGray
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisibility = !passwordVisibility
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.RemoveRedEye,
                                contentDescription = stringResource(R.string.auth_toggle_password),
                                tint = Color.DarkGray
                            )
                        }
                    },
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = if (isPasswordError) F1Red else Color.White,
                        unfocusedBorderColor = if (isPasswordError) F1Red else Color.White,
                        errorBorderColor = F1Red,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLeadingIconColor = Color.DarkGray,
                        unfocusedLeadingIconColor = Color.DarkGray,
                        focusedTrailingIconColor = Color.DarkGray,
                        unfocusedTrailingIconColor = Color.DarkGray,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    maxLines = 1
                )
                if (isPasswordError && authUiState is AuthUiState.Error && authUiState.targetField == AuthErrorField.PASSWORD) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = authUiState.message,
                        color = F1Red,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                // 6. STRENGTH SPECTRUM
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.auth_password_requirements),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(
                            when (passwordStrength.label) {
                                "Set a password" -> R.string.auth_set_password
                                "Strong" -> R.string.auth_strength_strong
                                "Good" -> R.string.auth_strength_good
                                "Fair" -> R.string.auth_strength_fair
                                else -> R.string.auth_strength_weak
                            }
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = passwordStrength.color
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) { index ->
                        val isBarActive = index < passwordStrength.score
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .background(
                                    color = if (isBarActive) passwordStrength.color else Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        if (index < 3) {
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Password Requirements Indicators
                Row(
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RequirementChip(label = stringResource(R.string.auth_password_length), isMet = passwordStrength.hasMinLength)
                    RequirementChip(label = "A-Z", isMet = passwordStrength.hasUppercase)
                    RequirementChip(label = "a-z", isMet = passwordStrength.hasLowercase)
                    RequirementChip(label = "0-9", isMet = passwordStrength.hasDigit)
                    RequirementChip(label = "!@#", isMet = passwordStrength.hasSpecialChar)
                }
                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { registrationStep = 2 },
                    enabled = emailState.isNotBlank() && passwordStrength.isStrong,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = F1Red)
                ) {
                    Text(stringResource(R.string.signup_continue))
                }
                }

                if (registrationStep == 2) {
                // Required legal acceptance is explicit; optional marketing consent is not collected here.
                Row(
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoundedCheckbox(
                        checked = termsChecked,
                        onCheckedChange = { termsChecked = it },
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    val termsText = buildAnnotatedString {
                        append("I accept the ")
                        withLink(
                            LinkAnnotation.Clickable(
                                tag = "TERMS",
                                styles = TextLinkStyles(
                                    style = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = TextDecoration.Underline,
                                        color = Color.White
                                    )
                                ),
                                linkInteractionListener = {
                                    openCustomTabUrl(context, LegalConstants.TERMS_CONDITIONS_URL)
                                }
                            )
                        ) {
                            append("PitWall Terms")
                        }
                        append(".")
                    }
                    Text(
                        text = termsText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // 8. CREATE PADDOCK ACCOUNT BUTTON
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        F1Red.copy(alpha = 0.6f),
                                        F1Red.copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                    )
                    val isLoading = authUiState is AuthUiState.Loading
                    Button(
                        onClick = {
                            onRegisterClick(
                                emailState,
                                passwordState,
                                callsignState,
                                selectedTeam,
                                selectedDriver
                            )
                        },
                        enabled = !isLoading && emailState.isNotBlank() && passwordStrength.isStrong && termsChecked,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE10600),
                            contentColor = Color.White
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                            text = stringResource(R.string.auth_create_account),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
                
                // Divider: // OR CONNECT VIA TELEMETRY //
                Row(
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )
                    Text(
                        text = stringResource(R.string.auth_or_sign_up_with),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )
                }

                // Social Login Button: Google
                val isLoadingG = authUiState is AuthUiState.Loading
                OutlinedButton(
                    onClick = {
                        onGoogleClick(callsignState, selectedTeam, selectedDriver)
                    },
                    enabled = !isLoadingG && termsChecked,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(44.dp),
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.06f),
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.auth_continue_google),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 9. FOOTER LINK (Highlighted Surface Container)
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF141418).copy(alpha = 0.90f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            modifier = Modifier.clickable { onSignInClick() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.auth_already_have_account) + " ",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                        text = stringResource(R.string.auth_sign_in),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = F1Red,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private data class PasswordStrength(
    val score: Int,
    val label: String,
    val color: Color,
    val hasMinLength: Boolean,
    val hasUppercase: Boolean,
    val hasLowercase: Boolean,
    val hasDigit: Boolean,
    val hasSpecialChar: Boolean
) {
    val isStrong: Boolean
        get() = hasMinLength && hasUppercase && hasLowercase && hasDigit && hasSpecialChar
}

private fun calculatePasswordStrength(password: String): PasswordStrength {
    if (password.isEmpty()) {
        return PasswordStrength(
            score = 0,
            label = "Set a password",
            color = Color.Gray,
            hasMinLength = false,
            hasUppercase = false,
            hasLowercase = false,
            hasDigit = false,
            hasSpecialChar = false
        )
    }

    val hasMinLength = password.length >= 8
    val hasUppercase = password.any { it.isUpperCase() }
    val hasLowercase = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() }

    var score = 0
    if (hasUppercase) score++
    if (hasLowercase) score++
    if (hasDigit) score++
    if (hasSpecialChar) score++

    val (label, color) = when {
        score == 4 && hasMinLength -> "Strong" to Color(0xFF00E676)
        score >= 3 -> "Good" to Color(0xFFFFD700)
        score >= 2 -> "Fair" to Color(0xFFFF9800)
        else -> "Weak" to Color(0xFFE10600)
    }

    return PasswordStrength(
        score = score,
        label = label,
        color = color,
        hasMinLength = hasMinLength,
        hasUppercase = hasUppercase,
        hasLowercase = hasLowercase,
        hasDigit = hasDigit,
        hasSpecialChar = hasSpecialChar
    )
}

@Composable
private fun RequirementChip(label: String, isMet: Boolean) {
    val activeColor = Color(0xFF00E676)
    val inactiveColor = Color.White.copy(alpha = 0.65f)
    Text(
        text = if (isMet) "✓ $label" else "• $label",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = if (isMet) FontWeight.Bold else FontWeight.Normal,
        color = if (isMet) activeColor else inactiveColor
    )
}


@Composable
fun RoundedCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    checkedColor: Color = F1Red,
    uncheckedBorderColor: Color = Color.White.copy(alpha = 0.6f),
    cornerRadius: Dp = 6.dp
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(48.dp)
            .toggleable(value = checked, onValueChange = onCheckedChange, role = Role.Checkbox)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(shape)
                .background(if (checked) checkedColor else Color.Transparent)
                .border(
                    width = if (checked) 0.dp else 1.5.dp,
                    color = if (checked) Color.Transparent else uncheckedBorderColor,
                    shape = shape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
