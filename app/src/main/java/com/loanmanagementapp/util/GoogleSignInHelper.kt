package com.loanmanagementapp.util

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "GoogleSignInHelper"

    // Replace with your web client ID from Google Cloud Console
    private val webClientId = "280085505226-jrf7q1605qcq7bdbbu5sojdubhjhua6o.apps.googleusercontent.com"

    private val googleSignInOptions: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
    }

    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignIn.getClient(context, googleSignInOptions)
    }

    /**
     * Get the sign-in intent to start the Google Sign-In flow
     */
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    /**
     * Handle the result from the Google Sign-In activity
     * @param data The intent data returned from the sign-in activity
     * @return The GoogleSignInAccount if successful, null otherwise
     */
    fun handleSignInResult(data: Intent?): GoogleSignInAccount? {
        try {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            return task.getResult(ApiException::class.java)
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
            return null
        }
    }

    /**
     * Check if the user is already signed in
     * @return The GoogleSignInAccount if the user is signed in, null otherwise
     */
    fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    /**
     * Sign out the current Google user
     */
    fun signOut() {
        googleSignInClient.signOut()
    }

    /**
     * Revoke access for the current Google user
     */
    fun revokeAccess() {
        googleSignInClient.revokeAccess()
    }
}
