package com.dietinapp.ui.navigation

sealed class AuthScreen (val route: String) {
    object Register : AuthScreen("register")
    object Login : AuthScreen("login")
    object RegisterByGoogle : AuthScreen("registerbygoogle/{email}") {
        fun createRoute(email: String) = "registerbygoogle/$email"
    }

}
