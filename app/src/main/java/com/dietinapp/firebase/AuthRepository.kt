package com.dietinapp.firebase

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import com.dietinapp.data.datastore.UserPreferenceViewModel
import com.dietinapp.ui.activity.AuthActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class AuthRepository private constructor(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    fun rememberFirebaseAuthLauncher(
        result: ActivityResult,
        context: CoroutineContext,
        onAuthComplete: (AuthResult) -> Unit,
        onAuthError: (ApiException) -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            CoroutineScope(context).launch {
                try {
                    val authResult = firebaseAuth.signInWithCredential(credential).await()
                    if (authResult.additionalUserInfo?.isNewUser == true) {
                        val user = authResult.user
                        val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                            .setDisplayName("")
                            .build()

                        user?.updateProfile(userProfileChangeRequest)?.await()
                        onAuthComplete(authResult)
                    } else {
                        onAuthComplete(authResult)
                    }
                } catch (e: ApiException) {
                    onAuthError(e)
                }
            }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }

    fun registerCustom(
        onAuthComplete: () -> Unit,
        onAuthError: (String?) -> Unit,
        email: String,
        password: String,
        username: String
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
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
        onAuthComplete: () -> Unit,
        onAuthError: (String?) -> Unit,
        username: String,
        password: String
    ) {
        val user = firebaseAuth.currentUser
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
        email: String,
        password: String,
        onAuthComplete: (AuthResult) -> Unit,
        onAuthError: (String?) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { signInTask ->
                if (signInTask.isSuccessful) {
                    val authResult = signInTask.result
                    onAuthComplete(authResult)
                } else {
                    onAuthError(signInTask.exception?.message)
                }
            }
    }

    fun signOut(
        context: Context,
        userPreferenceViewModel: UserPreferenceViewModel
    ){
        if (firebaseAuth.currentUser == null) {
            userPreferenceViewModel.deleteAll()
            context.findActivity()?.finish()
            context.startActivity(
                Intent(
                    context,
                    AuthActivity::class.java
                )
            )
        } else {
            firebaseAuth.signOut()
        }
    }

    private fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            firebaseAuth: FirebaseAuth
        ): AuthRepository = instance ?: synchronized(this) {
            instance ?: AuthRepository(firebaseAuth)
        }.also { instance = it }
    }
}
