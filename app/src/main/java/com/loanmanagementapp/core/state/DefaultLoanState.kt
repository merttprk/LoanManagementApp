package com.loanmanagementapp.core.state

import com.loanmanagementapp.core.calculator.LoanInterestCalculator
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.remote.model.Loan
import kotlin.random.Random

/**
 * Temerrüt durumundaki kredi için state sınıfı
 * Uzun süre ödenmemiş ve yasal takip sürecine girmiş krediler için
 */
class DefaultLoanState : LoanState {
    
    override fun getStateName(): String = "default"
    
    override fun updateLoan(loan: Loan, loanInterestCalculator: LoanInterestCalculator, loanType: LoanType): Loan {
        // Temerrüt durumundaki kredilerde en yüksek faiz uygulanır
        val baseRate = loanInterestCalculator.getBaseRate(loanType)
        // Temerrüt faizi normal faizden %100 daha yüksek
        loan.interestRate = calculateDefaultInterestRate(loan.interestRate, baseRate)
        
        // Vade gününü azalt (negatif değer gecikme gününü gösterir)
        loan.dueIn -= 1
        
        return loan
    }
    
    override fun handleDueDate(loan: Loan): LoanState {
        // Temerrüt durumundaki krediler için durum değişikliği kontrolleri
        
        // Kredi miktarı çok düşükse (1000'den az) ödenmiş duruma geç
        if (loan.principalAmount < 1000) {
            return PaidLoanState()
        }
        
        // Diğer durumlarda temerrüt durumunda kal
        return this
    }
    
    /**
     * Temerrüt durumundaki krediler için faiz oranı hesapla
     * En yüksek faiz oranı uygulanır
     */
    private fun calculateDefaultInterestRate(currentRate: Double, baseRate: Double): Double {
        // Mevcut oran ile temel oranın ağırlıklı ortalamasını al
        val weightedAverage = (currentRate * 0.5) + (baseRate * 0.5)
        
        // Temerrüt için ek faiz (%100 daha fazla)
        val defaultIncrease = weightedAverage * 1.0
        
        // Günlük küçük bir artış ekle (0.1% - 0.2% arası)
        val dailyIncrease = 0.1 + (Random.nextDouble() * 0.1)
        
        return weightedAverage + defaultIncrease + dailyIncrease
    }
}
