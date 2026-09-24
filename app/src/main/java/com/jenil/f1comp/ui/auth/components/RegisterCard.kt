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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
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
import com.jenil.f1comp.util.ProfileUtils.defaultFallBackDrivers
import com.jenil.f1comp.util.ProfileUtils.defaultFallBackTeams
import com.jenil.f1comp.util.TeamUtils
import com.jenil.f1comp.viewmodel.AuthErrorField
import com.jenil.f1comp.viewmodel.AuthUiState

@Composable
fun RegisterCard(
    modifier: Modifier = Modifier,
    authUiState: AuthUiState = AuthUiState.Idle,
    navController: NavController? = null,
    onSignInClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {},
    onRegisterClick: (email: String, pass: String, callsign: String) -> Unit = { _, _, _ -> },
    driverProfiles: List<DriverProfileEntity> = emptyList(),
    constructorProfiles: List<ConstructorProfileEntity> = emptyList()
) {
    var callsignState by remember { mutableStateOf("") }
    var emailState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    val isEmailError = authUiState is AuthUiState.Error && (authUiState.targetField == AuthErrorField.EMAIL || authUiState.targetField == AuthErrorField.GENERAL)
    val isPasswordError = authUiState is AuthUiState.Error && (authUiState.targetField == AuthErrorField.PASSWORD || authUiState.targetField == AuthErrorField.GENERAL)

    var selectedTeam by remember { mutableStateOf<String?>(null) }
    var teamExpanded by remember { mutableStateOf(false) }

    var selectedDriver by remember { mutableStateOf<String?>(null) }
    var driverExpanded by remember { mutableStateOf(false) }

    var alertsChecked by remember { mutableStateOf(true) }
    var termsChecked by remember { mutableStateOf(true) }

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

    val neonGreen = Color(0xFF00E676)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                // 1. CALLSIGN / FULL NAME
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CALLSIGN / FULL NAME",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "REQ // S-01",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = callsignState,
                    onValueChange = { callsignState = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "User Icon",
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
                        text = "OFFICIAL EMAIL ADDRESS",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "COMM LINK",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
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
                            contentDescription = "Email Icon",
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
                Spacer(modifier = Modifier.height(10.dp))

                // 3. FAVORITE CONSTRUCTOR / TEAM
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "FAVORITE CONSTRUCTOR / TEAM",
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
                                text = selectedTeam ?: "Select Team",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selectedTeam != null) Color.White else Color.White.copy(alpha = 0.5f),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                contentDescription = "Dropdown",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = teamExpanded,
                        onDismissRequest = { teamExpanded = false },
                        modifier = Modifier
                            .widthIn(max = 488.dp)
                            .background(Color(0xFF1A1A22))
                    ) {
                        teams.forEach { team ->
                            val logoUrl = TeamUtils.getTeamLogoUrl(team, constructorProfiles)
                            DropdownMenuItem(
                                leadingIcon = {
                                    TeamLogoCircle(
                                        logoUrl = logoUrl,
                                        teamName = team,
                                        size = 24.dp,
                                        containerColor = Color.White
                                    )
                                },
                                text = { Text(team, color = Color.White) },
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
                        text = "FAVORITE DRIVER / RACER",
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
                                text = selectedDriver ?: "Select Driver",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selectedDriver != null) Color.White else Color.White.copy(alpha = 0.5f),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                contentDescription = "Dropdown",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = driverExpanded,
                        onDismissRequest = { driverExpanded = false },
                        modifier = Modifier
                            .widthIn(max = 488.dp)
                            .background(Color(0xFF1A1A22))
                    ) {
                        drivers.forEach { driver ->
                            val driverImg = TeamUtils.getDriverImageUrl(driver, driverProfiles)
                            DropdownMenuItem(
                                leadingIcon = {
                                    DriverProfileCircle(
                                        imageUrl = driverImg,
                                        driverName = driver,
                                        size = 24.dp,
                                        containerColor = Color.White
                                    )
                                },
                                text = { Text(driver, color = Color.White) },
                                onClick = {
                                    selectedDriver = driver
                                    driverExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // 5. CREATE PADDOCK PASSKEY
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CREATE PADDOCK PASSKEY",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "ENCRYPTED // SHA-256",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = neonGreen
                    )
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
                            contentDescription = "Lock Icon",
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
                                contentDescription = "Toggle password visibility",
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
                val passwordStrength = remember(passwordState) { calculatePasswordStrength(passwordState) }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STRENGTH SPECTRUM",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = passwordStrength.label,
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
                    RequirementChip(label = "8+ Chars", isMet = passwordStrength.hasMinLength)
                    RequirementChip(label = "A-Z", isMet = passwordStrength.hasUppercase)
                    RequirementChip(label = "a-z", isMet = passwordStrength.hasLowercase)
                    RequirementChip(label = "0-9", isMet = passwordStrength.hasDigit)
                    RequirementChip(label = "!@#", isMet = passwordStrength.hasSpecialChar)
                }
                Spacer(modifier = Modifier.height(14.dp))

                // 7. CHECKBOXES
                // Checkbox 1: Audio alerts
                Row(
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoundedCheckbox(
                        checked = alertsChecked,
                        onCheckedChange = { alertsChecked = it },
                        modifier = Modifier.padding(start = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    val audioText = buildAnnotatedString {
                        append("Receive ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) {
                            append("updates, promotions and other information")
                        }
                        append(" about the app over mail.")
                    }
                    Text(
                        text = audioText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                // Checkbox 2: Terms & Regulations
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
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline, color = Color.White)) {
                            append("PitWall Terms")
                        }
                        append(" & Data Regulations.")
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
                                callsignState
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
                                text = "///  CREATE PADDOCK ACCOUNT   ->",
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
                        text = "//  OR CONNECT VIA TELEMETRY  //",
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
                        onGoogleClick()
                    },
                    enabled = !isLoadingG,
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
                            contentDescription = "Google",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Continue with Google",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
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
                    text = "Already holding paddock credentials? ",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "SIGN IN",
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
            label = "ENTER PASSKEY",
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
        score == 4 && hasMinLength -> "TELEMETRY SECURE // STRONG" to Color(0xFF00E676)
        score >= 3 -> "OPTIMAL // GOOD" to Color(0xFFFFD700)
        score >= 2 -> "MODERATE" to Color(0xFFFF9800)
        else -> "WEAK // INSECURE" to Color(0xFFE10600)
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
    val inactiveColor = Color.White.copy(alpha = 0.35f)
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
            .size(20.dp)
            .clip(shape)
            .background(if (checked) checkedColor else Color.Transparent)
            .border(
                width = if (checked) 0.dp else 1.5.dp,
                color = if (checked) Color.Transparent else uncheckedBorderColor,
                shape = shape
            )
            .clickable { onCheckedChange(!checked) }
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