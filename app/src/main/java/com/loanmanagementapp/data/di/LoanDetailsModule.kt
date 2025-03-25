package com.loanmanagementapp.di

import com.loanmanagementapp.domain.repository.LoanRepository
import com.loanmanagementapp.domain.usecase.loan.GetLoanDetailsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Kredi detayları için bağımlılık enjeksiyonu modülü
 */
@Module
@InstallIn(SingletonComponent::class)
object LoanDetailsModule {

    /**
     * GetLoanDetailsUseCase için provider
     * @param loanRepository Kredi repository'si
     * @return GetLoanDetailsUseCase instance'ı
     */
    @Provides
    @Singleton
    fun provideGetLoanDetailsUseCase(loanRepository: LoanRepository): GetLoanDetailsUseCase {
        return GetLoanDetailsUseCase(loanRepository)
    }
}
