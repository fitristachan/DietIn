package com.dietinapp.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.dietinapp.database.datastore.UserPreference
import com.dietinapp.database.datastore.UserPreferenceViewModel
import com.dietinapp.database.datastore.UserPreferenceViewModelFactory
import com.dietinapp.database.datastore.dataStore
import com.dietinapp.firebase.AuthViewModel
import com.dietinapp.firebase.AuthViewModelFactory
import com.dietinapp.ui.navigation.AuthScreen
import com.dietinapp.ui.screen.login.LoginScreen
import com.dietinapp.ui.screen.register.RegisterByGoogleScreen
import com.dietinapp.ui.screen.register.RegisterScreen
import com.dietinapp.ui.theme.DietInTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlin.coroutines.CoroutineContext

class AuthActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private val authViewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        val pref = UserPreference.getInstance(application.dataStore)
        val userPreferenceViewModel =
            ViewModelProvider(
                this,
                UserPreferenceViewModelFactory(pref)
            )[UserPreferenceViewModel::class.java]

        authViewModel.tokenValidationCheck(userPreferenceViewModel, auth)

        userPreferenceViewModel.getSession().observe(this) { session: Boolean? ->
            if (session == true && auth.currentUser != null) {
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
                            Auth(
                                userPreferenceViewModel = userPreferenceViewModel,
                                authViewModel = authViewModel
                            )
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
    authViewModel: AuthViewModel,
    userPreferenceViewModel: UserPreferenceViewModel,
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current
    val coroutineContext = rememberCoroutineScope().coroutineContext
    val token = stringResource(R.string.default_web_client_id)
    val email = remember { mutableStateOf("") }

    val launcher = rememberFirebaseAuthLauncher(
        context = coroutineContext,
        navController = navController,
        authViewModel = authViewModel,
        userPreferenceViewModel = userPreferenceViewModel
    )

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
                authViewModel = authViewModel,
                onClick = {
                    authViewModel.registerByCustom(navController)
                },
                registerGoogle = {
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(token)
                            .requestEmail()
                            .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    launcher.launch(googleSignInClient.signInIntent)
                }
            )
        }

        composable(
            route = AuthScreen.RegisterByGoogle.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType }),
        ) {
            email.value = it.arguments?.getString("email") ?: ""
            RegisterByGoogleScreen(
                email = email.value,
                authViewModel = authViewModel,
                onClick = {
                    authViewModel.registerByGoogle(
                        navController,
                        email.value
                    )
                },
            )
        }

        composable(AuthScreen.Login.route) {
            LoginScreen(
                navigateToRegister = { navController.navigate(AuthScreen.Register.route) },
                authViewModel = authViewModel,
                onClick = {
                    authViewModel.loginByCustom(userPreferenceViewModel)
                },
                loginGoogle = {
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(token)
                            .requestEmail()
                            .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    launcher.launch(googleSignInClient.signInIntent)
                }
            )
        }
    }
}

@Composable
fun rememberFirebaseAuthLauncher(
    context: CoroutineContext,
    navController: NavController,
    authViewModel: AuthViewModel,
    userPreferenceViewModel: UserPreferenceViewModel
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            authViewModel.GoogleLauncher(
                result = result,
                context = context,
                navController = navController,
                userPreferenceViewModel = userPreferenceViewModel
            )
        }

    return launcher
}