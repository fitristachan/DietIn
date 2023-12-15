package com.dietinapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.dietinapp.retrofit.data.viewmodel.HistoryPagingViewModel
import com.dietinapp.retrofit.data.viewmodel.HistoryPagingViewModelFactory
import com.dietinapp.retrofit.data.viewmodel.HistoryViewModel
import com.dietinapp.retrofit.data.viewmodel.HistoryViewModelFactory
import com.dietinapp.ui.navigation.DietinScreen
import com.dietinapp.ui.screen.detail.DetailScreen
import com.dietinapp.ui.screen.home.HomeScreen
import com.dietinapp.ui.screen.profile.ProfileScreen
import com.dietinapp.ui.screen.scan.ScanScreen
import com.dietinapp.ui.theme.DietInTheme
import com.dietinapp.ui.component.BottomBar
import com.dietinapp.ui.component.TopBar
import com.dietinapp.ui.screen.detail.ArticleScreen
import com.dietinapp.ui.screen.history.HistoryScreen
import com.dietinapp.ui.component.ErrorDialog
import com.dietinapp.ui.screen.profile.ChangePasswordScreen
import com.dietinapp.ui.screen.profile.ChangeUsernameScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = UserPreference.getInstance(application.dataStore)
        val userPreferenceViewModel =
            ViewModelProvider(
                this,
                UserPreferenceViewModelFactory(pref)
            )[UserPreferenceViewModel::class.java]


        setContent {
            DietInTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DietinApp(
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
    authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory.getInstance(LocalContext.current)
    ),
    historyViewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModelFactory.getInstance(LocalContext.current)
    ),
    historyPagingViewModel: HistoryPagingViewModel = viewModel(
        factory = HistoryPagingViewModelFactory.getInstance(LocalContext.current)
    ),
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current

    var sessionEndMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val expirationTime = authViewModel.expirationTokenTimeStamp.collectAsState()
    val isExpired = authViewModel.isFirebaseTokenExpired(expirationTime.value)

    LaunchedEffect(isExpired, Unit, authViewModel) {
        authViewModel.tokenValidationCheck(
            userPreferenceViewModel = userPreferenceViewModel,
            onSignOutComplete = {
                historyPagingViewModel.deleteHistories()
                sessionEndMessage = "Sesi Anda telah berakhir. Silakan login kembali."
                context.findActivity()?.finish()
                context.startActivity(
                    Intent(
                        context,
                        AuthActivity::class.java
                    )
                )
            },
            onError = {
                errorMessage = it
            }
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

    var showDialog by remember { mutableStateOf(false) }
    if (sessionEndMessage.isNotEmpty()) {
        showDialog = true
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (currentRoute != DietinScreen.Detail.route
                && currentRoute != DietinScreen.History.route
                && currentRoute != DietinScreen.Scan.route
                && currentRoute != DietinScreen.Article.route
                && currentRoute != DietinScreen.ChangeUsername.route
                && currentRoute != DietinScreen.ChangePassword.route

            ) {
                BottomBar(navController)
            }
        },
        topBar = {
            if (currentRoute != DietinScreen.Home.route
                && currentRoute != DietinScreen.Profile.route
                && currentRoute != DietinScreen.Scan.route
                && currentRoute != DietinScreen.Detail.route
            ) {
                var text = ""
                when (currentRoute) {
                    DietinScreen.History.route -> {
                        text = stringResource(R.string.history)
                    }
                    DietinScreen.Article.route -> {
                        text = stringResource(R.string.article_read)
                    }
                    DietinScreen.ChangeUsername.route -> {
                        text = stringResource(R.string.change_username)
                    }
                    DietinScreen.ChangePassword.route -> {
                        text = stringResource(R.string.change_password)
                    }
                }
                TopBar(
                    navigateBack = { navController.navigateUp() },
                    title = text
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = DietinScreen.Home.route,
        ) {
            composable(DietinScreen.Home.route) {
                HomeScreen(
                    username = username,
                    userPreferenceViewModel = userPreferenceViewModel,
                    navigateToHistory = {
                        navController.navigate(DietinScreen.History.route)
                    },
                    navigateToDetail = {
                        val route = DietinScreen.Detail.createRoute(it)
                        navController.navigate(route)
                    },
                    navigateToArticle = {
                        val route = DietinScreen.Article.createRoute(it)
                        navController.navigate(route)
                    }
                )
                ErrorDialog(
                    showDialog = showDialog,
                    errorMsg = sessionEndMessage,
                    onDismiss =
                    {
                        sessionEndMessage = ""
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
                    authViewModel = authViewModel,
                    userPreferenceViewModel = userPreferenceViewModel,
                    navigateToHistory = {
                        navController.navigate(DietinScreen.History.route)
                    },
                    navigateToChangeUsername = {
                        navController.navigate(DietinScreen.ChangeUsername.route)
                    },
                    navigateToChangePassword = {
                        navController.navigate(DietinScreen.ChangePassword.route)
                    }
                )
                ErrorDialog(
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
                        val route = DietinScreen.Detail.createRoute(it)
                        navController.navigate(route)
                    }
                )
            }
            composable(DietinScreen.History.route) {
                HistoryScreen(
                    modifier = Modifier,
                    navigateToDetail = {
                        val route = DietinScreen.Detail.createRoute(it)
                        navController.navigate(route)
                    },
                )
            }
            composable(
                route = DietinScreen.Detail.route,
                arguments = listOf(
                    navArgument("historyId") { type = NavType.StringType }
                )
            ) {
                val historyId =
                    it.arguments?.getString("historyId") ?: ""
                DetailScreen(
                    modifier,
                    historyId = historyId,
                    historyViewModel = historyViewModel,
                    navigateBack = {
                        navController.navigateUp()
                    },
                    errorMessage = errorMessage,
                )
            }
            composable(
                route = DietinScreen.Article.route,
                arguments = listOf(
                    navArgument("articleId") { type = NavType.IntType }
                )
            ) {
                val articleId = it.arguments?.getInt("articleId")
                ArticleScreen(
                    articleId = articleId!!,
                )
            }

            composable(DietinScreen.ChangePassword.route)
             {
                ChangePasswordScreen(
                    authViewModel = authViewModel,
                    oldEmail = email,
                    navigateBack = {
                        navController.navigateUp()
                    }
                )
            }
            composable(
                route = DietinScreen.ChangeUsername.route) {
                ChangeUsernameScreen(
                    oldUsername = username,
                    authViewModel = authViewModel,
                    onClick = {
                        val newUsername = authViewModel.username.value
                        authViewModel.updateUsername(
                            onUpdateComplete = {
                                userPreferenceViewModel.saveUsername(newUsername.toString())
                                Toast.makeText(context,
                                    R.string.username_update_successfully, Toast.LENGTH_SHORT).show()
                                navController.navigateUp() },
                            onUpdateError = {
                                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                            }
                        )
                    })

            }
        }
    }
}