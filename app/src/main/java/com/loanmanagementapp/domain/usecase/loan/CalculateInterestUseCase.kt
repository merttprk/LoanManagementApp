package com.loanmanagementapp.domain.usecase.loan

import com.loanmanagementapp.core.calculator.LoanInterestCalculator
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.remote.model.Loan
import javax.inject.Inject

/**
 * LoanInterestCalculator kullanarak kredi faizi hesaplama kullanım durumu
 */
class CalculateInterestUseCase @Inject constructor(
    private val loanInterestCalculator: LoanInterestCalculator
) {

    /**
     * Bir kredinin türüne göre faizini hesapla
     * @param loan Faizi hesaplanacak kredi
     * @param months Faiz hesaplaması için ay sayısı
     * @param loanType Kredi türü (PERSONAL, AUTO, MORTGAGE, BUSINESS)
     * @return Hesaplanan faiz tutarı
     */
    fun execute(loan: Loan, months: Int, loanType: LoanType): Double {
        return loanInterestCalculator.calculateInterest(loan, months, loanType)
    }
    
    /**
     * Belirli bir kredi türü için önerilen vade süresini ay cinsinden al
     * @param loanType Kredi türü
     * @return Ay cinsinden önerilen vade süresi
     */
    fun getRecommendedTerm(loanType: LoanType): Int {
        return loanInterestCalculator.getRecommendedTerm(loanType)
    }
    
    /**
     * Belirli bir kredi türü için temel faiz oranını al
     * @param loanType Kredi türü
     * @return Yüzde olarak temel faiz oranı
     */
    fun getBaseRate(loanType: LoanType): Double {
        return loanInterestCalculator.getBaseRate(loanType)
    }
}