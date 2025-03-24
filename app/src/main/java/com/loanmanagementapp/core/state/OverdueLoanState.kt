package com.loanmanagementapp.core.state

import com.loanmanagementapp.core.calculator.LoanInterestCalculator
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.remote.model.Loan
import kotlin.random.Random

/**
 * Gecikmiş kredi durumu için state sınıfı
 * Vadesi geçmiş ancak hala ödenebilir durumdaki krediler için
 */
class OverdueLoanState : LoanState {
    
    override fun getStateName(): String = "overdue"
    
    override fun updateLoan(loan: Loan, loanInterestCalculator: LoanInterestCalculator, loanType: LoanType): Loan {
        // Gecikmiş kredilerde yüksek faiz uygulanır
        val baseRate = loanInterestCalculator.getBaseRate(loanType)
        // Gecikme faizi normal faizden %50 daha yüksek
        loan.interestRate = calculateOverdueInterestRate(loan.interestRate, baseRate)
        
        // Vade gününü azalt (negatif değer gecikme gününü gösterir)
        loan.dueIn -= 1
        
        // Kredi miktarı yüksekse (5000'den fazla) ve gecikme durumundaysa
        // temerrüt durumuna geçir
        if (loan.principalAmount > 5000) {
            loan.status = "default"
        }
        
        return loan
    }
    
    override fun handleDueDate(loan: Loan): LoanState {
        // Gecikmiş krediler için durum değişikliği kontrolleri
        
        // Kredi miktarı yoksa ödenmiş duruma geç
        if (loan.principalAmount <= 0) {
            return PaidLoanState()
        }
        
        // Kredi miktarı yüksekse ve gecikme durumundaysa temerrüt durumuna geç
        if (loan.principalAmount > 5000) {
            return DefaultLoanState()
        }
        
        // Diğer durumlarda gecikme durumunda kal
        return this
    }
    
    /**
     * Gecikmiş krediler için faiz oranı hesapla
     * Normal faize göre daha yüksek bir oran uygulanır
     */
    private fun calculateOverdueInterestRate(currentRate: Double, baseRate: Double): Double {
        // Mevcut oran ile temel oranın ağırlıklı ortalamasını al
        val weightedAverage = (currentRate * 0.6) + (baseRate * 0.4)
        
        // Gecikme için ek faiz (%50 daha fazla)
        val overdueIncrease = weightedAverage * 0.5
        
        // Günlük küçük bir artış ekle (0.05% - 0.1% arası)
        val dailyIncrease = 0.05 + (Random.nextDouble() * 0.05)
        
        return weightedAverage + overdueIncrease + dailyIncrease
    }
}
