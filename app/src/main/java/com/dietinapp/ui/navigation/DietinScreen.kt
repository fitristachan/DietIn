package com.dietinapp.ui.navigation

sealed class DietinScreen (val route: String) {

    object Home : DietinScreen("home")
    object Scan : DietinScreen("scan")


    object Profile : DietinScreen("profile")
    object ChangeEmail : DietinScreen("{email}"){
        fun createRoute(email: String): String = email
    }
    object ChangeUsername : DietinScreen("username")
    object ChangePassword : DietinScreen("password")

    object History: DietinScreen("history")
    object Detail : DietinScreen("home/{scanId}{historyId}") {
        fun createRoute(scanId: Int, historyId: String): String = "home/$scanId$historyId"
    }
    object Article : DietinScreen("home/{articleId}") {
        fun createRoute(articleId: Int): String = "home/$articleId"
    }

}
