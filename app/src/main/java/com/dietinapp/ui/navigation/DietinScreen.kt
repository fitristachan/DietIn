package com.dietinapp.ui.navigation

sealed class DietinScreen (val route: String) {

    object Home : DietinScreen("home")
    object Scan : DietinScreen("ic_scan")
    object Profile : DietinScreen("profile")
    object History: DietinScreen("history")
    object Detail : DietinScreen("home/{scanId}{historyId}") {
        fun createRoute(scanId: Int, historyId: String): String = "home/$scanId$historyId"
    }
    object Article : DietinScreen("home/{articleId}") {
        fun createRoute(articleId: Int): String = "home/$articleId"
    }

}
