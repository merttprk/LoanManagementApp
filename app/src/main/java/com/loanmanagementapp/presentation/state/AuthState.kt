package com.loanmanagementapp.presentation.state

sealed class AuthState {
    data object Initial : AuthState()
    data object Loading : AuthState()
    data object Success : AuthState()
    data object PasswordResetSent : AuthState()
    data class Error(val message: String) : AuthState()
}
