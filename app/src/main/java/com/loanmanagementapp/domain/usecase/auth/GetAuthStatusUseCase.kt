package com.loanmanagementapp.domain.usecase.auth

import com.loanmanagementapp.data.remote.model.User
import com.loanmanagementapp.domain.repository.AuthRepository
import javax.inject.Inject

class GetAuthStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Boolean {
        return authRepository.isUserLoggedIn()
    }
    
    fun getCurrentUser(): User? {
        return authRepository.getCurrentUser()
    }
}
