package com.dietin.ui.navigation

sealed class DietinScreen (val route: String) {

    object Home : DietinScreen("home")
    object Scan : DietinScreen("ic_scan")
    object Profile : DietinScreen("profile")
    object History: DietinScreen("history")
    object Detail : DietinScreen("home/{scanId}") {
        fun createRoute(scanId: Int) = "home/$scanId"
    }

}
