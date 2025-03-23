package com.loanmanagementapp.data.di

import com.loanmanagementapp.domain.repository.AuthRepository
import com.loanmanagementapp.domain.usecase.auth.GetAuthStatusUseCase
import com.loanmanagementapp.domain.usecase.auth.LoginUseCase
import com.loanmanagementapp.domain.usecase.auth.LogoutUseCase
import com.loanmanagementapp.domain.usecase.auth.SignupUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Module for providing view models.
 */
@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideSignupUseCase(authRepository: AuthRepository): SignupUseCase {
        return SignupUseCase(authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideLogoutUseCase(authRepository: AuthRepository): LogoutUseCase {
        return LogoutUseCase(authRepository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideGetAuthStatusUseCase(authRepository: AuthRepository): GetAuthStatusUseCase {
        return GetAuthStatusUseCase(authRepository)
    }

}
