package com.dietinapp.ui.screen.history

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.dietinapp.ui.component.ScanCard

@Composable
fun HistoryScreen(
    modifier: Modifier
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        userScrollEnabled = true
    ){
        items(count = 10){
            ScanCard(
                modifier = Modifier,
                foodName = it.toString(),
                onClick = {})
        }
    }
}