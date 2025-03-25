package com.loanmanagementapp.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.loanmanagementapp.core.calculator.LoanInterestCalculator
import com.loanmanagementapp.data.repository.FirestoreLoanRepositoryImpl
import com.loanmanagementapp.data.repository.FirestorePaymentRepositoryImpl
import com.loanmanagementapp.domain.repository.LoanRepository
import com.loanmanagementapp.domain.repository.PaymentRepository
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
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideLoanInterestCalculator(): LoanInterestCalculator = LoanInterestCalculator()

    @Provides
    @Singleton
    fun provideLoanRepository(
        firestore: FirebaseFirestore,
        loanInterestCalculator: LoanInterestCalculator
    ): LoanRepository = FirestoreLoanRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun providePaymentRepository(
        firestore: FirebaseFirestore
    ): PaymentRepository = FirestorePaymentRepositoryImpl(firestore)
}
