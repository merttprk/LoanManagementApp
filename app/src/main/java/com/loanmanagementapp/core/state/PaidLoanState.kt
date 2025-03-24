package com.loanmanagementapp.core.state

import com.loanmanagementapp.core.calculator.LoanInterestCalculator
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.remote.model.Loan

/**
 * Ödenmiş kredi durumu için state sınıfı
 * Tamamen ödenmiş krediler için
 */
class PaidLoanState : LoanState {
    
    override fun getStateName(): String = "paid"
    
    override fun updateLoan(loan: Loan, loanInterestCalculator: LoanInterestCalculator, loanType: LoanType): Loan {
        // Ödenmiş kredilerde herhangi bir faiz güncellemesi yapılmaz
        // Sadece vade gününü azalt
        loan.dueIn -= 1
        
        return loan
    }
    
    override fun handleDueDate(loan: Loan): LoanState {
        // Ödenmiş krediler her zaman ödenmiş durumda kalır
        return this
    }
}
