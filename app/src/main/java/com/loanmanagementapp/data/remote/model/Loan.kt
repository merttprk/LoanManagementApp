package com.loanmanagementapp.data.remote.model

import com.loanmanagementapp.core.type.LoanType

data class Loan(
    val id: String,
    val name: String,
    var principalAmount: Double,
    var interestRate: Double,
    var status: String,
    var dueIn: Int,
    val type: LoanType = LoanType.PERSONAL
)