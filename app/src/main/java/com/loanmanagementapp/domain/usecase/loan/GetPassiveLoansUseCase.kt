package com.loanmanagementapp.domain.usecase.loan

import android.content.Context
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.domain.repository.LoanRepository
import javax.inject.Inject

/**
 * Pasif kredileri getiren use case
 */
class GetPassiveLoansUseCase @Inject constructor(
    private val loanRepository: LoanRepository
) {
    /**
     * Pasif kredileri getirir (ödenmiş, gecikmiş veya temerrüt durumundaki krediler)
     * @param context Uygulama context'i
     * @return Pasif kredi listesi
     */
    suspend operator fun invoke(context: Context): List<Loan> {
        val allLoans = loanRepository.updateLoans(context)
        // Aktif olmayan tüm kredileri getir (paid, overdue, default)
        return allLoans.filter { 
            !it.status.equals("active", ignoreCase = true) 
        }
    }
}
