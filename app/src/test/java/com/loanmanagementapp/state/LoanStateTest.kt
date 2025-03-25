package com.loanmanagementapp.state

import com.loanmanagementapp.core.calculator.LoanInterestCalculator
import com.loanmanagementapp.core.state.*
import com.loanmanagementapp.core.state.statusinterface.LoanState
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.util.TestUtil
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoanStateTest {

    private lateinit var activeLoanState: LoanState
    private lateinit var paidLoanState: LoanState
    private lateinit var overdueLoanState: LoanState
    private lateinit var defaultLoanState: LoanState
    private lateinit var mockCalculator: LoanInterestCalculator
    private lateinit var testLoan: Loan

    @Before
    fun setup() {
        activeLoanState = ActiveLoanState()
        paidLoanState = PaidLoanState()
        overdueLoanState = OverdueLoanState()
        defaultLoanState = DefaultLoanState()
        mockCalculator = mockk(relaxed = true)
        
        testLoan = TestUtil.createTestLoan()
    }

    @Test
    fun `test active loan state`() {
        assertEquals("Active", activeLoanState.getStateName())
        
        every { mockCalculator.calculateInterest(any(), any(), any()) } returns 1000.0
        
        val updatedLoan = activeLoanState.updateLoan(testLoan, mockCalculator, LoanType.PERSONAL)
        assertEquals("Active", updatedLoan.status)
        
        // Test state transition when due date passes
        val overdueLoan = testLoan.copy(dueIn = -1)
        val nextState = activeLoanState.handleDueDate(overdueLoan)
        assertTrue(nextState is OverdueLoanState)
    }

    @Test
    fun `test paid loan state`() {
        assertEquals("Paid", paidLoanState.getStateName())
        
        every { mockCalculator.calculateInterest(any(), any(), any()) } returns 0.0
        
        val updatedLoan = paidLoanState.updateLoan(testLoan, mockCalculator, LoanType.PERSONAL)
        assertEquals("Paid", updatedLoan.status)
        
        // Paid loans should remain in paid state
        val nextState = paidLoanState.handleDueDate(testLoan)
        assertTrue(nextState is PaidLoanState)
    }

    @Test
    fun `test overdue loan state`() {
        assertEquals("Overdue", overdueLoanState.getStateName())
        
        every { mockCalculator.calculateInterest(any(), any(), any()) } returns 1500.0
        
        val updatedLoan = overdueLoanState.updateLoan(testLoan, mockCalculator, LoanType.PERSONAL)
        assertEquals("Overdue", updatedLoan.status)
        
        // Test transition to default state after grace period
        val overdueLoan = testLoan.copy(dueIn = -91)
        val nextState = overdueLoanState.handleDueDate(overdueLoan)
        assertTrue(nextState is DefaultLoanState)
    }

    @Test
    fun `test default loan state`() {
        assertEquals("Default", defaultLoanState.getStateName())
        
        every { mockCalculator.calculateInterest(any(), any(), any()) } returns 2000.0
        
        val updatedLoan = defaultLoanState.updateLoan(testLoan, mockCalculator, LoanType.PERSONAL)
        assertEquals("Default", updatedLoan.status)
        
        // Default loans remain in default state
        val nextState = defaultLoanState.handleDueDate(testLoan)
        assertTrue(nextState is DefaultLoanState)
    }

    @Test
    fun `test state factory method`() {
        assertTrue(LoanState.fromStatus("active") is ActiveLoanState)
        assertTrue(LoanState.fromStatus("paid") is PaidLoanState)
        assertTrue(LoanState.fromStatus("overdue") is OverdueLoanState)
        assertTrue(LoanState.fromStatus("default") is DefaultLoanState)
        assertTrue(LoanState.fromStatus("unknown") is ActiveLoanState) // Default case
    }
}
