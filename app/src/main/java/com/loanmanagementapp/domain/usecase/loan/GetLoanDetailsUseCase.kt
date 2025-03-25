package com.loanmanagementapp.domain.usecase.loan

import android.content.Context
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.domain.repository.LoanRepository
import javax.inject.Inject

/**
 * Kredi detaylarını getiren use case
 * Repository'den kredi bilgilerini alır ve gerekli işlemleri yapar
 */
class GetLoanDetailsUseCase @Inject constructor(
    private val loanRepository: LoanRepository
) {
    /**
     * Tüm kredileri getirir
     * @param context Uygulama context'i
     * @return Kredi listesi
     */
    suspend operator fun invoke(context: Context): List<Loan> {
        return loanRepository.updateLoans(context)
    }
    
    /**
     * Belirli bir kredi için detaylı faiz bilgisini hesaplar
     * @param loan Faiz hesaplanacak kredi
     * @param months Faiz hesaplaması için ay sayısı
     * @return Hesaplanan faiz tutarı
     */
    fun calculateInterestForLoan(loan: Loan, months: Int): Double {
        return loanRepository.calculateInterest(loan, months)
    }
}
