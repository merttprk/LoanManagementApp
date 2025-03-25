package com.loanmanagementapp.presentation.state

import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.data.remote.model.Payment

/**
 * Ana sayfa için UI durum sınıfı
 */
data class HomeState(
    val activeLoans: List<Loan> = emptyList(),
    val paymentHistory: List<Payment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedTab: HomeTab = HomeTab.ACTIVE_LOANS
)

/**
 * Ana sayfa sekmeleri için enum sınıfı
 */
enum class HomeTab {
    ACTIVE_LOANS,
    PASSIVE_LOANS,
    PAYMENT_HISTORY,
    NEW_LOAN_APPLICATION
}
