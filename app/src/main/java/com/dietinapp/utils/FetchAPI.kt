package com.dietinapp.utils

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import com.dietinapp.model.processImage
import com.dietinapp.model.readRecipesFromJson
import com.dietinapp.retrofit.data.viewmodel.HistoryViewModel
import com.dietinapp.retrofit.response.IngredientsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


val imageFileInGallery = mutableStateOf("")

fun processAndFetch(
    imageUri: Uri,
    context: Context,
    historyViewModel: HistoryViewModel,
    navigateToDetail: (Int) -> Unit,
    onWait: () -> Unit,
) {
    CoroutineScope(Dispatchers.IO).launch {
        val result = suspendCoroutine<Int?> { continuation ->
            processImage(context, imageUri) { processedResult ->
                continuation.resume(processedResult)
            }
        }

        if (result != null) {
            val recipes = readRecipesFromJson(context)
            val foodName = recipes[result].name
            val lectineStatus = recipes[result].status
            val ingredients: List<IngredientsItem> = recipes[result].ingredients

            val foodPhoto = uriToFile(imageUri, context).reduceFileImage()
            imageFileInGallery.value = imageUri.toString()

            historyViewModel.addHistory(
                foodPhoto = foodPhoto,
                foodName = foodName,
                lectineStatus = lectineStatus,
                ingredients = ingredients
            )

            withContext(Dispatchers.Main) {
                if (result in 0..15) {
                    navigateToDetail(result)
                }
            }
        } else {
            onWait()
        }
    }
}


fun processAndFetchCamera(
    imageUri: Uri,
    context: Context,
    historyViewModel: HistoryViewModel,
    navigateToDetail: (Int) -> Unit,
    onWait: () -> Unit,
) {
    CoroutineScope(Dispatchers.IO).launch {
        val result = suspendCoroutine<Int?> { continuation ->
            processImage(context, imageUri) { processedResult ->
                continuation.resume(processedResult)
            }
        }

        if (result != null) {
            val recipes = readRecipesFromJson(context)
            val foodName = recipes[result].name
            val lectineStatus = recipes[result].status
            val ingredients: List<IngredientsItem> = recipes[result].ingredients

            saveToGallery(context, imageUri) { file ->
                val foodPhoto = uriToFile(imageUri, context).reduceFileImage()
                imageFileInGallery.value = file!!.toUri().toString()

                historyViewModel.addHistory(
                    foodPhoto = foodPhoto,
                    foodName = foodName,
                    lectineStatus = lectineStatus,
                    ingredients = ingredients
                )
            }

            withContext(Dispatchers.Main) {
                if (result in 0..15) {
                    navigateToDetail(result)
                }
            }
        } else {
            onWait()
        }
    }
}