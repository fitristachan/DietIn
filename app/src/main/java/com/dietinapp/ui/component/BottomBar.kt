package com.dietinapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dietinapp.R
import com.dietinapp.ui.navigation.DietinScreen
import com.dietinapp.ui.navigation.NavigationItem

@Composable
fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Box {
        NavigationBar(
            modifier = modifier
                .padding(top = 30.dp)
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .fillMaxWidth()
                .shadow(2.dp)
                .border(1.dp, MaterialTheme.colorScheme.onSecondary, RectangleShape),
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary,
            tonalElevation = 2.dp
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
        IconButton(
            onClick = {
                DietinScreen.Scan.route.let {
                    navController.navigate(it) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }

                }
            },
            enabled = true,
            modifier = Modifier
                .padding(bottom = 40.dp)
                .clip(CircleShape)
                .size(70.dp)
                .background(MaterialTheme.colorScheme.secondary)
                .align(Alignment.BottomCenter)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_scan),
                contentDescription = stringResource(R.string.menu_scan),
                tint = MaterialTheme.colorScheme.background
            )
        }
    }
}
