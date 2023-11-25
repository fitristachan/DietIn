package com.dietin.ui.navigation

sealed class DietinScreen (val route: String) {

    object Home : DietinScreen("home")
    object Scan : DietinScreen("ic_scan")
    object Profile : DietinScreen("profile")
    object History: DietinScreen("history")
    object DetailFromHome : DietinScreen("home/{scanId}") {
        fun createRoute(scanId: Long) = "home/$scanId"
    }
    object DetailFromHistory : DietinScreen("history/{scanId}") {
        fun createRoute(scanId: Long) = "history/$scanId"
    }

}
