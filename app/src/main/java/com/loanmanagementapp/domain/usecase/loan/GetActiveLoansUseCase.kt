package com.loanmanagementapp.domain.usecase.loan

import android.content.Context
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.domain.repository.LoanRepository
import javax.inject.Inject

/**
 * Aktif kredileri getiren use case
 */
class
GetActiveLoansUseCase @Inject constructor(
    private val loanRepository: LoanRepository
) {
    /**
     * Aktif kredileri getirir
     * @param context Uygulama context'i
     * @return Aktif kredi listesi
     */
    suspend operator fun invoke(context: Context): List<Loan> {
        val allLoans = loanRepository.updateLoans(context)
        return allLoans.filter { it.status.equals("active", ignoreCase = true) }
    }
}
