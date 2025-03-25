package com.loanmanagementapp.domain.usecase.payment

import android.content.Context
import com.loanmanagementapp.data.remote.model.Payment
import com.loanmanagementapp.domain.repository.PaymentRepository
import javax.inject.Inject

/**
 * Belirli bir krediye ait ödeme geçmişini getiren use case
 */
class GetLoanPaymentsUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    /**
     * Belirli bir krediye ait ödeme geçmişini getirir
     * @param context Uygulama context'i
     * @param loanId Kredi ID'si
     * @return Belirli bir krediye ait ödeme geçmişi listesi
     */
    suspend operator fun invoke(context: Context, loanId: String): List<Payment> {
        return paymentRepository.getPaymentsForLoan(context, loanId)
    }
}
