package com.loanmanagementapp.data.repository

import android.content.Context
import com.loanmanagementapp.core.calculator.LoanInterestCalculator
import com.loanmanagementapp.core.state.LoanState
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.LoanService
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.domain.repository.LoanRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

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
     * @param context Uygulama context'i
     * @return Güncellenmiş kredi listesi
     */
    override suspend fun updateLoans(context: Context): List<Loan> {
        return withContext(Dispatchers.IO) {
            val loans = loanService.loadLoans(context).toMutableList()

            for (i in loans.indices) {
                val loan = loans[i]
                
                // Kredi durumunu belirle ve güncelleme işlemini yap
                val loanType = getLoanTypeFromLoan(loan)
                var loanState = LoanState.fromStatus(loan.status)
                
                // Krediyi güncelle
                val updatedLoan = loanState.updateLoan(loan, loanInterestCalculator, loanType)
                
                // Vade durumuna göre yeni durumu belirle
                loanState = loanState.handleDueDate(updatedLoan)
                
                // Yeni durumu krediye ata
                updatedLoan.status = loanState.getStateName()
                
                loans[i] = updatedLoan
            }

            loanService.saveLoans(loans)
            loans
        }
    }
    
    /**
     * Kredi türünü belirle
     * @param loan Kredi nesnesi
     * @return Kredi türü
     */
    private fun getLoanTypeFromLoan(loan: Loan): LoanType {
        // Kredi adına göre türünü belirle
        return when {
            loan.name.contains("personal", ignoreCase = true) -> LoanType.PERSONAL
            loan.name.contains("auto", ignoreCase = true) || 
            loan.name.contains("car", ignoreCase = true) -> LoanType.AUTO
            loan.name.contains("mortgage", ignoreCase = true) || 
            loan.name.contains("home", ignoreCase = true) || 
            loan.name.contains("house", ignoreCase = true) -> LoanType.MORTGAGE
            loan.name.contains("business", ignoreCase = true) || 
            loan.name.contains("commercial", ignoreCase = true) -> LoanType.BUSINESS
            else -> LoanType.PERSONAL // Varsayılan olarak kişisel kredi
        }
    }
    
    /**
     * Belirli bir kredi için faiz hesapla
     * @param loan Faizi hesaplanacak kredi
     * @param months Faiz hesaplaması için ay sayısı
     * @return Hesaplanan faiz tutarı
     */
    override fun calculateInterest(loan: Loan, months: Int): Double {
        val loanType = getLoanTypeFromLoan(loan)
        return loanInterestCalculator.calculateInterest(loan, months, loanType)
    }
    
    /**
     * Belirli bir kredi türü için önerilen vade süresini al
     * @param loanType Kredi türü
     * @return Ay cinsinden önerilen vade süresi
     */
    override fun getRecommendedTerm(loanType: LoanType): Int {
        return loanInterestCalculator.getRecommendedTerm(loanType)
    }
}