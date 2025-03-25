package com.loanmanagementapp.core.state

import com.loanmanagementapp.core.calculator.LoanInterestCalculator
import com.loanmanagementapp.core.state.statusinterface.LoanState
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.remote.model.Loan

/**
 * Ödenmiş kredi durumu için state sınıfı
 * Tamamen ödenmiş ve kapatılmış krediler için
 */
class PaidLoanState : LoanState {
    
    override fun getStateName(): String = "Paid"
    
    override fun updateLoan(loan: Loan, loanInterestCalculator: LoanInterestCalculator, loanType: LoanType): Loan {
        // Ödenmiş kredilerde herhangi bir güncelleme yapılmaz
        loan.status = getStateName()
        return loan
    }
    
    override fun handleDueDate(loan: Loan): LoanState {
        // Ödenmiş krediler her zaman ödenmiş durumda kalır
        return this
    }
}
