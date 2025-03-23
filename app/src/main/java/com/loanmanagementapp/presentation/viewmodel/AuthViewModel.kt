package com.loanmanagementapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.loanmanagementapp.data.remote.model.User
import com.loanmanagementapp.domain.usecase.auth.GetAuthStatusUseCase
import com.loanmanagementapp.domain.usecase.auth.LoginUseCase
import com.loanmanagementapp.domain.usecase.auth.LogoutUseCase
import com.loanmanagementapp.domain.usecase.auth.SignupUseCase
import com.loanmanagementapp.presentation.state.AuthState
import com.loanmanagementapp.presentation.state.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signupUseCase: SignupUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getAuthStatusUseCase: GetAuthStatusUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        val isLoggedIn = getAuthStatusUseCase()
        if (isLoggedIn) {
            _currentUser.value = getAuthStatusUseCase.getCurrentUser()
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                when (val result = loginUseCase(email, password)) {
                    is Result.Success -> {
                        _currentUser.value = result.data
                        _authState.value = AuthState.Authenticated
                    }
                    is Result.Error -> {
                        _authState.value = AuthState.Error(result.exception.message ?: "Authentication failed")
                    }
                    else -> {
                        // Handle other cases if needed
                    }
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Authentication failed")
            }
        }
    }

    fun loginWithGoogle(googleAccount: GoogleSignInAccount) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = loginUseCase.loginWithGoogle(googleAccount)
                when (result) {
                    is Result.Success -> {
                        _currentUser.value = result.data
                        _authState.value = AuthState.Authenticated
                    }
                    is Result.Error -> {
                        _authState.value = AuthState.Error(result.exception.message ?: "Google authentication failed")
                    }
                    else -> {
                        // Handle other cases if needed
                    }
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google authentication failed")
            }
        }
    }

    fun signup(username: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                when (val result = signupUseCase(username, email, password)) {
                    is Result.Success -> {
                        _currentUser.value = result.data
                        _authState.value = AuthState.Authenticated
                    }
                    is Result.Error -> {
                        _authState.value = AuthState.Error(result.exception.message ?: "Signup failed")
                    }
                    else -> {
                        // Handle other cases if needed
                    }
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Signup failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                logoutUseCase()
                _currentUser.value = null
                _authState.value = AuthState.Unauthenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Logout failed")
            }
        }
    }
}