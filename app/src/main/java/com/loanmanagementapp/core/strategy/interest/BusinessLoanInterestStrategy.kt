package com.loanmanagementapp.core.strategy.interest

import com.loanmanagementapp.core.strategy.interest.strategyinterface.InterestStrategy
import com.loanmanagementapp.data.remote.model.Loan

/**
 * İşletme Kredisi faiz hesaplamaları için strateji uygulaması
 */
class BusinessLoanInterestStrategy : InterestStrategy {
    
    companion object {
        private const val BASE_RATE = 9.75 // İşletme kredileri için %9.75 temel faiz oranı
        private const val RECOMMENDED_TERM_MONTHS = 84 // 7 yıl
    }
    
    /**
     * İşletme kredileri için faiz hesapla
     * İşletme kredileri genellikle üç aylık bileşik faiz kullanır
     */
    override fun calculateInterest(loan: Loan, months: Int): Double {
        val principal = loan.principalAmount
        val annualRate = (BASE_RATE + loan.interestRate) / 100 // Yıllık faiz oranı ondalık olarak
        val quarterlyRate = annualRate / 4 // Üç aylık faiz oranı
        val quarters = months / 3.0 // Üç aylık dönem sayısı
        
        // Bileşik faiz formülü: P(1 + r)^n - P
        // Burada P = Anapara, r = dönem başına oran, n = dönem sayısı
        val totalAmount = principal * Math.pow((1 + quarterlyRate), quarters)
        
        // Faiz, toplam tutar ile anapara arasındaki farktır
        return totalAmount - principal
    }
    
    override fun getRecommendedTerm(): Int {
        return RECOMMENDED_TERM_MONTHS
    }
    
    override fun getBaseRate(): Double {
        return BASE_RATE
    }
}