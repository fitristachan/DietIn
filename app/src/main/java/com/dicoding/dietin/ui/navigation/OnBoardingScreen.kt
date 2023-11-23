package com.dicoding.dietin.ui.navigation

sealed class OnBoardingScreen (val route: String) {
    object FirstOnBoarding : OnBoardingScreen("firstBoarding")
    object SecondOnBoarding : OnBoardingScreen("secondBoarding")

}
