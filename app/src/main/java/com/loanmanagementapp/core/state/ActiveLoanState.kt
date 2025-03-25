package com.loanmanagementapp.core.state

import com.loanmanagementapp.core.calculator.LoanInterestCalculator
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.remote.model.Loan
import kotlin.random.Random

/**
 * Aktif kredi durumu için state sınıfı
 * Vadesi devam eden ve ödemeleri düzenli olan krediler için
 */
class ActiveLoanState : LoanState {
    
    override fun getStateName(): String = "active"
    
    override fun updateLoan(loan: Loan, loanInterestCalculator: LoanInterestCalculator, loanType: LoanType): Loan {
        // Aktif kredilerde faiz oranı güncellenir
        if (loan.dueIn > 0) {
            val baseRate = loanInterestCalculator.getBaseRate(loanType)
            loan.interestRate = calculateDynamicInterestRate(loan.interestRate, baseRate)
        }
        
        // Vade gününü azalt
        loan.dueIn -= 1
        
        return loan
    }
    
    override fun handleDueDate(loan: Loan): LoanState {
        // Vade süresi bittiğinde
        if (loan.dueIn < 0) {
            // Kredi miktarı hala varsa gecikmiş duruma geç
            return if (loan.principalAmount > 0) {
                OverdueLoanState()
            } else {
                // Kredi miktarı yoksa ödenmiş duruma geç
                PaidLoanState()
            }
        }
        
        // Vade süresi bitmemişse aynı durumda kal
        return this
    }
    
    /**
     * Dinamik faiz oranı hesapla
     */
    private fun calculateDynamicInterestRate(currentRate: Double, baseRate: Double): Double {
        // Mevcut oran ile temel oranın ağırlıklı ortalamasını al
        val weightedAverage = (currentRate * 0.7) + (baseRate * 0.3)
        
        // Günlük küçük bir artış ekle (0.01% - 0.05% arası)
        val dailyIncrease = 0.01 + (Random.nextDouble() * 0.04)
        
        return weightedAverage + dailyIncrease
    }
}
