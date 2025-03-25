package com.loanmanagementapp.core.strategy.interest

import com.loanmanagementapp.core.strategy.interest.strategyinterface.InterestStrategy
import com.loanmanagementapp.data.remote.model.Loan

/**
 * Bireysel Kredi faiz hesaplamaları için strateji uygulaması
 */
class PersonalLoanStrategy : InterestStrategy {
    
    companion object {
        private const val BASE_RATE = 12.5 // Bireysel krediler için %12.5 temel faiz oranı
        private const val RECOMMENDED_TERM_MONTHS = 36 // 3 yıl
    }
    
    /**
     * Bireysel krediler için faiz hesapla
     * Bireysel krediler genellikle basit faiz hesaplaması kullanır
     */
    override fun calculateInterest(loan: Loan, months: Int): Double {
        // Basit faiz formülü: P * R * T
        // P = Anapara, R = Oran (ondalık olarak), T = Süre (yıl olarak)
        val principal = loan.principalAmount
        val rate = loan.interestRate / 100 // Yüzdeyi ondalık sayıya çevir
        val timeInYears = months / 12.0
        
        return principal * rate * timeInYears
    }
    
    override fun getRecommendedTerm(): Int {
        return RECOMMENDED_TERM_MONTHS
    }
    
    override fun getBaseRate(): Double {
        return BASE_RATE
    }
}