package com.loanmanagementapp.data.remote.model

import java.util.Date

/**
 * Kredi ödemelerini temsil eden veri sınıfı
 */
data class Payment(
    val id: String,                // Ödeme ID'si
    val loanId: String,           // Bağlı olduğu kredi ID'si
    val amount: Double,           // Ödeme tutarı
    val dueDate: Date,            // Ödeme vadesi
    val status: String,           // Ödeme durumu (pending, completed, failed)
    val paymentDate: Date?= null // Ödemenin yapıldığı tarih (null ise henüz ödenmemiş)
)