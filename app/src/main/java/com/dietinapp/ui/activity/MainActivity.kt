package com.dietinapp.ui.activity

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dietinapp.R
import com.dietinapp.database.datastore.UserPreference
import com.dietinapp.database.datastore.UserPreferenceViewModel
import com.dietinapp.database.datastore.UserPreferenceViewModelFactory
import com.dietinapp.database.datastore.dataStore
import com.dietinapp.firebase.AuthViewModel
import com.dietinapp.firebase.AuthViewModelFactory
import com.dietinapp.retrofit.data.viewmodel.HistoryViewModel
import com.dietinapp.retrofit.data.viewmodel.HistoryViewModelFactory
import com.dietinapp.ui.navigation.DietinScreen
import com.dietinapp.ui.screen.detail.DetailScreen
import com.dietinapp.ui.screen.home.HomeScreen
import com.dietinapp.ui.screen.profile.ProfileScreen
import com.dietinapp.ui.screen.scan.ScanScreen
import com.dietinapp.ui.theme.DietInTheme
import com.dietinapp.ui.component.BottomBar
import com.dietinapp.ui.screen.history.HistoryScreen
import com.dietinapp.utils.errorDialog
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private val authViewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory.getInstance(application)
    }

    private val historyViewModel by viewModels<HistoryViewModel> {
        HistoryViewModelFactory.getInstance(application)
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


        setContent {
            DietInTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DietinApp(
                        historyViewModel = historyViewModel,
                        authViewModel = authViewModel,
                        userPreferenceViewModel = userPreferenceViewModel,
                    )
                }
            }
        }
    }
}

@Composable
fun DietinApp(
    modifier: Modifier = Modifier,
    userPreferenceViewModel: UserPreferenceViewModel,
    authViewModel: AuthViewModel,
    historyViewModel: HistoryViewModel,
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current

    var sessionEndMessage by remember { mutableStateOf("") }

    LaunchedEffect(authViewModel){
        authViewModel.tokenValidationCheck(
            userPreferenceViewModel = userPreferenceViewModel,
            onSignOutComplete = {sessionEndMessage = it}
        )
    }


    var username by remember { mutableStateOf("") }
    userPreferenceViewModel.getUsername().observe(lifecycleOwner) {
        username = it.toString()
    }

    var email by remember { mutableStateOf("") }
    userPreferenceViewModel.getEmail().observe(lifecycleOwner) {
        email = it.toString()
    }

    var photo by remember { mutableStateOf("") }
    userPreferenceViewModel.getPhoto().observe(lifecycleOwner) {
        photo = it.toString()
    }

    var showDialog by remember { mutableStateOf(false) }
    if (sessionEndMessage.isNotEmpty()){
        showDialog = true
    }

    Scaffold(
        bottomBar = {
            if (currentRoute != DietinScreen.Detail.route
                && currentRoute != DietinScreen.History.route
                && currentRoute != DietinScreen.Scan.route
            ) {
                BottomBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = DietinScreen.Home.route,
        ) {
            composable(DietinScreen.Home.route) {
                HomeScreen(
                    username = username,
                    photo = photo,
                    historyViewModel = historyViewModel,
                    navigateToHistory = {
                        navController.navigate(DietinScreen.History.route)
                    },
                    navigateToDetail = {
                        val route = DietinScreen.Detail.createRoute(scanId = 0, it)
                        navController.navigate(route)
                    }
                )
                errorDialog(
                    showDialog = showDialog,
                    errorMsg = sessionEndMessage,
                    onDismiss =
                    {
                        showDialog = false
                        context.findActivity()?.finish()
                        context.startActivity(
                            Intent(
                                context,
                                AuthActivity::class.java
                            )
                        )
                    }
                )
            }
            composable(DietinScreen.Profile.route) {
                ProfileScreen(
                    username = username,
                    email = email,
                    photo = photo,
                    authViewModel = authViewModel,
                    logOut = {
                        authViewModel.signOut(
                            userPreferenceViewModel,
                            onSignOutComplete = {
                                context.findActivity()?.finish()
                                context.startActivity(
                                    Intent(
                                        context,
                                        AuthActivity::class.java
                                    )
                                )
                            })
                    },
                    navigateToHistory = {
                        navController.navigate(DietinScreen.History.route)
                    }
                )
                errorDialog(
                    showDialog = showDialog,
                    errorMsg = sessionEndMessage,
                    onDismiss =
                    {
                        showDialog = false
                        context.findActivity()?.finish()
                        context.startActivity(
                            Intent(
                                context,
                                AuthActivity::class.java
                            )
                        )
                    }
                )
            }
            composable(DietinScreen.Scan.route) {
                ScanScreen(
                    historyViewModel = historyViewModel,
                    navigateToDetail = {
                        val route = DietinScreen.Detail.createRoute(it, "local")
                        navController.navigate(route)
                    }
                )
            }
            composable(DietinScreen.History.route) {
                HistoryScreen(
                    modifier = Modifier,
                    navigateToDetail = {
                        val route = DietinScreen.Detail.createRoute(scanId = 0, it)
                        navController.navigate(route)
                    },
                    navigateBack = {
                        navController.navigateUp()
                    }
                )
            }
            composable(
                route = DietinScreen.Detail.route,
                arguments = listOf(
                    navArgument("scanId") { type = NavType.IntType },
                    navArgument("historyId") { type = NavType.StringType }
                )
            ) {
                val scanId = it.arguments?.getInt("scanId") ?: 0
                val historyId = it.arguments?.getString("historyId") ?: stringResource(R.string.local)
                DetailScreen(
                    modifier,
                    scanId = scanId,
                    historyId = historyId,
                    historyViewModel = historyViewModel,
                    navigateBack = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}