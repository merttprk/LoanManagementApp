package com.loanmanagementapp.data.di

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.loanmanagementapp.data.repository.AuthRepositoryImpl
import com.loanmanagementapp.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(@ApplicationContext context: Context): FirebaseAuth {
        // Ensure Firebase is initialized
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
                Log.d("AuthModule", "Firebase initialized in AuthModule")
            }
        } catch (e: Exception) {
            Log.e("AuthModule", "Error initializing Firebase: ${e.message}", e)
        }
        
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(@ApplicationContext context: Context): FirebaseFirestore {
        // Firebase should already be initialized by provideFirebaseAuth
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): AuthRepository = AuthRepositoryImpl(context, firebaseAuth, firestore)

}