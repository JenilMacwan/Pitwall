package com.jenil.f1comp.ui.auth.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.jenil.f1comp.R
import com.jenil.f1comp.ui.theme.F1Red
import com.jenil.f1comp.viewmodel.AuthErrorField
import com.jenil.f1comp.viewmodel.AuthUiState

@Composable
fun LoginCard(
    modifier: Modifier = Modifier,
    authUiState: AuthUiState = AuthUiState.Idle,
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onGoogleClick: () -> Unit = {},
    onForgotPasswordClick: (String) -> Unit = {},
    onGuestClick: () -> Unit = {},
) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val passwordVisibility = remember { mutableStateOf(false) }
    val checkState = remember { mutableStateOf(false) }
    val isLoading = authUiState is AuthUiState.Loading

    val isEmailError = authUiState is AuthUiState.Error && (authUiState.targetField == AuthErrorField.EMAIL || authUiState.targetField == AuthErrorField.GENERAL)
    val isPasswordError = authUiState is AuthUiState.Error && (authUiState.targetField == AuthErrorField.PASSWORD || authUiState.targetField == AuthErrorField.GENERAL)

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#",
                        style = MaterialTheme.typography.bodyMedium,
                        color = F1Red
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "TEAM CREDENTIALS / EMAIL",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "HUD // 01",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = emailState.value,
                    onValueChange = { emailState.value = it },
                    isError = isEmailError,
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
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#",
                        style = MaterialTheme.typography.bodyMedium,
                        color = F1Red
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "PADDOCK PASSKEY",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "SHA-256 ENCRYPTED",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = passwordState.value,
                    onValueChange = { passwordState.value = it },
                    isError = isPasswordError,
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisibility.value = !passwordVisibility.value
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
                    visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
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
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        modifier = Modifier.clip(RoundedCornerShape(4.dp)),
                        checked = checkState.value,
                        onCheckedChange = {
                            checkState.value = it
                        }
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Remember Pit Pass",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Forgot Passkey ?",
                        style = MaterialTheme.typography.bodySmall,
                        color = F1Red,
                        modifier = Modifier.clickable {
                            onForgotPasswordClick(emailState.value)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .widthIn(max = 320.dp)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    // Soft Red Glow behind the button
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
                    Button(
                        onClick = {
                            onLoginClick(emailState.value, passwordState.value)
                        },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = F1Red,
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
                                text = "ACCESS PITWALL PADDOCK /// >>>",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
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
                OutlinedButton(
                    onClick = {
                        onGoogleClick()
                    },
                    enabled = !isLoading,
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

        // Guest Option Centered Outside Card
        Text(
            text = "CONTINUE AS PADDOCK GUEST  ->",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier
                .clickable { onGuestClick() }
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}