package com.loanmanagementapp.data

import android.content.Context
import com.loanmanagementapp.data.remote.model.Loan

interface LoanService {
    suspend fun loadLoans(context: Context): List<Loan>
    suspend fun saveLoans(loans: List<Loan>)
}