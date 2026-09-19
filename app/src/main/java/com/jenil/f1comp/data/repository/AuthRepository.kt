package com.jenil.f1comp.data.repository

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.jenil.f1comp.data.model.UserProfile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    val currentUserFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUserProfileFlow: Flow<UserProfile?> = currentUserFlow.flatMapLatest { user ->
        if (user == null) {
            flowOf(null)
        } else {
            callbackFlow {
                val listener = firestore.collection("users")
                    .document(user.uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }
                        val profile = snapshot?.toObject(UserProfile::class.java)
                        trySend(profile)
                    }
                awaitClose { listener.remove() }
            }
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw Exception("Sign-in failed")
        syncUserToCloud(user)
        return user
    }

    suspend fun registerWithEmailAndPassword(email: String, password: String, displayName: String = "") {
        require(
            password.length >= 8 &&
                    password.any { it.isUpperCase() } &&
                    password.any { it.isLowerCase() } &&
                    password.any { it.isDigit() } &&
                    password.any { !it.isLetterOrDigit() }
        ) {
            "Password does not meet security requirements (8+ chars, upper, lower, number, special character)."
        }
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw Exception("Registration failed")
        if (displayName.isNotBlank()) {
            val request = userProfileChangeRequest {
                this.displayName = displayName
            }
            user.updateProfile(request).await()
        }
        user.sendEmailVerification().await()
        syncUserToCloud(user)
    }

    suspend fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth.signInWithCredential(credential).await()
        val user = result.user ?: throw Exception("Google sign-in failed")
        syncUserToCloud(user)
    }

    suspend fun updateProfile(displayName: String) {
        val user = firebaseAuth.currentUser ?: return
        val request = userProfileChangeRequest {
            this.displayName = displayName
        }
        user.updateProfile(request).await()
        syncUserToCloud(user)
    }

    suspend fun updateProfileImage(imageUrl: String) {
        val user = firebaseAuth.currentUser ?: return
        val request = userProfileChangeRequest {
            this.photoUri = imageUrl.toUri()
        }
        user.updateProfile(request).await()
        syncUserToCloud(user)
    }

    suspend fun updateFavoriteTeamAndDriver(favoriteTeam: String? = null, favoriteDriver: String? = null) {
        val user = firebaseAuth.currentUser ?: return
        val updates = mutableMapOf<String, Any>()
        if (favoriteTeam != null) updates["favoriteTeam"] = favoriteTeam
        if (favoriteDriver != null) updates["favoriteDriver"] = favoriteDriver

        if (updates.isNotEmpty()) {
            try {
                firestore.collection("users")
                    .document(user.uid)
                    .set(updates, SetOptions.merge())
                    .await()
            } catch (e: Exception) {
                Log.e("AuthRepository", "Failed to update favorites in Firestore: ${e.message}", e)
            }
        }
    }

    suspend fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    private suspend fun syncUserToCloud(user: FirebaseUser) {
        val userMap = mutableMapOf<String, Any>(
            "uid" to user.uid,
            "email" to (user.email ?: ""),
            "displayName" to (user.displayName ?: ""),
            "photoUrl" to (user.photoUrl?.toString() ?: "")
        )
        try {
            firestore.collection("users")
                .document(user.uid)
                .set(userMap, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to sync user to Firestore: ${e.message}", e)
        }
    }
}
