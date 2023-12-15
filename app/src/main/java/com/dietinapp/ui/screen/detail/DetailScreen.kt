package com.dietinapp.ui.screen.detail

import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.dietinapp.R
import com.dietinapp.ui.component.IngredientCard
import com.dietinapp.model.readRecipesFromJson
import com.dietinapp.retrofit.data.viewmodel.HistoryViewModel
import com.dietinapp.retrofit.response.IngredientsItem
import com.dietinapp.ui.component.ConfirmationDialog
import com.dietinapp.ui.component.LoadingScreen
import com.dietinapp.ui.component.ErrorDialog
import com.dietinapp.utils.imageFileInGallery
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun DetailScreen(
    modifier: Modifier,
    scanId: Int,
    historyId: String,
    errorMessage: String,
    historyViewModel: HistoryViewModel,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var errorNetworkMessage by remember { mutableStateOf("") }

    var foodName by remember { mutableStateOf("") }
    var foodStatus by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf<List<IngredientsItem>>(emptyList()) }
    var foodPhoto by remember { mutableStateOf<Uri?>(null) }
    val color =
        if (foodStatus == stringResource(R.string.free_lectine)) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.tertiary

    var isLoading by remember { mutableStateOf(false) }


    if (historyId == stringResource(R.string.local)) {
        val recipes = remember { readRecipesFromJson(context) }
        val label = recipes[scanId]
        foodName = label.name
        foodStatus =
            if (!label.status) stringResource(R.string.free_lectine) else stringResource(R.string.contain_lectine)
        ingredients = label.ingredients
        foodPhoto = imageFileInGallery.value.toUri()
    } else if (historyId != stringResource(R.string.local)) {
        isLoading = historyViewModel.isLoading.collectAsState().value

        var statusBoolean by remember { mutableStateOf(false) }
        historyViewModel.getDetailHistory(historyId).observe(lifecycleOwner) { history ->
            foodName = history.data.foodName
            statusBoolean = history.data.lectineStatus
            foodPhoto = history.data.foodPhoto.toUri()

            val gson = Gson()
            val ingredientsString = history.data.ingredients
            try {
                val listType = object : TypeToken<List<IngredientsItem>>() {}.type
                val ingredientsList: List<IngredientsItem> = gson.fromJson(ingredientsString, listType)
                ingredients = ingredientsList
                isLoading = false
            } catch (e: Exception) {
                e.printStackTrace()
                isLoading = false
            }
        }
        foodStatus =
            if (!statusBoolean) stringResource(R.string.free_lectine) else stringResource(R.string.contain_lectine)
    }

    var showDialog by remember { mutableStateOf(false) }
    if (errorMessage.isNotEmpty()) {
        errorNetworkMessage = errorMessage
        if (errorNetworkMessage.isNotEmpty()){
            showDialog = true
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            //back button
            IconButton(
                onClick = { navigateBack() },
                modifier = Modifier.size(35.dp)
            ){
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button))
            }
            if (historyId != stringResource(R.string.local)) {
                IconButton(
                    onClick = { showDeleteDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(20))
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = stringResource(R.string.delete_history_button),
                        tint = Color.White
                    )
                }
            }
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
            model = foodPhoto,
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
                text = stringResource(R.string.ingredients),
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
                        status = if (!ingredient.ingredientLectineStatus) stringResource(R.string.free_lectine) else stringResource(
                            R.string.contain_lectine
                        )
                    )
                }
            }
        }
    }
    if (isLoading){
        LoadingScreen()
    }
    ErrorDialog(
        showDialog = showDialog,
        errorMsg = errorNetworkMessage,
        onDismiss =
        {
            errorNetworkMessage = ""
            showDialog = false
            navigateBack()
        }
    )
    ConfirmationDialog(
        showDialog = showDeleteDialog,
        title = stringResource(R.string.delete_confirmation_title),
        text = stringResource(R.string.delete_confirmation_text, foodName),
        onConfirm = {
            historyViewModel.deleteHistory(historyId)
            navigateBack()
            showDeleteDialog = false
        },
        onCancel = {
            showDeleteDialog = false
        }
    )
}