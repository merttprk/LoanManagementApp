package com.loanmanagementapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.loanmanagementapp.data.remote.model.User
import com.loanmanagementapp.domain.repository.AuthRepository
import com.loanmanagementapp.presentation.state.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "loan_management_prefs", Context.MODE_PRIVATE
    )

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Authentication failed")
            
            // Get user data from Firestore
            val userDoc = firestore.collection("users").document(userId).get().await()
            if (userDoc.exists()) {
                val user = User(
                    id = userId,
                    email = email,
                    name = userDoc.getString("name") ?: "",
                )
                
                // Save user session
                saveUserSession(user)
                
                Result.Success(user)
            } else {
                Result.Error(Exception("User data not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun loginWithGoogle(googleAccount: GoogleSignInAccount): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val userId = authResult.user?.uid ?: throw Exception("Authentication failed")
            val email = googleAccount.email ?: throw Exception("Email not available")
            
            // Check if user exists in Firestore
            val userDoc = firestore.collection("users").document(userId).get().await()
            val user = if (userDoc.exists()) {
                User(
                    id = userId,
                    email = email,
                    name = userDoc.getString("name") ?: googleAccount.displayName ?: "",
                )
            } else {
                // Create new user in Firestore
                val newUser = User(
                    id = userId,
                    email = email,
                    name = googleAccount.displayName ?: "",
                )
                
                firestore.collection("users").document(userId).set(newUser).await()
                newUser
            }
            
            // Save user session
            saveUserSession(user)
            
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun signup(username: String, email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("User creation failed")
            
            val user = User(
                id = userId,
                email = email,
                name = username,
            )
            
            // Save user to Firestore
            firestore.collection("users").document(userId).set(user).await()
            
            // Save user session
            saveUserSession(user)
            
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null && 
               sharedPreferences.getString("user_id", null) != null
    }

    override fun getCurrentUser(): User? {
        val userId = sharedPreferences.getString("user_id", null) ?: return null
        val email = sharedPreferences.getString("user_email", "") ?: ""
        val name = sharedPreferences.getString("user_name", "") ?: ""
        
        return User(
            id = userId,
            email = email,
            name = name,
        )
    }

    override fun getCurrentUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
        clearUserSession()
    }
    
    private fun saveUserSession(user: User) {
        sharedPreferences.edit().apply {
            putString("user_id", user.id)
            putString("user_email", user.email)
            putString("user_name", user.name)
            apply()
        }
    }
    
    private fun clearUserSession() {
        sharedPreferences.edit().apply {
            remove("user_id")
            remove("user_email")
            remove("user_name")
            apply()
        }
    }
}