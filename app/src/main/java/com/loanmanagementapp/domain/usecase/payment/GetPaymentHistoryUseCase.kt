package com.loanmanagementapp.domain.usecase.payment

import android.content.Context
import com.loanmanagementapp.data.remote.model.Payment
import com.loanmanagementapp.domain.repository.LoanRepository
import com.loanmanagementapp.domain.repository.PaymentRepository
import javax.inject.Inject

/**
 * Ödeme geçmişini getiren use case
 */
class GetPaymentHistoryUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val loanRepository: LoanRepository
) {
    /**
     * Tüm ödeme geçmişini getirir
     * @param context Uygulama context'i
     * @return Ödeme geçmişi listesi
     */
    suspend operator fun invoke(context: Context): List<Payment> {
        val loans = loanRepository.updateLoans(context)
        val allPayments = mutableListOf<Payment>()
        
        for (loan in loans) {
            val payments = paymentRepository.getPaymentsForLoan(context, loan.id)
            allPayments.addAll(payments)
        }
        
        return allPayments.sortedByDescending { it.paymentDate }
    }
}
