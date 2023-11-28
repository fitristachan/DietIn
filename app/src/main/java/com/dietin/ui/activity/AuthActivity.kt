package com.dietin.ui.activity

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dietin.ui.activity.mainfeature.MainActivity
import com.dietin.ui.navigation.AuthScreen
import com.dietin.ui.screen.login.LoginScreen
import com.dietin.ui.screen.register.RegisterScreen
import com.dietin.ui.theme.DietInTheme

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DietInTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Auth()
                }
            }
        }
    }
}

@Composable
fun Auth(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    ) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = AuthScreen.Register.route,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        composable(AuthScreen.Register.route) {
            RegisterScreen(
                navigateToLogin = { navController.navigate(AuthScreen.Login.route) })
        }
        composable(AuthScreen.Login.route) {
            LoginScreen(
                navigateToRegister = { navController.navigate(AuthScreen.Register.route) },
                navigateToHome = {
                    //isloginpreferences
                    context.findActivity()?.finish()
                    context.startActivity(
                        Intent(
                            context,
                            MainActivity::class.java
                        )
                    )
                })
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    DietInTheme {
        Auth()
    }
}