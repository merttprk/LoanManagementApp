package com.loanmanagementapp.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.IOException
import javax.inject.Inject

class MockLoanService @Inject constructor() : LoanService {
    override suspend fun loadLoans(context: Context): List<Loan> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("loans.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val loans = mutableListOf<Loan>()

            for (i in 0 until jsonArray.length()) {
                val jsonLoan = jsonArray.getJSONObject(i)
                loans.add(
                    Loan(
                        name = jsonLoan.getString("name"),
                        principalAmount = jsonLoan.getDouble("principal_amount"),
                        interestRate = jsonLoan.getDouble("interest_rate"),
                        status = jsonLoan.getString("status"),
                        dueIn = jsonLoan.getInt("due_in")
                    )
                )
            }
            return@withContext loans
        } catch (e: IOException) {
            throw RuntimeException("Error loading loans.json", e)
        }
    }

    override suspend fun saveLoans(loans: List<Loan>) {
        println("Loans saved: $loans")
    }
}