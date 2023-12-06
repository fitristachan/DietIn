package com.dietinapp.firebase

import android.content.Context
import com.google.firebase.auth.FirebaseAuth

object AuthInjection {
    fun provideRepository(context: Context): AuthRepository {
        val firebaseAuth = FirebaseAuth.getInstance()
        return AuthRepository.getInstance(firebaseAuth)
    }
}