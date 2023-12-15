package com.dietinapp.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dietinapp.R

@Composable
fun IngredientCard(
    modifier: Modifier,
    ingredientName: String,
    status: String
) {
    val color =
        if (status == stringResource(id = R.string.free_lectine)) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.tertiary

    Card(
        backgroundColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(width = 2.dp, color = color),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {

            }
    ) {
        Text(
            text = ingredientName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 16.dp)
        )
    }
}
