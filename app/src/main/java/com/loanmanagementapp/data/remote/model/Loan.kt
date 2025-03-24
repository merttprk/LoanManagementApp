package com.loanmanagementapp.data.remote.model

data class Loan(
    val id: String,
    val name: String,
    var principalAmount: Double,
    var interestRate: Double,
    var status: String,
    var dueIn: Int
)