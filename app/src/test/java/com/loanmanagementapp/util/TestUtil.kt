package com.loanmanagementapp.util

import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.core.type.LoanType

object TestUtil {
    fun createTestLoan(
        id: String = "1",
        name: String = "Test Loan",
        principalAmount: Double = 10000.0,
        interestRate: Double = 12.5,
        status: String = "Active",
        dueIn: Int = 30,
        type: LoanType = LoanType.PERSONAL
    ): Loan {
        return Loan(
            id = id,
            name = name,
            principalAmount = principalAmount,
            interestRate = interestRate,
            status = status,
            dueIn = dueIn,
            type = type
        )
    }

    fun createOverdueLoan(): Loan {
        return createTestLoan(
            id = "2",
            status = "Overdue",
            dueIn = -30
        )
    }

    fun createDefaultLoan(): Loan {
        return createTestLoan(
            id = "3",
            status = "Default",
            dueIn = -90
        )
    }

    fun createPaidLoan(): Loan {
        return createTestLoan(
            id = "4",
            status = "Paid",
            dueIn = 0
        )
    }
}
