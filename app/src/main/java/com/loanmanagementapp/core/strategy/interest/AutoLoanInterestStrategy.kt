package com.loanmanagementapp.core.strategy.interest

import com.loanmanagementapp.core.strategy.interest.interfaces.InterestStrategy
import com.loanmanagementapp.data.remote.model.Loan

/**
 * Taşıt Kredisi faiz hesaplamaları için strateji uygulaması
 */
class AutoLoanInterestStrategy : InterestStrategy {
    
    companion object {
        private const val BASE_RATE = 7.5 // Taşıt kredileri için %7.5 temel faiz oranı
        private const val RECOMMENDED_TERM_MONTHS = 60 // 5 yıl
    }
    
    /**
     * Taşıt kredileri için faiz hesapla
     * Taşıt kredileri genellikle aylık bileşik faiz kullanır
     */
    override fun calculateInterest(loan: Loan, months: Int): Double {
        val principal = loan.principalAmount
        val monthlyRate = (BASE_RATE + loan.interestRate) / 100 / 12 // Aylık faiz oranı ondalık olarak
        
        // Amortisman formülü kullanarak toplam ödemeyi hesapla
        val totalPayment = principal * monthlyRate * Math.pow((1 + monthlyRate).toDouble(), months.toDouble()) / 
                          (Math.pow((1 + monthlyRate).toDouble(), months.toDouble()) - 1)
        
        // Faiz, toplam ödeme ile anapara arasındaki farktır
        return totalPayment * months - principal
    }
    
    override fun getRecommendedTerm(): Int {
        return RECOMMENDED_TERM_MONTHS
    }
    
    override fun getBaseRate(): Double {
        return BASE_RATE
    }
}
