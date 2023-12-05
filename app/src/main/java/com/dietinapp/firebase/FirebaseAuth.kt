package com.dietinapp.firebase

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                if (authResult.additionalUserInfo?.isNewUser == true) {
                    val user = authResult.user
                    val userProfileChangeRequest =
                        UserProfileChangeRequest.Builder()
                            .setDisplayName("")
                            .build()

                    user?.updateProfile(userProfileChangeRequest)
                        ?.addOnCompleteListener { profileUpdateTask ->
                            if (profileUpdateTask.isSuccessful) {
                                onAuthComplete(authResult)
                            }
                        }
                } else {
                    onAuthComplete(authResult)
                }

            }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }
}

fun registerCustom(
    auth: FirebaseAuth,
    onAuthComplete: () -> Unit,
    onAuthError: (String?) -> Unit,
    email: String,
    password: String,
    username: String
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "createUserWithEmail:success")

                val user = task.result.user
                val userProfileChangeRequest =
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()

                user?.updateProfile(userProfileChangeRequest)
                    ?.addOnCompleteListener { profileUpdateTask ->
                        if (profileUpdateTask.isSuccessful) {
                            onAuthComplete()
                        } else {
                            onAuthError(profileUpdateTask.exception?.message)
                        }
                    }

            } else {
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                onAuthError(task.exception?.message)
            }
        }
}


fun registerByGoogle(
    auth: FirebaseAuth,
    onAuthComplete: () -> Unit,
    onAuthError: (String?) -> Unit,
    username: String,
    password: String
) {
    val user = auth.currentUser
    if (user != null) {
        user.updatePassword(password)
        val userProfileChangeRequest = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            .build()

        user.updateProfile(userProfileChangeRequest)
            .addOnCompleteListener { profileUpdateTask ->
                if (profileUpdateTask.isSuccessful) {
                    onAuthComplete()
                } else {
                    onAuthError(profileUpdateTask.exception?.message)
                }
            }
    }
}

fun loginCustom(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (String?) -> Unit
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { signInTask ->
            if (signInTask.isSuccessful) {
                val authResult = signInTask.result
                onAuthComplete(authResult)
            } else {
                onAuthError(signInTask.exception?.message)
            }
        }
}
