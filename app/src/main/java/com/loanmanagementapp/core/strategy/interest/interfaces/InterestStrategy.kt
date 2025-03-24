package com.loanmanagementapp.core.strategy.interest.interfaces

import com.loanmanagementapp.data.remote.model.Loan

/**
 * Kredi faizi hesaplama için strateji arayüzü
 */
interface InterestStrategy {
    /**
     * Bir kredi için faiz tutarını hesapla
     * @param loan Faizi hesaplanacak kredi
     * @param months Faiz hesaplaması için ay sayısı
     * @return Hesaplanan faiz tutarı
     */
    fun calculateInterest(loan: Loan, months: Int): Double

    /**
     * Bu kredi türü için önerilen vade süresini ay cinsinden al
     * @return Ay cinsinden önerilen vade süresi
     */
    fun getRecommendedTerm(): Int

    /**
     * Bu kredi türü için temel faiz oranını al
     * @return Yüzde olarak temel faiz oranı
     */
    fun getBaseRate(): Double
}