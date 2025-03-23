package com.loanmanagementapp.domain.usecase.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.loanmanagementapp.data.remote.model.User
import com.loanmanagementapp.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank() || password.isBlank()) {
            throw Exception("Email and password cannot be empty")
        }

        when (val result = authRepository.login(email, password)) {
            is Result.Success -> return result
            is Result.Error -> throw result.exception
            is Result.Loading -> throw Exception("Unexpected loading state")
        }
    }

    suspend fun loginWithGoogle(googleAccount: GoogleSignInAccount): Result<User> {
        when (val result = authRepository.loginWithGoogle(googleAccount)) {
            is Result.Success -> return result
            is Result.Error -> throw result.exception
            is Result.Loading -> throw Exception("Unexpected loading state")
        }
    }

    fun getCurrentUserId(): String? {
        return authRepository.getCurrentUserId()
    }
}