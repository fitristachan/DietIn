package com.dicoding.dietin.ui.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dicoding.dietin.R
import com.dicoding.dietin.ui.navigation.NavigationItem
import com.dicoding.dietin.ui.navigation.DietinScreen
import com.dicoding.dietin.ui.screen.profile.ProfileScreen
import com.dicoding.dietin.ui.screen.home.HomeScreen
import com.dicoding.dietin.ui.theme.DietInTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietinApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != DietinScreen.DetailFromHome.route
                && currentRoute != DietinScreen.DetailFromHistory.route
                && currentRoute != DietinScreen.History.route
                && currentRoute != DietinScreen.Scan.route
            ) {
                BottomAppBar(
                    modifier = Modifier
                        .height(65.dp)
                        .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)),
                    containerColor = MaterialTheme.colorScheme.secondary,
                    tonalElevation = 22.dp
                ) {
                    BottomBar(navController)
                }
            }
        }, floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            if (currentRoute != DietinScreen.DetailFromHome.route
                && currentRoute != DietinScreen.DetailFromHistory.route
                && currentRoute != DietinScreen.History.route
                && currentRoute != DietinScreen.Scan.route
            ) {
                FloatingActionButton(
                    shape = CircleShape,
                    onClick = {
                        DietinScreen.Scan.route.let {
                            navController.navigate(it) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        DietinScreen.Scan.route.let { navController.navigate(it) }
                    }, contentColor = MaterialTheme.colorScheme.background
                ) {
                    val imageVector: Painter = painterResource(id = R.drawable.scan)

                    Image(
                        painter = imageVector, contentDescription = stringResource(R.string.menu_scan)
                    )
                }
            }
        }) { innerPadding ->
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
        }
    }
}

@Composable
private fun BottomBar(
    navController: NavHostController, modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val navigationItems = listOf(
            NavigationItem(
                title = stringResource(R.string.menu_home),
                icon = Icons.Default.Home,
                dietinScreen = DietinScreen.Home
            ),
            NavigationItem(
                title = stringResource(R.string.menu_profile),
                icon = Icons.Default.AccountCircle,
                dietinScreen = DietinScreen.Profile
            ),
        )
        navigationItems.forEachIndexed { index, item ->
            if (index != 1) {
                NavigationBarItem(icon = {
                    Icon(
                        imageVector = item.icon, contentDescription = item.title
                    )
                },
                    label = { Text(item.title) },
                    selected = currentRoute == item.dietinScreen.route,
                    onClick = {
                        navController.navigate(item.dietinScreen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    })
            } else {
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { },
                    label = { },
                    enabled = false
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