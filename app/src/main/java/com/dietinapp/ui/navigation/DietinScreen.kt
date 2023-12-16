package com.dietinapp.ui.navigation

sealed class DietinScreen (val route: String) {

    object Home : DietinScreen("home")
    object Scan : DietinScreen("scan")


    object Profile : DietinScreen("profile")

    object ChangeUsername : DietinScreen("username")
    object ChangePassword : DietinScreen("password")

    object History: DietinScreen("history")
    object Detail : DietinScreen("home/{historyId}") {
        fun createRoute(historyId: String): String = "home/${historyId}"
    }
    object Article : DietinScreen("article/{articleId}") {
        fun createRoute(articleId: Int): String = "article/$articleId"
    }

}
