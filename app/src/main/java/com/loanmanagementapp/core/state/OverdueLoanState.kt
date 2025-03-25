package com.loanmanagementapp.core.state

import com.loanmanagementapp.core.calculator.LoanInterestCalculator
import com.loanmanagementapp.core.state.statusinterface.LoanState
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.remote.model.Loan

/**
 * Gecikmiş kredi durumu için state sınıfı
 * Vadesi geçmiş ancak henüz yasal takibe düşmemiş krediler için
 */
class OverdueLoanState : LoanState {
    
    override fun getStateName(): String = "Overdue"
    
    override fun updateLoan(loan: Loan, loanInterestCalculator: LoanInterestCalculator, loanType: LoanType): Loan {
        // Gecikmiş kredilerde ceza faizi uygulanır
        val baseRate = loanInterestCalculator.getBaseRate(loanType)
        loan.interestRate = baseRate * PENALTY_MULTIPLIER
        
        // Vade gününü azalt
        loan.dueIn -= 1
        
        // Kredi durumunu güncelle
        loan.status = getStateName()
        
        return loan
    }
    
    override fun handleDueDate(loan: Loan): LoanState {
        // 90 günden fazla gecikme varsa yasal takibe geç
        if (loan.dueIn < -90) {
            return DefaultLoanState()
        }
        
        return this
    }
    
    companion object {
        private const val PENALTY_MULTIPLIER = 1.5
    }
}
