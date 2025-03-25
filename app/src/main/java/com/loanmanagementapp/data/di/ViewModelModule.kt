package com.loanmanagementapp.data.di

import com.loanmanagementapp.domain.repository.AuthRepository
import com.loanmanagementapp.domain.repository.LoanRepository
import com.loanmanagementapp.domain.repository.PaymentRepository
import com.loanmanagementapp.domain.usecase.auth.*
import com.loanmanagementapp.domain.usecase.loan.GetLoanDetailsUseCase
import com.loanmanagementapp.domain.usecase.payment.GetPaymentHistoryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Module for providing view model scoped use cases.
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

    @Provides
    @ViewModelScoped
    fun provideGetLoanDetailsUseCase(loanRepository: LoanRepository): GetLoanDetailsUseCase {
        return GetLoanDetailsUseCase(loanRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetPaymentHistoryUseCase(
        paymentRepository: PaymentRepository,
        loanRepository: LoanRepository
    ): GetPaymentHistoryUseCase {
        return GetPaymentHistoryUseCase(paymentRepository, loanRepository)
    }
}
