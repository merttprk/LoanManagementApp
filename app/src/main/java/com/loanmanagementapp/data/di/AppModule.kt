package com.loanmanagementapp.data.di

import com.loanmanagementapp.core.calculator.LoanInterestCalculator
import com.loanmanagementapp.data.repository.LoanRepositoryImpl
import com.loanmanagementapp.domain.repository.LoanRepository
import com.loanmanagementapp.data.LoanService
import com.loanmanagementapp.data.MockLoanService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideLoanService(): LoanService = MockLoanService()
    
    @Provides
    @Singleton
    fun provideLoanInterestCalculator(): LoanInterestCalculator = LoanInterestCalculator()

    @Provides
    @Singleton
    fun provideLoanRepository(
        loanService: LoanService,
        loanInterestCalculator: LoanInterestCalculator
    ): LoanRepository = LoanRepositoryImpl(loanService, loanInterestCalculator)
}
