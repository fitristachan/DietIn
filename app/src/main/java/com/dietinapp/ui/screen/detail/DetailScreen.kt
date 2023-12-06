package com.dietinapp.ui.screen.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dietinapp.R
import com.dietinapp.utils.readRecipesFromJson

@Composable
fun DetailScreen(
    modifier: Modifier,
    scanId: Int,
    navigateBack: () -> Unit
){
    val context = LocalContext.current
    val recipes = remember { readRecipesFromJson(context) }
    val label = recipes[scanId]

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.detail_background),
                contentDescription = label.name,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(text = label.name)
                Text(text = scanId.toString())
            }
        }
}