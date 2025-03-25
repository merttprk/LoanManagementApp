package com.loanmanagementapp.domain.repository
import android.content.Context
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.core.type.LoanType

/**
 * Kredi işlemlerini yöneten repository arayüzü
 * Strateji Deseni kullanarak farklı kredi türleri için faiz hesaplamalarını yapar
 * Durum Deseni kullanarak kredi durumlarını yönetir
 */
interface LoanRepository {
    /**
     * Kredileri günceller ve faiz hesaplamalarını yapar
     * @param context Uygulama context'i
     * @return Güncellenmiş kredi listesi
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
}