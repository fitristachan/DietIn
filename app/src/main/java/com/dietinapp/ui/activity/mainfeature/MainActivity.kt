package com.dietinapp.ui.activity.mainfeature

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dietinapp.data.datastore.UserPreference
import com.dietinapp.data.datastore.UserPreferenceViewModel
import com.dietinapp.data.datastore.UserPreferenceViewModelFactory
import com.dietinapp.data.datastore.dataStore
import com.dietinapp.ui.activity.AuthActivity
import com.dietinapp.ui.navigation.DietinScreen
import com.dietinapp.ui.screen.detail.DetailScreen
import com.dietinapp.ui.screen.home.HomeScreen
import com.dietinapp.ui.screen.profile.ProfileScreen
import com.dietinapp.ui.screen.scan.ScanScreen
import com.dietinapp.ui.theme.DietInTheme
import com.dietinapp.ui.component.BottomBar
import com.dietinapp.ui.component.LoadingScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
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

        setContent {
            DietInTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DietinApp(auth = auth, userPreferenceViewModel = userPreferenceViewModel)
                }
            }
        }
    }
}

@Composable
fun DietinApp(
    auth: FirebaseAuth,
    modifier: Modifier = Modifier,
    userPreferenceViewModel: UserPreferenceViewModel,
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current

    var username by remember {mutableStateOf("")}
    userPreferenceViewModel.getUsername().observe(lifecycleOwner){
        username = it.toString()
    }

    var email by remember {mutableStateOf("")}
    userPreferenceViewModel.getEmail().observe(lifecycleOwner){
        email = it.toString()
    }

    var photo by remember {mutableStateOf("")}
    userPreferenceViewModel.getPhoto().observe(lifecycleOwner){
        photo = it.toString()
    }

    var isLoading by remember { mutableStateOf(false) }

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
            navController = navController,
            startDestination = DietinScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(DietinScreen.Home.route) {
                HomeScreen(
                    username = username,
                    photo = photo
                )
            }
            composable(DietinScreen.Profile.route) {
                ProfileScreen(
                    username = username,
                    email = email,
                    photo = photo,
                    logOut = {
                        isLoading = true
                        auth.signOut()
                        if (auth.currentUser == null) {
                            userPreferenceViewModel.deleteAll()
                            context.findActivity()?.finish()
                            context.startActivity(
                                Intent(
                                    context,
                                    AuthActivity::class.java
                                )
                            )
                        } else {
                            auth.signOut()
                        }
                    }
                )
                if (isLoading) {
                    LoadingScreen()
                }
            }
            composable(DietinScreen.Scan.route) {
                ScanScreen(
                    navigateToDetail = {
                        navController.navigate(DietinScreen.Detail.createRoute(it))
                    }
                )
            }
            composable(
                route = DietinScreen.Detail.route,
                arguments = listOf(navArgument("scanId") { type = NavType.IntType }),
            ) {
                val id = it.arguments?.getInt("scanId") ?: 0
                DetailScreen(
                    modifier,
                    scanId = id,
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