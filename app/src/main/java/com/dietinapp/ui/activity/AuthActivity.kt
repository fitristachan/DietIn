package com.dietinapp.ui.activity

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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dietinapp.R
import com.dietinapp.data.datastore.UserPreference
import com.dietinapp.data.datastore.UserPreferenceViewModel
import com.dietinapp.data.datastore.UserPreferenceViewModelFactory
import com.dietinapp.data.datastore.dataStore
import com.dietinapp.firebase.rememberFirebaseAuthLauncher
import com.dietinapp.ui.activity.mainfeature.MainActivity
import com.dietinapp.ui.navigation.AuthScreen
import com.dietinapp.ui.screen.login.LoginScreen
import com.dietinapp.ui.screen.register.RegisterByGoogleScreen
import com.dietinapp.ui.screen.register.RegisterScreen
import com.dietinapp.ui.theme.DietInTheme
import com.dietinapp.ui.component.LoadingScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AuthActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        val pref = UserPreference.getInstance(application.dataStore)
        val userPreferenceViewModel =
            ViewModelProvider(
                this,
                UserPreferenceViewModelFactory(pref)
            )[UserPreferenceViewModel::class.java]

        userPreferenceViewModel.getSession().observe(this) { session: Boolean? ->
            if (session == true && auth.currentUser != null) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else if (session == false && auth.currentUser != null ){
                auth.signOut()
            } else {
                setContent {
                    DietInTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Auth(auth = auth, userPreferenceViewModel = userPreferenceViewModel)
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
    auth: FirebaseAuth,
    userPreferenceViewModel: UserPreferenceViewModel,
    navController: NavHostController = rememberNavController(),
) {
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->
            saveUserByGoogle(navController, userPreferenceViewModel, result)
            isLoading = false
        },
        onAuthError = {
            isLoading = false
        }
    )
    val token = stringResource(R.string.default_web_client_id)

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
                auth = auth,
                onAuthComplete = { navController.navigate(AuthScreen.Login.route) },
                registerGoogle = {
                    isLoading = true
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(token)
                            .requestEmail()
                            .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    launcher.launch(googleSignInClient.signInIntent)
                }
            )
            if (isLoading) {
                LoadingScreen()
            }
        }

        composable(
            route = AuthScreen.RegisterByGoogle.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType }),
        ) {
            val email = it.arguments?.getString("email") ?: ""
            RegisterByGoogleScreen(
                email = email,
                auth = auth,
                onAuthComplete = {
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(token)
                            .requestEmail()
                            .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    launcher.launch(googleSignInClient.signInIntent)
                },
            )
        }

        composable(AuthScreen.Login.route) {
            LoginScreen(
                navigateToRegister = { navController.navigate(AuthScreen.Register.route) },
                auth = auth,
                onAuthComplete = { result ->
                    saveUserCustom(userPreferenceViewModel, result)
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
                }
            )
            if (isLoading) {
                LoadingScreen()
            }
        }
    }
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
        userPreferenceViewModel.saveToken(
            token = result.user?.uid.toString(),
            username = result.user?.displayName.toString(),
            email = result.user?.email.toString(),
            photo = result.user?.photoUrl.toString(),
            session = true
        )
    }
}

private fun saveUserCustom(
    userPreferenceViewModel: UserPreferenceViewModel,
    result: AuthResult
) {
    userPreferenceViewModel.saveToken(
        token = result.user?.uid.toString(),
        username = result.user?.displayName.toString(),
        email = result.user?.email.toString(),
        photo = result.user?.photoUrl.toString(),
        session = true
    )

}