package com.dietinapp.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val dietinScreen: DietinScreen
)
