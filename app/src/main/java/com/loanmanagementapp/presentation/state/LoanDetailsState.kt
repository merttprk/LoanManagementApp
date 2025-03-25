package com.loanmanagementapp.presentation.state

import com.loanmanagementapp.data.remote.model.Loan

/**
 * Kredi detayları ekranının durumunu temsil eden sınıf
 */
data class LoanDetailsState(
    val isLoading: Boolean = false,
    val loans: List<Loan> = emptyList(),
    val selectedLoan: Loan? = null,
    val error: String? = null
)
