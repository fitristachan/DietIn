package com.dietinapp.firebase

import android.content.Context
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.dietinapp.database.datastore.UserPreferenceViewModel
import com.dietinapp.ui.navigation.AuthScreen
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _expirationTokenTimeStamp = MutableStateFlow(0L)
    val expirationTokenTimeStamp: StateFlow<Long> = _expirationTokenTimeStamp

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val oldPassword = MutableLiveData<String>()
    val username = MutableLiveData<String>()

    fun googleLauncher(
        result: ActivityResult,
        context: CoroutineContext,
        navController: NavController,
        userPreferenceViewModel: UserPreferenceViewModel,
    ) {
        _errorMessage.value = ""
        _isLoading.value = true
        authRepository.rememberFirebaseAuthLauncher(
            result,
            context,
            onAuthComplete = { authResult ->
                _isLoading.value = false
                saveUserByGoogle(navController, userPreferenceViewModel, authResult)
            },
            onAuthError = {
                _isLoading.value = false
                _errorMessage.value = it.message
                _showDialog.value = true
            })
    }

    fun registerByCustom(
        navController: NavController,
    ) {
        _errorMessage.value = ""
        _isLoading.value = true
        authRepository.registerCustom(
            onAuthComplete = {
                _isLoading.value = false
                navController.navigate(AuthScreen.Login.route)
            },
            onAuthError = {
                _isLoading.value = false
                _errorMessage.value = it
                _showDialog.value = true
            },
            email = email.value.toString(),
            password = password.value.toString(),
            username = username.value.toString()
        )
    }

    fun registerByGoogle(
        navController: NavController,
        email: String
    ) {
        _errorMessage.value = ""
        _isLoading.value = true
        authRepository.registerByGoogle(
            username = username.value.toString(),
            password = password.value.toString(),
            email = email,
            onAuthComplete = {
                _isLoading.value = false
                navController.navigate(AuthScreen.Login.route)
            },
            onAuthError = { errorMsg ->
                _isLoading.value = false
                _errorMessage.value = errorMsg.toString()
                _showDialog.value = true
            },
        )
    }

    fun loginByCustom(
        userPreferenceViewModel: UserPreferenceViewModel,
    ) {
        _errorMessage.value = ""
        _isLoading.value = true
        authRepository.loginCustom(
            email = email.value.toString(),
            password = password.value.toString(),
            onAuthComplete = { result ->
                _isLoading.value = false
                saveUserCustom(userPreferenceViewModel, result)
            },
            onAuthError = {
                _isLoading.value = false
                _errorMessage.value = it
                _showDialog.value = true
            }
        )
    }

    fun updatePhoto(
        photo: Uri,
        onUpdateComplete: () -> Unit,
        onUpdateError: (String?) -> Unit
    ) {
        authRepository.updatePhoto(
            photo, onUpdateComplete, onUpdateError
        )
    }

    fun updateUsername(
        onUpdateComplete: () -> Unit,
        onUpdateError: (String?) -> Unit
    ) {
        authRepository.updateUsername(
            username.value.toString(), onUpdateComplete, onUpdateError
        )
    }

    fun updatePassword(
        oldEmail: String,
        onUpdateComplete: () -> Unit,
        onUpdateError: (String?) -> Unit
    ) {
        authRepository.loginCustom(
            email = oldEmail,
            password = oldPassword.value.toString(),
            onAuthComplete = { result ->
                _isLoading.value = false
                authRepository.updatePassword(
                    password.value.toString(), onUpdateComplete, onUpdateError
                )
            },
            onAuthError = {
                onUpdateError(it)
            }
        )
    }

    fun updateEmail(
        onUpdateComplete: () -> Unit,
        onUpdateError: (String?) -> Unit
    ) {
        authRepository.updateEmail(
            email.value.toString(), onUpdateComplete, onUpdateError
        )
    }

    private fun saveUserByGoogle(
        navController: NavController,
        userPreferenceViewModel: UserPreferenceViewModel,
        result: AuthResult
    ) {
        if (result.user?.displayName == "" || result.user?.displayName == "Null" || result.user?.displayName == null) {
            val email = result.user?.email.toString()
            navController.navigate(AuthScreen.RegisterByGoogle.createRoute(email))
        } else {
            result.user?.getIdToken(true)
                ?.addOnSuccessListener {
                    userPreferenceViewModel.saveToken(
                        token = it.token.toString(),
                        username = result.user?.displayName.toString(),
                        email = result.user?.email.toString(),
                        photo = result.user?.photoUrl.toString(),
                        session = true
                    )
                }
                ?.addOnFailureListener {
                    _errorMessage.value = it.message
                }
        }
    }

    private fun saveUserCustom(
        userPreferenceViewModel: UserPreferenceViewModel,
        result: AuthResult
    ) {
        result.user?.getIdToken(true)
            ?.addOnSuccessListener {
                userPreferenceViewModel.saveToken(
                    token = it.token.toString(),
                    username = result.user?.displayName.toString(),
                    email = result.user?.email.toString(),
                    photo = result.user?.photoUrl.toString(),
                    session = true
                )
            }
            ?.addOnFailureListener {
                _errorMessage.value = it.message
            }
    }


    fun tokenValidationCheck(
        userPreferenceViewModel: UserPreferenceViewModel,
        onSignOutComplete: (String) -> Unit,
        onError: (String) -> Unit,
    ) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)
            ?.addOnSuccessListener {
                _expirationTokenTimeStamp.value = it.expirationTimestamp
                userPreferenceViewModel.reloadToken(it.token.toString())
            }
            ?.addOnFailureListener { exception ->
                when (exception) {
                    is FirebaseAuthInvalidUserException -> {
                        signOut(
                            userPreferenceViewModel = userPreferenceViewModel,
                            onSignOutComplete = {
                                onSignOutComplete(exception.localizedMessage!!.toString())
                            }
                        )
                    }

                    else -> {
                        // Handle other failure scenarios
                        onError(exception.localizedMessage ?: "Unknown error")
                    }
                }
            }
    }

    fun isFirebaseTokenExpired(expirationTokenTimeStamp: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        return (expirationTokenTimeStamp < currentTime)
    }


    fun signOut(
        userPreferenceViewModel: UserPreferenceViewModel,
        onSignOutComplete: () -> Unit
    ) {
        _isLoading.value = true
        authRepository.signOut(
            userPreferenceViewModel,
            onSignOutComplete
        )
        _isLoading.value = false
    }
}

class AuthViewModelFactory private constructor(private val authRepository: AuthRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: AuthViewModelFactory? = null
        fun getInstance(context: Context): AuthViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: AuthViewModelFactory(AuthInjection.provideRepository(context))
            }.also { instance = it }
    }
}