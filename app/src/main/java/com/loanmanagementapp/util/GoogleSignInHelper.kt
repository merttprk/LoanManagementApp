package com.loanmanagementapp.util

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val googleSignInClient: GoogleSignInClient

    init {
        // Firebase konsolundan alınan Web Client ID
        // Not: Bu ID'nin Firebase konsolunda doğru yapılandırıldığından emin olun
        val webClientId = ""

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId) // ID token'i istiyoruz
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent(): Intent {
        // Her giriş denemesinde hesap seçici dialogunun görüntülenmesi için mevcut hesaptan çıkış yapıyoruz
        googleSignInClient.signOut().addOnCompleteListener {
            Timber.d("Önceki hesaptan çıkış yapıldı, hesap seçici dialog gösterilecek")
        }
        return googleSignInClient.signInIntent
    }

    fun getAccountFromIntent(data: Intent?): GoogleSignInAccount? {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        return try {
            val account = task.getResult(ApiException::class.java)
            Timber.d("Google Sign-In başarılı: ${account.email}")
            account
        } catch (e: ApiException) {
            Timber.e("Google Sign-In hatası: ${e.statusCode} - ${e.message}")
            null
        }
    }

    fun signOut() {
        googleSignInClient.signOut()
    }

    fun isUserSignedIn(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account != null
    }
}
