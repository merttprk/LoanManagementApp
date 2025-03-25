package com.loanmanagementapp.core.strategy.interest

import com.loanmanagementapp.core.strategy.interest.strategyinterface.InterestStrategy
import com.loanmanagementapp.data.remote.model.Loan
import kotlin.math.pow

/**
 * Konut Kredisi faiz hesaplamaları için strateji uygulaması
 */
class MortgageInterestStrategy : InterestStrategy {
    
    companion object {
        private const val BASE_RATE = 5.25 // Konut kredileri için %5.25 temel faiz oranı
        private const val RECOMMENDED_TERM_MONTHS = 360 // 30 yıl
    }
    
    /**
     * Konut kredileri için faiz hesapla
     * Konut kredileri uzun vadeler boyunca aylık bileşik faiz kullanır
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