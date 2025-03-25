package com.loanmanagementapp.data.repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.loanmanagementapp.data.remote.model.Payment
import com.loanmanagementapp.domain.repository.PaymentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PaymentRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val LOANS_COLLECTION = "loans"
        private const val LOAN_DATA_DOCUMENT = "loan_data"
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }

    override suspend fun getPaymentsForLoan(context: Context, loanId: String): List<Payment> = withContext(Dispatchers.IO) {
        try {
            val currentUserId = getCurrentUserId()
            
            val loanDataDoc = firestore.collection(USERS_COLLECTION)
                .document(currentUserId)
                .collection(LOANS_COLLECTION)
                .document(LOAN_DATA_DOCUMENT)
            
            val snapshot = loanDataDoc.get().await()
            
            if (!snapshot.exists()) {
                Timber.d("Loan data document not found for user: $currentUserId")
                return@withContext emptyList()
            }
            
            val paymentsArray = snapshot.get("payments") as? List<Map<String, Any>> ?: emptyList()
            
            val payments = paymentsArray
                .filter { (it["loanId"] as? String) == loanId }
                .mapNotNull { paymentData ->
                    try {
                        Payment(
                            id = (paymentData["id"] as? String) ?: "",
                            loanId = loanId,
                            amount = (paymentData["amount"] as? Number)?.toDouble() ?: 0.0,
                            paymentDate = parseDate(paymentData["date"]),
                            isPrincipal = (paymentData["isPrincipal"] as? Boolean) ?: true,
                            isInterest = (paymentData["isInterest"] as? Boolean) ?: true,
                            status = (paymentData["status"] as? String) ?: "Completed",
                            description = (paymentData["description"] as? String) ?: ""
                        )
                    } catch (e: Exception) {
                        Timber.e(e, "Ödeme verisi dönüştürülürken hata oluştu")
                        null
                    }
                }
            
            Timber.d("Firestore'dan $loanId kredisi için ${payments.size} ödeme yüklendi")
            return@withContext payments.sortedByDescending { it.paymentDate }
        } catch (e: Exception) {
            Timber.e(e, "Kredi ödemeleri yüklenirken hata oluştu: $loanId")
            return@withContext emptyList()
        }
    }
    
    override suspend fun savePayment(context: Context, payment: Payment) = withContext(Dispatchers.IO) {
        try {
            val currentUserId = getCurrentUserId()
            
            val loanDataDoc = firestore.collection(USERS_COLLECTION)
                .document(currentUserId)
                .collection(LOANS_COLLECTION)
                .document(LOAN_DATA_DOCUMENT)
            
            val snapshot = loanDataDoc.get().await()
            
            val existingPayments = (snapshot.get("payments") as? List<Map<String, Any>> ?: emptyList()).toMutableList()
            
            val paymentData = hashMapOf(
                "id" to payment.id,
                "loanId" to payment.loanId,
                "amount" to payment.amount,
                "date" to DATE_FORMAT.format(payment.paymentDate),
                "isPrincipal" to payment.isPrincipal,
                "isInterest" to payment.isInterest,
                "status" to payment.status,
                "description" to payment.description
            )
            
            existingPayments.add(paymentData)
            
            loanDataDoc.update("payments", existingPayments).await()
            Timber.d("Ödeme başarıyla kaydedildi: ${payment.id}")
        } catch (e: Exception) {
            Timber.e(e, "Ödeme kaydedilirken hata oluştu")
        }
    }

    private fun getCurrentUserId(): String {
        return "TdzvQ2hpmIYw9EODzMKPsWErh0C3"
    }
    
    private fun parseDate(dateObj: Any?): Long {
        return when (dateObj) {
            is String -> try {
                DATE_FORMAT.parse(dateObj)?.time ?: System.currentTimeMillis()
            } catch (e: Exception) {
                Timber.e(e, "Tarih ayrıştırılamadı: $dateObj")
                System.currentTimeMillis()
            }
            is Date -> dateObj.time
            is Long -> dateObj
            else -> System.currentTimeMillis()
        }
    }
}