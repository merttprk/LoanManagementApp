package com.loanmanagementapp.data

import android.content.Context

interface LoanService {
    suspend fun loadLoans(context: Context): List<Loan>
    suspend fun saveLoans(loans: List<Loan>)
}