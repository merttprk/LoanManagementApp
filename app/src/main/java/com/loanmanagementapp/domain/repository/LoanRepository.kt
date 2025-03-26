package com.loanmanagementapp.domain.repository
import android.content.Context
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.data.remote.model.Payment
import com.loanmanagementapp.core.type.LoanType

/**
 * Kredi işlemlerini yöneten repository arayüzü
 * Strateji Deseni kullanarak farklı kredi türleri için faiz hesaplamalarını yapar
 * Durum Deseni kullanarak kredi durumlarını yönetir
 */
interface LoanRepository {
    /**
     * Tüm kredileri getirir
     * @param context Uygulama context'i
     * @return Kredi listesi
     */
    suspend fun updateLoans(context: Context): List<Loan>
    
    /**
     * Belirli bir kredi için faiz hesapla
     * @param loan Faizi hesaplanacak kredi
     * @param months Faiz hesaplaması için ay sayısı
     * @return Hesaplanan faiz tutarı
     */
    fun calculateInterest(loan: Loan, months: Int): Double
    
    /**
     * Belirli bir kredi türü için önerilen vade süresini al
     * @param loanType Kredi türü
     * @return Ay cinsinden önerilen vade süresi
     */
    fun getRecommendedTerm(loanType: LoanType): Int
    
    /**
     * Kredi listesini kaydeder
     * @param context Uygulama context'i
     * @param loans Kaydedilecek kredi listesi
     */
    suspend fun saveLoans(context: Context, loans: List<Loan>)

    /**
     * Aktif kredileri getirir
     * @param context Uygulama context'i
     * @return Aktif kredi listesi
     */
    suspend fun getActiveLoans(context: Context): List<Loan>

    /**
     * Pasif kredileri getirir
     * @param context Uygulama context'i
     * @return Pasif kredi listesi
     */
    suspend fun getInactiveLoans(context: Context): List<Loan>
    
    /**
     * Belirli bir kredinin ödeme planını Firestore'a kaydeder
     * @param loanId Kredi ID'si
     * @param payments Ödeme planı listesi
     */
    suspend fun savePayments(loanId: String, payments: List<Payment>)
    
    /**
     * Belirli bir kredinin ödeme planını getirir
     * @param loanId Kredi ID'si
     * @return Ödeme planı listesi
     */
    suspend fun getPayments(loanId: String): List<Payment>
    
    /**
     * Belirli bir ödemenin durumunu günceller
     * @param payment Güncellenecek ödeme
     */
    suspend fun updatePaymentStatus(payment: Payment)
}