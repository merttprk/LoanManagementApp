package com.loanmanagementapp.data.repository

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.loanmanagementapp.core.type.LoanType
import com.loanmanagementapp.data.remote.model.Loan
import com.loanmanagementapp.domain.repository.LoanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreLoanRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : LoanRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val LOANS_COLLECTION = "loans"
        private const val LOAN_DATA_DOCUMENT = "loan_data"
    }

    override suspend fun updateLoans(context: Context): List<Loan> = withContext(Dispatchers.IO) {
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
            
            val loansArray = snapshot.get("loans") as? List<Map<String, Any>> ?: emptyList()
            
            val allLoans = ArrayList<Loan>()
            
            for (loanData in loansArray) {
                try {
                    val loan = Loan(
                        id = (loanData["id"] as? String) ?: "",
                        name = (loanData["name"] as? String) ?: "",
                        principalAmount = (loanData["principalAmount"] as? Number)?.toDouble() ?: 0.0,
                        interestRate = (loanData["interestRate"] as? Number)?.toDouble() ?: 0.0,
                        status = (loanData["status"] as? String) ?: "",
                        dueIn = (loanData["dueIn"] as? Number)?.toInt() ?: 0,
                        type = try {
                            LoanType.valueOf(((loanData["type"] as? String) ?: "PERSONAL").uppercase())
                        } catch (e: IllegalArgumentException) {
                            Timber.e(e, "Geçersiz kredi türü, varsayılan olarak PERSONAL kullanılıyor")
                            LoanType.PERSONAL
                        }
                    )
                    allLoans.add(loan)
                    Timber.d("Kredi yüklendi: ${loan.id} - ${loan.name}")
                } catch (e: Exception) {
                    Timber.e(e, "Kredi verisi dönüştürülürken hata oluştu")
                }
            }
            
            Timber.d("Firestore'dan toplam ${allLoans.size} kredi yüklendi")
            return@withContext allLoans
        } catch (e: Exception) {
            Timber.e(e, "Krediler yüklenirken hata oluştu", e)
            return@withContext emptyList()
        }
    }

    override fun calculateInterest(loan: Loan, months: Int): Double {
        return when (loan.type) {
            LoanType.PERSONAL -> 0.125 * loan.principalAmount * months / 12  // 12.5% annual rate
            LoanType.AUTO -> 0.075 * loan.principalAmount * months / 12      // 7.5% annual rate
            LoanType.MORTGAGE -> 0.0525 * loan.principalAmount * months / 12 // 5.25% annual rate
            LoanType.BUSINESS -> 0.0975 * loan.principalAmount * months / 12 // 9.75% annual rate
            LoanType.EDUCATION -> 0.045 * loan.principalAmount * months / 12 // 4.5% annual rate
        }
    }

    override fun getRecommendedTerm(loanType: LoanType): Int {
        return when (loanType) {
            LoanType.PERSONAL -> 36   // 3 years
            LoanType.AUTO -> 60       // 5 years
            LoanType.MORTGAGE -> 360  // 30 years
            LoanType.BUSINESS -> 84   // 7 years
            LoanType.EDUCATION -> 120  // 10 years
        }
    }

    override suspend fun saveLoans(context: Context, loans: List<Loan>) = withContext(Dispatchers.IO) {
        try {
            val currentUserId = getCurrentUserId()
            
            val loanDataDoc = firestore.collection(USERS_COLLECTION)
                .document(currentUserId)
                .collection(LOANS_COLLECTION)
                .document(LOAN_DATA_DOCUMENT)
            
            val loanData = hashMapOf(
                "loans" to loans.map { loan ->
                    hashMapOf(
                        "id" to loan.id,
                        "name" to loan.name,
                        "principalAmount" to loan.principalAmount,
                        "interestRate" to loan.interestRate,
                        "status" to loan.status,
                        "dueIn" to loan.dueIn,
                        "type" to loan.type.name
                    )
                }
            )
            
            loanDataDoc.set(loanData).await()
            Timber.d("${loans.size} kredi başarıyla kaydedildi")
        } catch (e: Exception) {
            Timber.e(e, "Krediler kaydedilirken hata oluştu")
        }
    }

    override suspend fun getActiveLoans(context: Context): List<Loan> = withContext(Dispatchers.IO) {
        updateLoans(context).filter { it.status.equals("active", ignoreCase = true) }
    }

    override suspend fun getPassiveLoans(context: Context): List<Loan> = withContext(Dispatchers.IO) {
        updateLoans(context).filter { !it.status.equals("active", ignoreCase = true) }
    }

    private fun getCurrentUserId(): String {
        return "TdzvQ2hpmIYw9EODzMKPsWErh0C3"
    }
}
