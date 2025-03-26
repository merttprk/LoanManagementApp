package com.loanmanagementapp.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.loanmanagementapp.data.remote.model.Payment
import com.loanmanagementapp.domain.repository.PaymentRepository
import com.loanmanagementapp.util.DateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestorePaymentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : PaymentRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val LOANS_COLLECTION = "loans"
        private const val PAYMENTS_COLLECTION = "payments"
    }

    override suspend fun getPaymentsForLoan(context: Context, loanId: String): List<Payment> = withContext(Dispatchers.IO) {
        try {
            val currentUserId = getCurrentUserId()

            val paymentsCollection = firestore.collection(USERS_COLLECTION)
                .document(currentUserId)
                .collection(LOANS_COLLECTION)
                .document(loanId)
                .collection(PAYMENTS_COLLECTION)

            val snapshot = paymentsCollection.get().await()

            if (snapshot.isEmpty) {
                Timber.d("Kredi için ödeme bulunamadı: $loanId")
                return@withContext emptyList()
            }

            return@withContext snapshot.documents.mapNotNull { document ->
                try {
                    val dueDate = document.get("dueDate")
                    val paymentDate = document.get("paymentDate")
                    
                    Payment(
                        id = document.id,
                        loanId = loanId,
                        amount = document.getDouble("amount") ?: 0.0,
                        dueDate = when (dueDate) {
                            is Timestamp -> dueDate.toDate()
                            is Date -> dueDate
                            else -> Date()
                        },
                        status = document.getString("status") ?: "pending",
                        paymentDate = when (paymentDate) {
                            is Timestamp -> paymentDate.toDate()
                            is Date -> paymentDate
                            else -> Date()
                        }
                    )
                } catch (e: Exception) {
                    Timber.e(e, "Ödeme verisi dönüştürülürken hata oluştu: ${document.id}")
                    null
                }
            }.sortedByDescending { it.dueDate }
        } catch (e: Exception) {
            Timber.e(e, "Kredi ödemeleri yüklenirken hata oluştu: $loanId")
            emptyList()
        }
    }

    /* override suspend fun savePayment(context: Context, payment: Payment) = withContext(Dispatchers.IO) {
        try {
            val currentUserId = getCurrentUserId()

            val paymentDoc = firestore.collection(USERS_COLLECTION)
                .document(currentUserId)
                .collection(LOANS_COLLECTION)
                .document(payment.loanId)
                .collection(PAYMENTS_COLLECTION)
                .document(payment.id)

            val paymentData = hashMapOf(
                "amount" to payment.amount,
                "dueDate" to Timestamp(payment.dueDate.time / 1000, 0),
                "status" to payment.status,
                "paymentDate" to Timestamp(payment.paymentDate.time / 1000, 0)
            )

            paymentDoc.set(paymentData).await()
            Timber.d("Ödeme başarıyla kaydedildi: ${payment.id}")
        } catch (e: Exception) {
            Timber.e(e, "Ödeme kaydedilirken hata oluştu: ${payment.id}")
            throw e
        }
    } */

    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("Kullanıcı oturumu bulunamadı")
    }
}