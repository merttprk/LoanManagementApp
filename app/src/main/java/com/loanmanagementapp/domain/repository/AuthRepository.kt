package com.loanmanagementapp.domain.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.loanmanagementapp.data.remote.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun loginWithGoogle(googleAccount: GoogleSignInAccount): Result<User>
    suspend fun signup(username: String, email: String, password: String): Result<User>
    suspend fun resetPassword(email: String): Result<Unit>
    fun isUserLoggedIn(): Boolean
    fun getCurrentUser(): User?
    fun getCurrentUserId(): String?
    suspend fun logout()
}
