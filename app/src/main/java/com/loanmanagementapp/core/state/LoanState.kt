package com.loanmanagementapp.core.state

import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.core.calculator.LoanInterestCalculator
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.core.state.ActiveLoanState
import com.loanmanagementapp.core.state.PaidLoanState
import com.loanmanagementapp.core.state.OverdueLoanState
import com.loanmanagementapp.core.state.DefaultLoanState

/**
 * Kredi durumlarını yöneten State Pattern arayüzü
 */
interface LoanState {
    /**
     * Kredi durumunun adını döndürür
     */
    fun getStateName(): String
    
    /**
     * Kredi durumuna göre güncelleme yapar
     * @param loan Güncellenecek kredi
     * @param loanInterestCalculator Faiz hesaplama aracı
     * @param loanType Kredi türü
     * @return Güncellenen kredi
     */
    fun updateLoan(loan: Loan, loanInterestCalculator: LoanInterestCalculator, loanType: LoanType): Loan
    
    /**
     * Vade süresi bittiğinde yeni durumu belirler
     * @param loan Kredi
     * @return Yeni kredi durumu
     */
    fun handleDueDate(loan: Loan): LoanState
    
    companion object {
        /**
         * Kredi durumuna göre uygun state nesnesini döndürür
         * @param status Kredi durumu
         * @return LoanState nesnesi
         */
        fun fromStatus(status: String): LoanState {
            return when (status.lowercase()) {
                "active" -> ActiveLoanState()
                "paid" -> PaidLoanState()
                "overdue" -> OverdueLoanState()
                "default" -> DefaultLoanState()
                else -> ActiveLoanState() // Varsayılan olarak aktif durum
            }
        }
    }
}
