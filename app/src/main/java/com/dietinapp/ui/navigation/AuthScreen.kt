package com.dietinapp.ui.navigation

sealed class AuthScreen (val route: String) {
    object Register : AuthScreen("register")
    object Login : AuthScreen("register/login")

}
