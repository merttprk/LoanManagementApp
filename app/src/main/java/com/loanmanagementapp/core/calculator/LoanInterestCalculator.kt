package com.loanmanagementapp.core.calculator

import com.loanmanagementapp.core.strategy.interest.AutoLoanInterestStrategy
import com.loanmanagementapp.core.strategy.interest.BusinessLoanInterestStrategy
import com.loanmanagementapp.core.strategy.interest.strategyinterface.InterestStrategy
import com.loanmanagementapp.core.strategy.interest.MortgageInterestStrategy
import com.loanmanagementapp.core.strategy.interest.PersonalLoanStrategy
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.core.type.LoanType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoanInterestCalculator @Inject constructor() {
    
    private val strategies = mutableMapOf<LoanType, InterestStrategy>()
    
    init {
        registerStrategy(LoanType.PERSONAL, PersonalLoanStrategy())
        registerStrategy(LoanType.AUTO, AutoLoanInterestStrategy())
        registerStrategy(LoanType.MORTGAGE, MortgageInterestStrategy())
        registerStrategy(LoanType.BUSINESS, BusinessLoanInterestStrategy())
    }
    
    /**
     * Bir kredi türü için yeni bir strateji kaydet
     * @param loanType Kredi türü
     * @param strategy Bu kredi türü için kullanılacak strateji
     */
    private fun registerStrategy(loanType: LoanType, strategy: InterestStrategy) {
        strategies[loanType] = strategy
    }
    
    /**
     * Bir kredinin türüne göre faizini hesapla
     * @param loan Faizi hesaplanacak kredi
     * @param months Faiz hesaplaması için ay sayısı
     * @param loanType Kredi türü
     * @return Hesaplanan faiz tutarı
     */
    fun calculateInterest(loan: Loan, months: Int, loanType: LoanType): Double {
        val strategy = getStrategy(loanType)
        return strategy.calculateInterest(loan, months)
    }
    
    /**
     * Belirli bir kredi türü için önerilen vade süresini ay cinsinden al
     * @param loanType Kredi türü
     * @return Ay cinsinden önerilen vade süresi
     */
    fun getRecommendedTerm(loanType: LoanType): Int {
        val strategy = getStrategy(loanType)
        return strategy.getRecommendedTerm()
    }
    
    /**
     * Belirli bir kredi türü için temel faiz oranını al
     * @param loanType Kredi türü
     * @return Yüzde olarak temel faiz oranı
     */
    fun getBaseRate(loanType: LoanType): Double {
        val strategy = getStrategy(loanType)
        return strategy.getBaseRate()
    }
    
    /**
     * Kredi türüne göre uygun faiz stratejisini al
     * @param loanType Kredi türü
     * @return Kullanılacak faiz stratejisi
     * @throws IllegalArgumentException kredi türü için kayıtlı strateji yoksa
     */
    private fun getStrategy(loanType: LoanType): InterestStrategy {
        return strategies[loanType] ?: throw IllegalArgumentException("Kredi türü için kayıtlı strateji bulunamadı: $loanType")
    }
}
