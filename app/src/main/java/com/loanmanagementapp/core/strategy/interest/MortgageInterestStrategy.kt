package com.loanmanagementapp.core.strategy.interest

import com.loanmanagementapp.core.strategy.interest.interfaces.InterestStrategy
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
        val monthlyRate = (BASE_RATE + loan.interestRate) / 100 / 12 // Aylık faiz oranı ondalık olarak
        
        // Standart konut kredisi formülü kullanarak aylık ödemeyi hesapla
        val monthlyPayment = principal * monthlyRate * (1 + monthlyRate)
            .pow(months.toDouble()) /
                            ((1 + monthlyRate).pow(months.toDouble()) - 1)
        
        // Kredinin ömrü boyunca toplam ödeme
        val totalPayment = monthlyPayment * months
        
        // Faiz, toplam ödeme ile anapara arasındaki farktır
        return totalPayment - principal
    }
    
    override fun getRecommendedTerm(): Int {
        return RECOMMENDED_TERM_MONTHS
    }
    
    override fun getBaseRate(): Double {
        return BASE_RATE
    }
}