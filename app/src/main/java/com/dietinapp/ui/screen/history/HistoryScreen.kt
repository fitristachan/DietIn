package com.dietinapp.ui.screen.history

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HistoryScreen(
    modifier: Modifier
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        userScrollEnabled = true
    ){
        items(count = 10){

        }
    }
}