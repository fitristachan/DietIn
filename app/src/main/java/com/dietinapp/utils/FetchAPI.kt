package com.dietinapp.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.dietinapp.model.Ingredient
import com.dietinapp.model.processImage
import com.dietinapp.model.readRecipesFromJson
import com.dietinapp.retrofit.data.viewmodel.HistoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


val imageFileInGallery = mutableStateOf("")

fun processAndFetch(
    imageUri: Uri,
    context: Context,
    historyViewModel: HistoryViewModel,
    onProcessAdditional: () -> Unit,
    navigateToDetail: (Int) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        processImage(context, imageUri) { result ->
            val recipes = readRecipesFromJson(context)
            val foodName = recipes[result].name
            val lectineStatus = recipes[result].status

            val ingredients: List<Ingredient> = recipes[result].ingredients

            val foodPhoto = uriToFile(imageUri, context).reduceFileImage()
            imageFileInGallery.value = imageUri.toString()

            Log.d("Image File", "showImage: ${foodPhoto.path}")

            CoroutineScope(Dispatchers.Main).launch {
                historyViewModel.addHistory(
                    foodPhoto = foodPhoto,
                    foodName = foodName,
                    lectineStatus = lectineStatus,
                    ingredients = ingredients
                )

                onProcessAdditional()
                navigateToDetail(result)
            }
        }
    }
}

fun processAndFetchCamera(
    imageUri: Uri,
    context: Context,
    historyViewModel: HistoryViewModel,
    onProcessAdditional: () -> Unit,
    navigateToDetail: (Int) -> Unit,
) {
    processImage(context, imageUri) { result ->
        val recipes = readRecipesFromJson(context)
        val foodName = recipes[result].name
        val lectineStatus = recipes[result].status

        val ingredients: List<Ingredient> = recipes[result].ingredients

        saveToGallery(context, imageUri) { file ->
            val foodPhoto = file!!.reduceFileImage()
            imageFileInGallery.value = file.toUri().toString()

            historyViewModel.addHistory(
                foodPhoto = foodPhoto,
                foodName = foodName,
                lectineStatus = lectineStatus,
                ingredients = ingredients
            )
        }

        onProcessAdditional()
        navigateToDetail(result)
    }
}