package com.loanmanagementapp.core.strategy.interest

import com.loanmanagementapp.core.strategy.interest.strategyinterface.InterestStrategy
import com.loanmanagementapp.data.remote.model.Loan

/**
 * Bireysel Krediler için faiz hesaplamaları için strateji uygulaması
 */
class PersonalLoanStrategy : InterestStrategy {
    
    companion object {
        private const val BASE_RATE = 12.5 // Bireysel krediler için %12.5 temel faiz oranı
        private const val RECOMMENDED_TERM_MONTHS = 36 // 3 yıl
    }
    
    /**
     * Bireysel krediler için faiz hesapla
     * Bireysel krediler genellikle basit faiz kullanır
     */
    override fun calculateInterest(loan: Loan, months: Int): Double {
        val principal = loan.principalAmount
        val annualRate = loan.interestRate / 100 // Yıllık faiz oranı ondalık olarak
        
        // Basit faiz formülü: P * r * t
        return principal * annualRate * (months / 12.0)
    }
    
    override fun getRecommendedTerm(): Int {
        return RECOMMENDED_TERM_MONTHS
    }
    
    override fun getBaseRate(): Double {
        return BASE_RATE
    }
}