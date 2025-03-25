package com.loanmanagementapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.data.remote.model.Payment
import com.loanmanagementapp.domain.usecase.loan.GetActiveLoansUseCase
import com.loanmanagementapp.domain.usecase.loan.GetPassiveLoansUseCase
import com.loanmanagementapp.domain.usecase.payment.GetLoanPaymentsUseCase
import com.loanmanagementapp.domain.usecase.payment.GetPaymentHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Ana sayfa için ViewModel sınıfı
 * Kredi ve ödeme verilerini yönetir
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getActiveLoansUseCase: GetActiveLoansUseCase,
    private val getPassiveLoansUseCase: GetPassiveLoansUseCase,
    private val getPaymentHistoryUseCase: GetPaymentHistoryUseCase,
    private val getLoanPaymentsUseCase: GetLoanPaymentsUseCase
) : ViewModel() {

    // UI durumları için StateFlow'lar
    private val _activeLoans = MutableStateFlow<List<Loan>>(emptyList())
    val activeLoans: StateFlow<List<Loan>> = _activeLoans.asStateFlow()

    private val _passiveLoans = MutableStateFlow<List<Loan>>(emptyList())
    val passiveLoans: StateFlow<List<Loan>> = _passiveLoans.asStateFlow()

    private val _paymentHistory = MutableStateFlow<List<Payment>>(emptyList())
    val paymentHistory: StateFlow<List<Payment>> = _paymentHistory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Aktif kredileri yükler
     * @param context Uygulama context'i
     */
    fun loadActiveLoans(context: Context) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val loans = getActiveLoansUseCase(context)
                _activeLoans.value = loans
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Krediler yüklenirken bir hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Pasif kredileri yükler
     * @param context Uygulama context'i
     */
    fun loadPassiveLoans(context: Context) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val loans = getPassiveLoansUseCase(context)
                _passiveLoans.value = loans
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Pasif krediler yüklenirken bir hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Ödeme geçmişini yükler
     * @param context Uygulama context'i
     */
    fun loadPaymentHistory(context: Context) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val loans = getPaymentHistoryUseCase(context)
                _paymentHistory.value = loans.map { loan ->
                    Payment(
                        id = "P${loan.id}",
                        loanId = loan.id,
                        amount = loan.amount,
                        paymentDate = System.currentTimeMillis(),
                        isPrincipal = true,
                        isInterest = true,
                        status = loan.status,
                        description = loan.description
                    )
                }.sortedByDescending { it.paymentDate }
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ödeme geçmişi yüklenirken bir hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Belirli bir krediye ait ödeme geçmişini yükler
     * @param context Uygulama context'i
     * @param loanId Kredi ID'si
     */
    fun loadPaymentHistoryForLoan(context: Context, loanId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val payments = getLoanPaymentsUseCase(context, loanId)
                _paymentHistory.value = payments.sortedByDescending { it.paymentDate }
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Ödeme geçmişi yüklenirken bir hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Hata mesajını temizler
     */
    fun clearError() {
        _error.value = null
    }
}
