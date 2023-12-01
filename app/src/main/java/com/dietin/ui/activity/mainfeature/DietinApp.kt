package com.dietin.ui.activity.mainfeature

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dietin.ui.navigation.DietinScreen
import com.dietin.ui.screen.profile.ProfileScreen
import com.dietin.ui.screen.detail.DetailScreen
import com.dietin.ui.screen.home.HomeScreen
import com.dietin.ui.screen.scan.ScanScreen
import com.dietin.ui.theme.DietInTheme
import com.dietin.utils.BottomBar

@Composable
fun DietinApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != DietinScreen.Detail.route
                && currentRoute != DietinScreen.History.route
                && currentRoute != DietinScreen.Scan.route
            ) {
                    BottomBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DietinScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(DietinScreen.Home.route) {
                HomeScreen()
            }
            composable(DietinScreen.Profile.route) {
                ProfileScreen()
            }
            composable(DietinScreen.Scan.route) {
                ScanScreen(
                    navigateToDetail = {
                        navController.navigate(DietinScreen.Detail.createRoute(it))
                    }
                )
            }
            composable(
                route = DietinScreen.Detail.route,
                arguments = listOf(navArgument("scanId") { type = NavType.IntType }),
            ) {
                val id = it.arguments?.getInt("scanId") ?: 0
                DetailScreen(
                    modifier,
                    scanId = id,
                    navigateBack = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DietinAppPreview() {
    DietInTheme {
        DietinApp()
    }
}