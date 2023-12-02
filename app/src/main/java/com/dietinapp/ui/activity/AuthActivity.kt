package com.dietinapp.ui.activity

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dietinapp.R
import com.dietinapp.data.datastore.UserPreference
import com.dietinapp.data.datastore.UserPreferenceViewModel
import com.dietinapp.data.datastore.UserPreferenceViewModelFactory
import com.dietinapp.data.datastore.dataStore
import com.dietinapp.firebase.rememberFirebaseAuthLauncher
import com.dietinapp.ui.activity.mainfeature.MainActivity
import com.dietinapp.ui.navigation.AuthScreen
import com.dietinapp.ui.screen.login.LoginScreen
import com.dietinapp.ui.screen.register.RegisterScreen
import com.dietinapp.ui.theme.DietInTheme
import com.dietinapp.utils.LoadingScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthResult

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = UserPreference.getInstance(application.dataStore)
        val userPreferenceViewModel =
            ViewModelProvider(
                this,
                UserPreferenceViewModelFactory(pref)
            )[UserPreferenceViewModel::class.java]

        userPreferenceViewModel.getSession().observe(this) { session: Boolean? ->
            if (session == true) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                setContent {
                    DietInTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Auth(userPreferenceViewModel = userPreferenceViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Auth(
    modifier: Modifier = Modifier,
    userPreferenceViewModel: UserPreferenceViewModel,
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current
    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->
            saveUserByGoogle(userPreferenceViewModel, result)
        },
        onAuthError = {
        }
    )
    val token = stringResource(R.string.default_web_client_id)

    var isLoading by remember { mutableStateOf(false) }

        NavHost(
            navController = navController,
            startDestination = AuthScreen.Register.route,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            composable(AuthScreen.Register.route) {
                RegisterScreen(
                    navigateToLogin = { navController.navigate(AuthScreen.Login.route) },
                    registerCustom = {
                        isLoading = true
                    },
                    registerGoogle = {
                        isLoading = true
                        val gso =
                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(token)
                                .requestEmail()
                                .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        launcher.launch(googleSignInClient.signInIntent)
                        isLoading = false
                    }
                )
                if (isLoading) {
                    LoadingScreen()
                }
            }
            composable(AuthScreen.Login.route) {
                LoginScreen(
                    navigateToRegister = { navController.navigate(AuthScreen.Register.route) },
                    loginCustom = {
                        isLoading = true
                    },
                    loginGoogle = {
                        isLoading = true
                        val gso =
                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(token)
                                .requestEmail()
                                .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        launcher.launch(googleSignInClient.signInIntent)
                        isLoading = false
                    }
                )
                if (isLoading) {
                    LoadingScreen()
                }
            }
        }
//    } else {
//        context.findActivity()?.finish()
//        context.startActivity(
//            Intent(
//                context,
//                MainActivity::class.java
//            )
//        )
//    }

}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}


private fun saveUserByGoogle(
    userPreferenceViewModel: UserPreferenceViewModel,
    result: AuthResult
){
    if (result.additionalUserInfo?.username == null){
        userPreferenceViewModel.saveToken(
            token = result.user?.uid.toString(),
            username = result.user?.displayName.toString(),
            email = result.user?.email.toString(),
            photo = result.user?.photoUrl.toString(),
            session = true
        )
    } else {
        userPreferenceViewModel.saveToken(
            token = result.user?.uid.toString(),
            username = result.additionalUserInfo?.username.toString(),
            email = result.user?.email.toString(),
            photo = result.user?.photoUrl.toString(),
            session = true
        )
    }
}