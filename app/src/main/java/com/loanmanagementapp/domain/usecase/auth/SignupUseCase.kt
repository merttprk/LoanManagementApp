package com.loanmanagementapp.domain.usecase.auth

import com.loanmanagementapp.data.remote.model.User
import com.loanmanagementapp.domain.repository.AuthRepository
import com.loanmanagementapp.presentation.state.Result
import javax.inject.Inject

class SignupUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, email: String, password: String): Result<User> {
        if (username.isBlank()) {
            throw Exception("Username cannot be empty")
        }
        
        if (email.isBlank()) {
            throw Exception("Email cannot be empty")
        }
        
        if (password.isBlank()) {
            throw Exception("Password cannot be empty")
        }
        
        if (password.length < 6) {
            throw Exception("Password must be at least 6 characters")
        }
        
        return authRepository.signup(username, email, password)
    }
}
