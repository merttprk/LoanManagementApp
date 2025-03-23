package com.loanmanagementapp

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dagger.hilt.android.HiltAndroidApp

// Application Entry Point
@HiltAndroidApp
class LoanApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        try {
            // Check if Firebase is already initialized
            if (FirebaseApp.getApps(this).isEmpty()) {
                // Initialize Firebase
                FirebaseApp.initializeApp(this)
                Log.d("LoanApplication", "Firebase initialized successfully")
            } else {
                Log.d("LoanApplication", "Firebase was already initialized")
            }
        } catch (e: Exception) {
            Log.e("LoanApplication", "Error initializing Firebase: ${e.message}", e)
        }
    }
}
