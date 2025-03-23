package com.loanmanagementapp.data
import android.content.Context
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class LoanRepository @Inject constructor(private val loanService: LoanService) {
    suspend fun updateLoans(context: Context): List<Loan> {
        val loans = loanService.loadLoans(context).toMutableList()

        for (i in loans.indices) {
            if (loans[i].status != "paid" && loans[i].status != "default") {
                if (loans[i].dueIn > 0) {
                    loans[i].interestRate += 0.5 // Faiz oranı her gün artıyor
                }
            } else {
                if (loans[i].principalAmount < 1000) {
                    loans[i].status = "paid"
                }
            }

            if (loans[i].status == "overdue" && loans[i].principalAmount > 5000) {
                loans[i].status = "default"
            }

            loans[i].dueIn -= 1

            if (loans[i].dueIn < 0) {
                if (loans[i].status != "paid") {
                    loans[i].status = if (loans[i].principalAmount > 0) "overdue" else "default"
                } else {
                    loans[i].status = "paid"
                }
            }
        }

        loanService.saveLoans(loans)
        return loans
    }
}