package com.jenil.f1comp.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.jenil.f1comp.data.model.UserProfile
import com.jenil.f1comp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

enum class AuthErrorField { EMAIL, PASSWORD, GENERAL }

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    object Success : AuthUiState

    data class Error(
        val message: String,
        val targetField: AuthErrorField = AuthErrorField.GENERAL
    ) : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    val currentUser: StateFlow<FirebaseUser?> = authRepository.currentUserFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val currentUserProfile: StateFlow<UserProfile?> = authRepository.currentUserProfileFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun signInWithEmailAndPassword(email: String, password: String) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) {
            _authUiState.value = AuthUiState.Error("Email address cannot be empty.", AuthErrorField.EMAIL)
            return
        }
        if (password.isBlank()) {
            _authUiState.value = AuthUiState.Error("Passkey cannot be empty.", AuthErrorField.PASSWORD)
            return
        }
        val emailError = validateEmail(trimmedEmail)
        if (emailError != null) {
            _authUiState.value = AuthUiState.Error(emailError, AuthErrorField.EMAIL)
            return
        }

        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                authRepository.signInWithEmailAndPassword(trimmedEmail, password)
                _authUiState.value = AuthUiState.Success
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val (msg, field) = parseAuthException(e)
                _authUiState.value = AuthUiState.Error(msg, field)
            }
        }
    }

    fun registerWithEmailAndPassword(email: String, password: String, displayName: String = "") {
        val trimmedEmail = email.trim()
        val emailError = validateEmail(trimmedEmail)
        if (emailError != null) {
            _authUiState.value = AuthUiState.Error(emailError, AuthErrorField.EMAIL)
            return
        }
        val passwordError = validatePassword(password)
        if (passwordError != null) {
            _authUiState.value = AuthUiState.Error(passwordError, AuthErrorField.PASSWORD)
            return
        }

        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                authRepository.registerWithEmailAndPassword(trimmedEmail, password, displayName)
                _authUiState.value = AuthUiState.Success
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val (msg, field) = parseAuthException(e)
                _authUiState.value = AuthUiState.Error(msg, field)
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                authRepository.signInWithGoogle(idToken)
                _authUiState.value = AuthUiState.Success
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val (msg, field) = parseAuthException(e)
                _authUiState.value = AuthUiState.Error(msg, field)
            }
        }
    }

    fun forgotPassword(email: String) {
        val trimmedEmail = email.trim()
        val emailError = validateEmail(trimmedEmail)
        if (emailError != null) {
            _authUiState.value = AuthUiState.Error(emailError, AuthErrorField.EMAIL)
            return
        }

        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                authRepository.sendPasswordResetEmail(trimmedEmail)
                _authUiState.value = AuthUiState.Success
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val (msg, field) = parseAuthException(e)
                _authUiState.value = AuthUiState.Error(msg, field)
            }
        }
    }

    fun updateProfile(displayName: String) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                authRepository.updateProfile(displayName)
                _authUiState.value = AuthUiState.Success
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.localizedMessage ?: "Failed to update profile", AuthErrorField.GENERAL)
            }
        }
    }

    fun updateProfileImage(imageUrl: String) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                authRepository.updateProfileImage(imageUrl)
                _authUiState.value = AuthUiState.Success
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.localizedMessage ?: "Failed to update profile image", AuthErrorField.GENERAL)
            }
        }
    }

    fun updateFavoriteTeamAndDriver(favoriteTeam: String? = null, favoriteDriver: String? = null) {
        viewModelScope.launch {
            try {
                authRepository.updateFavoriteTeamAndDriver(favoriteTeam, favoriteDriver)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _authUiState.value = AuthUiState.Idle
    }

    fun resetAuthUiState() {
        _authUiState.value = AuthUiState.Idle
    }

    private fun validatePassword(password: String): String? {
        if (password.length < 8) return "Passkey must be at least 8 characters long."
        if (!password.any { it.isUpperCase() }) return "Passkey must contain at least one uppercase letter."
        if (!password.any { it.isLowerCase() }) return "Passkey must contain at least one lowercase letter."
        if (!password.any { it.isDigit() }) return "Passkey must contain at least one number."
        if (!password.any { !it.isLetterOrDigit() }) return "Passkey must contain at least one special character."
        return null
    }

    private fun validateEmail(email: String): String? {
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            return "Please enter a valid email address."
        }
        return null
    }

    private fun parseAuthException(e: Exception): Pair<String, AuthErrorField> {
        return when (e) {
            is FirebaseAuthWeakPasswordException -> {
                "The passkey provided is too weak. Please choose a stronger passkey." to AuthErrorField.PASSWORD
            }
            is FirebaseAuthInvalidUserException -> {
                "No account found for this email. Please check your email or register." to AuthErrorField.EMAIL
            }
            is FirebaseAuthInvalidCredentialsException -> {
                "Incorrect passkey or email credentials. Please check and try again." to AuthErrorField.PASSWORD
            }
            is FirebaseAuthUserCollisionException -> {
                "An account already exists with this email address. Please sign in." to AuthErrorField.EMAIL
            }
            else -> {
                val msg = e.message ?: ""
                when {
                    msg.contains("user-not-found", ignoreCase = true) ->
                        "No account found for this email. Please check your email or register." to AuthErrorField.EMAIL
                    msg.contains("wrong-password", ignoreCase = true) || msg.contains("invalid-credential", ignoreCase = true) ->
                        "Incorrect passkey or email." to AuthErrorField.PASSWORD
                    msg.contains("email-already-in-use", ignoreCase = true) || msg.contains("already in use", ignoreCase = true) ->
                        "An account already exists with this email address. Please sign in." to AuthErrorField.EMAIL
                    msg.contains("invalid-email", ignoreCase = true) ->
                        "Please enter a valid email address." to AuthErrorField.EMAIL
                    else ->
                        (e.localizedMessage ?: "An unexpected authentication error occurred.") to AuthErrorField.GENERAL
                }
            }
        }
    }
}
