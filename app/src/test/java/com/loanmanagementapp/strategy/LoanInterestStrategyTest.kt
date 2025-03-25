package com.loanmanagementapp.strategy

import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.core.strategy.interest.*
import com.loanmanagementapp.core.strategy.interest.strategyinterface.InterestStrategy
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LoanInterestStrategyTest {

    private lateinit var personalLoanStrategy: InterestStrategy
    private lateinit var autoLoanStrategy: InterestStrategy
    private lateinit var mortgageStrategy: InterestStrategy
    private lateinit var businessLoanStrategy: InterestStrategy

    @Before
    fun setup() {
        personalLoanStrategy = PersonalLoanStrategy()
        autoLoanStrategy = AutoLoanInterestStrategy()
        mortgageStrategy = MortgageInterestStrategy()
        businessLoanStrategy = BusinessLoanInterestStrategy()
    }

    @Test
    fun `test personal loan interest calculation`() {
        val loan = Loan(
            id = "1",
            name = "Personal Loan",
            principalAmount = 10000.0,
            interestRate = 12.5,
            status = "Active",
            dueIn = 30,
            type = LoanType.PERSONAL
        )
        val months = 12
        
        val expectedInterest = 1250.0 // 12.5% annual rate for 1 year
        val calculatedInterest = personalLoanStrategy.calculateInterest(loan, months)

        assertEquals(expectedInterest, calculatedInterest, 0.01)
        assertEquals(36, personalLoanStrategy.getRecommendedTerm())
        assertEquals(12.5, personalLoanStrategy.getBaseRate(), 0.01)
    }

    @Test
    fun `test auto loan interest calculation`() {
        val loan = Loan(
            id = "2",
            name = "Auto Loan",
            principalAmount = 20000.0,
            interestRate = 7.5,
            status = "Active",
            dueIn = 30,
            type = LoanType.AUTO
        )
        val months = 12
        
        // Monthly compounding at 7.5% APR
        val expectedInterest = 1545.41
        val calculatedInterest = autoLoanStrategy.calculateInterest(loan, months)

        assertEquals(expectedInterest, calculatedInterest, 0.01)
        assertEquals(60, autoLoanStrategy.getRecommendedTerm())
        assertEquals(7.5, autoLoanStrategy.getBaseRate(), 0.01)
    }

    @Test
    fun `test mortgage interest calculation`() {
        val loan = Loan(
            id = "3",
            name = "Mortgage",
            principalAmount = 200000.0,
            interestRate = 5.25,
            status = "Active",
            dueIn = 30,
            type = LoanType.MORTGAGE
        )
        val months = 12
        
        // Monthly compounding at 5.25% APR
        val expectedInterest = 10500.0
        val calculatedInterest = mortgageStrategy.calculateInterest(loan, months)

        assertEquals(expectedInterest, calculatedInterest, 0.01)
        assertEquals(360, mortgageStrategy.getRecommendedTerm())
        assertEquals(5.25, mortgageStrategy.getBaseRate(), 0.01)
    }

    @Test
    fun `test business loan interest calculation`() {
        val loan = Loan(
            id = "4",
            name = "Business Loan",
            principalAmount = 50000.0,
            interestRate = 9.75,
            status = "Active",
            dueIn = 30,
            type = LoanType.BUSINESS
        )
        val months = 12
        
        // Quarterly compounding at 9.75% APR
        val expectedInterest = 5018.59
        val calculatedInterest = businessLoanStrategy.calculateInterest(loan, months)

        assertEquals(expectedInterest, calculatedInterest, 0.01)
        assertEquals(84, businessLoanStrategy.getRecommendedTerm())
        assertEquals(9.75, businessLoanStrategy.getBaseRate(), 0.01)
    }
}
