package com.dietinapp.ui.screen.detail

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.dietinapp.R
import com.dietinapp.model.Ingredient
import com.dietinapp.ui.component.IngredientCard
import com.dietinapp.model.readRecipesFromJson
import com.dietinapp.utils.imageFileInGallery

@Composable
fun DetailScreen(
    modifier: Modifier,
    scanId: Int,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current

    var foodName by remember { mutableStateOf("") }
    var foodStatus by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf<List<Ingredient>>(emptyList()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val color =
        if (foodStatus == "Rendah Lektin") MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.tertiary

    val recipes = remember { readRecipesFromJson(context) }
    val label = recipes[scanId]
    if (label.name.isNotEmpty()){
        foodName = label.name
        foodStatus = if (!label.status) "Rendah Lektin" else "Tinggi Lektin"
        ingredients = label.ingredients
        imageUri = imageFileInGallery.value.toUri()

    } else {
        TODO()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .paint(
                painter = painterResource(id = R.drawable.detail_background),
                sizeToIntrinsics = true,
                contentScale = ContentScale.FillWidth
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 56.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            //back button
            Image(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = foodName,
                modifier = Modifier
                    .size(35.dp)
                    .clickable {
                        navigateBack()
                    }
            )
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 56.dp, bottom = 16.dp)
            .padding(horizontal = 16.dp),
    ) {
        AsyncImage(
            model = imageUri ,
            contentDescription = foodName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(170.dp)
                .clip(shape = CircleShape)
        )

        Spacer(modifier = Modifier.heightIn(min = 16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = foodName,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.heightIn(min = 16.dp))


        Row(
            modifier = Modifier
                .width(265.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(
                        color = color,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .wrapContentSize()
            ) {
                Text(
                    text = foodStatus,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.heightIn(min = 32.dp))

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),

        ) {

            Text(
                text = "Daftar Bahan",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.heightIn(min = 8.dp))


            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                items(ingredients, key = { it.ingredientName }) { ingredient ->
                    IngredientCard(
                        modifier = modifier,
                        ingredientName = ingredient.ingredientName,
                        status = if (!ingredient.ingredientLectineStatus) "Rendah Lektin" else "Tinggi Lektin"
                    )
                }
            }
        }
    }
}