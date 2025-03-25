package com.loanmanagementapp.data.repository

import android.content.Context
import com.loanmanagementapp.core.calculator.LoanInterestCalculator
import com.loanmanagementapp.core.state.statusinterface.LoanState
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.LoanService
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.domain.repository.LoanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * LoanRepository arayüzünün uygulaması
 * Kredi işlemlerini ve faiz hesaplamalarını yönetir
 */
class LoanRepositoryImpl @Inject constructor(
    private val loanService: LoanService,
    private val loanInterestCalculator: LoanInterestCalculator
) : LoanRepository {

    /**
     * Kredileri günceller ve faiz hesaplamalarını yapar
     */
    override suspend fun updateLoans(context: Context): List<Loan> =
        withContext(Dispatchers.IO) {
            val loans = loanService.loadLoans(context).toMutableList()
            loans.mapIndexed { _, loan ->
                updateLoanState(loan)
            }.also { updatedLoans ->
                loanService.saveLoans(updatedLoans)
            }
        }

    /**
     * Kredi durumunu günceller
     */
    private fun updateLoanState(loan: Loan): Loan {
        val loanType = getLoanTypeFromLoan(loan)
        var loanState = LoanState.fromStatus(loan.status)

        return loanState.updateLoan(loan, loanInterestCalculator, loanType).also { updatedLoan ->
            loanState = loanState.handleDueDate(updatedLoan)
            updatedLoan.status = loanState.getStateName()
        }
    }

    /**
     * Kredi türünü belirle
     */
    private fun getLoanTypeFromLoan(loan: Loan): LoanType = when {
        loan.name.contains("personal", ignoreCase = true) -> LoanType.PERSONAL
        loan.name.contains("auto", ignoreCase = true) || 
        loan.name.contains("car", ignoreCase = true) -> LoanType.AUTO
        loan.name.contains("mortgage", ignoreCase = true) || 
        loan.name.contains("home", ignoreCase = true) || 
        loan.name.contains("house", ignoreCase = true) -> LoanType.MORTGAGE
        loan.name.contains("business", ignoreCase = true) || 
        loan.name.contains("commercial", ignoreCase = true) -> LoanType.BUSINESS
        else -> LoanType.PERSONAL
    }

    /**
     * Belirli bir kredi için faiz hesapla
     */
    override fun calculateInterest(loan: Loan, months: Int): Double {
        val loanType = getLoanTypeFromLoan(loan)
        return loanInterestCalculator.calculateInterest(loan, months, loanType)
    }
    
    /**
     * Belirli bir kredi türü için önerilen vade süresini al
     */
    override fun getRecommendedTerm(loanType: LoanType): Int =
        loanInterestCalculator.getRecommendedTerm(loanType)
    
    /**
     * Kredi listesini kaydeder
     */
    override suspend fun saveLoans(context: Context, loans: List<Loan>) =
        withContext(Dispatchers.IO) {
            loanService.saveLoans(loans)
        }

    /**
     * Aktif kredileri Flow olarak getirir
     */
    override suspend fun getActiveLoans(context: Context): List<Loan> =
        withContext(Dispatchers.IO) {
            updateLoans(context).filter { it.status.equals("active", ignoreCase = true) }
        }

    /**
     * Pasif kredileri Flow olarak getirir
     */
    override suspend fun getPassiveLoans(context: Context): List<Loan> =
        withContext(Dispatchers.IO) {
            updateLoans(context).filter { !it.status.equals("active", ignoreCase = true) }
        }
}