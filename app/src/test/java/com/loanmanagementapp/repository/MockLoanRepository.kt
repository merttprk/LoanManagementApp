package com.loanmanagementapp.repository

import android.content.Context
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.domain.repository.LoanRepository

class MockLoanRepository : LoanRepository {
    private val loans = mutableListOf<Loan>()

    override suspend fun updateLoans(context: Context): List<Loan> {
        return loans
    }

    override fun calculateInterest(loan: Loan, months: Int): Double {
        // Mock calculation - returns 10% of principal amount for testing
        return loan.principalAmount * 0.10
    }

    override fun getRecommendedTerm(loanType: LoanType): Int {
        return when (loanType) {
            LoanType.PERSONAL -> 36
            LoanType.AUTO -> 60
            LoanType.MORTGAGE -> 360
            LoanType.BUSINESS -> 84
            LoanType.EDUCATION -> 48
        }
    }

    override suspend fun saveLoans(context: Context, loans: List<Loan>) {
        this.loans.clear()
        this.loans.addAll(loans)
    }

    override suspend fun getActiveLoans(context: Context): List<Loan> {
        return loans.filter { it.status == "Active" }
    }

    override suspend fun getPassiveLoans(context: Context): List<Loan> {
        return loans.filter { it.status != "Active" }
    }

    // Helper methods for testing
    fun addLoan(loan: Loan) {
        loans.add(loan)
    }

    fun updateLoan(loan: Loan) {
        val index = loans.indexOfFirst { it.id == loan.id }
        if (index != -1) {
            loans[index] = loan
        }
    }

    fun deleteLoan(loanId: String) {
        loans.removeIf { it.id == loanId }
    }

    fun getLoanById(loanId: String): Loan? {
        return loans.find { it.id == loanId }
    }
}
