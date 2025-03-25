package com.loanmanagementapp.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.data.remote.model.Payment
import com.loanmanagementapp.domain.usecase.loan.CalculateInterestUseCase
import com.loanmanagementapp.domain.usecase.loan.GetLoanDetailsUseCase
import com.loanmanagementapp.domain.usecase.payment.GetLoanPaymentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Kredi detayları ekranı için ViewModel
 */
@HiltViewModel
class LoanDetailsViewModel @Inject constructor(
    private val getLoanDetailsUseCase: GetLoanDetailsUseCase,
    private val getLoanPaymentsUseCase: GetLoanPaymentsUseCase,
    private val calculateInterestUseCase: CalculateInterestUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoanDetailsState())
    val state: StateFlow<LoanDetailsState> = _state.asStateFlow()

    private val _paymentState = MutableStateFlow(PaymentHistoryState())
    val paymentState: StateFlow<PaymentHistoryState> = _paymentState.asStateFlow()

    /**
     * Tüm kredileri getirir
     * @param context Uygulama context'i
     */
    fun getLoans(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val loans = getLoanDetailsUseCase(context)
                _state.value = _state.value.copy(isLoading = false, loans = loans)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    /**
     * Belirli bir kredi ID'sine göre kredi detaylarını getirir
     * @param context Uygulama context'i
     * @param loanId Kredi ID'si
     */
    fun getLoanById(context: Context, loanId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val loans = getLoanDetailsUseCase(context)
                // Fetch loan by ID
                val loan = loans.find { it.id == loanId }
                _state.value = _state.value.copy(isLoading = false, selectedLoan = loan)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    /**
     * Belirli bir kredi için ödeme geçmişini getirir
     * @param context Uygulama context'i
     * @param loanId Kredi ID'si
     */
    fun getLoanPayments(context: Context, loanId: String) {
        viewModelScope.launch {
            _paymentState.value = _paymentState.value.copy(isLoading = true, error = null)
            try {
                // Get loan payments
                val payments = getLoanPaymentsUseCase(context, loanId)
                _paymentState.value = _paymentState.value.copy(isLoading = false, payments = payments)
            } catch (e: Exception) {
                _paymentState.value = _paymentState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    /**
     * Belirli bir kredi için faiz hesaplar
     * @param loan Faizi hesaplanacak kredi
     * @param months Faiz hesaplaması için ay sayısı
     * @return Hesaplanan faiz tutarı
     */
    fun calculateInterest(loan: Loan, months: Int): Double {
        return calculateInterestUseCase.execute(loan, months, loan.type)
    }

    /**
     * Kredi seçer
     * @param loan Seçilen kredi
     */
    fun selectLoan(loan: Loan) {
        _state.value = _state.value.copy(selectedLoan = loan)
    }
}

/**
 * Kredi detayları ekranı için state sınıfı
 */
data class LoanDetailsState(
    val isLoading: Boolean = false,
    val loans: List<Loan> = emptyList(),
    val selectedLoan: Loan? = null,
    val error: String? = null
)

/**
 * Ödeme geçmişi ekranı için state sınıfı
 */
data class PaymentHistoryState(
    val isLoading: Boolean = false,
    val payments: List<Payment> = emptyList(),
    val error: String? = null
)
