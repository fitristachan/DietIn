package com.dietinapp.firebase

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)

            val firebaseAuth = Firebase.auth

            // Try to sign in with the given credentials
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        onAuthComplete(signInTask.result!!)
                    } else {
                        val exception = signInTask.exception
                        if (exception is FirebaseAuthInvalidUserException) {
                            firebaseAuth.createUserWithEmailAndPassword(
                                account.email!!, ""
                            )
                                .addOnCompleteListener { createAccountTask ->
                                    if (createAccountTask.isSuccessful) {
                                        onAuthComplete(createAccountTask.result!!)
                                    } else {
                                        createAccountTask.exception?.message ?: "Unknown error"
                                    }
                                }
                        } else {
                            exception?.message ?: "Unknown error"
                        }
                    }
                }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }
}
