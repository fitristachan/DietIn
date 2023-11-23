package com.dietin.ui.navigation

sealed class OnBoardingScreen (val route: String) {
    object FirstOnBoarding : OnBoardingScreen("onboarding")
    object SecondOnBoarding : OnBoardingScreen("onboarding/second")

}
