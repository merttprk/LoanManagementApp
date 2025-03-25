package com.loanmanagementapp.data.di

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.loanmanagementapp.util.GoogleSignInHelper
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
            }
        } catch (_: Exception) {
        }
        
        return Firebase.auth
    }


    @Provides
    @Singleton
    fun provideGoogleSignInHelper(@ApplicationContext context: Context): GoogleSignInHelper {
        return GoogleSignInHelper(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        googleSignInHelper: GoogleSignInHelper
    ): AuthRepository = AuthRepositoryImpl(context, firebaseAuth, firestore, googleSignInHelper)

}