package com.loanmanagementapp.domain.repository

import android.content.Context
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.data.remote.model.Payment
import kotlin.collections.List

/**
 * Ödeme işlemlerini yöneten repository arayüzü
 */
interface PaymentRepository {
    /**
     * Belirli bir krediye ait ödeme geçmişini getirir
     * @param context Uygulama context'i
     * @param loanId Kredi ID'si
     * @return Belirli bir krediye ait ödeme geçmişi listesi
     */
    suspend fun getPaymentsForLoan(context: Context, loanId: String): List<Payment>
    
    /**
     * Yeni bir ödeme kaydeder
     * @param context Uygulama context'i
     * @param payment Kaydedilecek ödeme
     */
    //suspend fun savePayment(context: Context, payment: Payment)
}