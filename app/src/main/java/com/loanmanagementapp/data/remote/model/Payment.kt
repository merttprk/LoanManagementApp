package com.loanmanagementapp.data.remote.model

import java.util.Date

/**
 * Kredi ödemelerini temsil eden veri sınıfı
 */
data class Payment(
    val id: String,
    val loanId: String,
    val amount: Double,
    val paymentDate: Long,
    val isPrincipal: Boolean,
    val isInterest: Boolean,
    val status: String,
    val description: String
)