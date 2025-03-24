package com.loanmanagementapp.domain.repository

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.loanmanagementapp.data.remote.model.User
import com.loanmanagementapp.presentation.state.Result

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun loginWithGoogle(googleAccount: GoogleSignInAccount): Result<User>
    fun getGoogleSignInIntent(): Intent
    fun getLastSignedInAccount(): GoogleSignInAccount?
    suspend fun signup(username: String, email: String, password: String): Result<User>
    suspend fun resetPassword(email: String): Result<Unit>
    fun isUserLoggedIn(): Boolean
    fun getCurrentUser(): User?
    fun getCurrentUserId(): String?
    suspend fun logout()
}
