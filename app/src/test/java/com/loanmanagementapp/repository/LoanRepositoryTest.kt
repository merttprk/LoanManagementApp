package com.loanmanagementapp.repository

import android.content.Context
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.util.TestUtil
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoanRepositoryTest {
    
    private lateinit var repository: MockLoanRepository
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        repository = MockLoanRepository()
        mockContext = mockk(relaxed = true)
    }

    @Test
    fun `test update loans`() = runBlocking {
        val loan = TestUtil.createTestLoan()
        repository.addLoan(loan)
        
        val loans = repository.updateLoans(mockContext)
        assertEquals(1, loans.size)
        assertEquals(loan.id, loans.first().id)
    }

    @Test
    fun `test calculate interest`() {
        val loan = TestUtil.createTestLoan(
            principalAmount = 10000.0,
            interestRate = 12.5
        )
        
        val interest = repository.calculateInterest(loan, 12)
        assertEquals(1000.0, interest, 0.01) // Mock implementation returns 10%
    }

    @Test
    fun `test get recommended term`() {
        assertEquals(36, repository.getRecommendedTerm(LoanType.PERSONAL))
        assertEquals(60, repository.getRecommendedTerm(LoanType.AUTO))
        assertEquals(360, repository.getRecommendedTerm(LoanType.MORTGAGE))
        assertEquals(84, repository.getRecommendedTerm(LoanType.BUSINESS))
    }

    @Test
    fun `test save loans`() = runBlocking {
        val loans = listOf(
            TestUtil.createTestLoan(id = "1"),
            TestUtil.createTestLoan(id = "2")
        )
        
        repository.saveLoans(mockContext, loans)
        val savedLoans = repository.updateLoans(mockContext)
        
        assertEquals(2, savedLoans.size)
        assertEquals(loans[0].id, savedLoans[0].id)
        assertEquals(loans[1].id, savedLoans[1].id)
    }

    @Test
    fun `test get active loans`() = runBlocking {
        val activeLoan = TestUtil.createTestLoan(status = "Active")
        val paidLoan = TestUtil.createPaidLoan()
        
        repository.addLoan(activeLoan)
        repository.addLoan(paidLoan)
        
        val activeLoans = repository.getActiveLoans(mockContext)
        assertEquals(1, activeLoans.size)
        assertEquals(activeLoan.id, activeLoans.first().id)
    }

    @Test
    fun `test get passive loans`() = runBlocking {
        val activeLoan = TestUtil.createTestLoan(status = "Active")
        val paidLoan = TestUtil.createPaidLoan()
        val overdueLoan = TestUtil.createOverdueLoan()
        
        repository.addLoan(activeLoan)
        repository.addLoan(paidLoan)
        repository.addLoan(overdueLoan)
        
        val passiveLoans = repository.getPassiveLoans(mockContext)
        assertEquals(2, passiveLoans.size)
        assertTrue(passiveLoans.any { it.id == paidLoan.id })
        assertTrue(passiveLoans.any { it.id == overdueLoan.id })
    }

    @Test
    fun `test helper methods`() = runBlocking {
        // Test addLoan and getLoanById
        val loan = TestUtil.createTestLoan()
        repository.addLoan(loan)
        val retrievedLoan = repository.getLoanById(loan.id)
        assertNotNull(retrievedLoan)
        assertEquals(loan.id, retrievedLoan?.id)
        
        // Test updateLoan
        val updatedLoan = loan.copy(
            principalAmount = 20000.0
        )
        repository.updateLoan(updatedLoan)
        val retrievedUpdatedLoan = repository.getLoanById(loan.id)
        assertEquals(20000.0, retrievedUpdatedLoan?.principalAmount ?: 0.0, 0.01)
        
        // Test deleteLoan
        repository.deleteLoan(loan.id)
        val deletedLoan = repository.getLoanById(loan.id)
        assertNull(deletedLoan)
    }
}
