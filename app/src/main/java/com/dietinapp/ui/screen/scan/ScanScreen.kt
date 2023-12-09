package com.dietinapp.ui.screen.scan

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import com.dietinapp.R
import com.dietinapp.model.Ingredient
import com.dietinapp.model.processImage
import com.dietinapp.retrofit.data.viewmodel.HistoryViewModel
import com.dietinapp.ui.component.CameraPreview
import com.dietinapp.ui.component.LoadingScreen
import com.dietinapp.utils.Permission
import com.dietinapp.utils.createCustomTempFile
import com.dietinapp.ui.component.executor
import com.dietinapp.ui.component.getCameraProvider
import com.dietinapp.utils.deleteTempFile
import com.dietinapp.utils.readRecipesFromJson
import com.dietinapp.utils.reduceFileImage
import com.dietinapp.utils.saveToGallery
import com.dietinapp.utils.uriToFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ScanScreen(
    modifier: Modifier = Modifier,
    token: String,
    historyViewModel: HistoryViewModel,
    navigateToDetail: (Int) -> Unit
) {
    Log.d("token", "token: ${token}")

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isCameraReady by remember { mutableStateOf(false) }
    val emptyImageUri = Uri.parse("/dev/null")
    var imageUri by remember { mutableStateOf(emptyImageUri) }

    var previewUseCase by remember { mutableStateOf<UseCase>(Preview.Builder().build()) }
    val imageCaptureUseCase by remember {
        mutableStateOf(
            ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()
        )
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    var isLoading by remember { mutableStateOf(false) }

    val launcherGallery = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    )
    { uri: Uri? ->
        if (uri != null) {
            isLoading = true
            imageUri = uri
            processAndFetch(
                imageUri = imageUri,
                context = context,
                historyViewModel = historyViewModel,
                onProcessAdditional = {
                    isLoading = false
                },
                navigateToDetail = {
                    isLoading = false
                    navigateToDetail(it)
                }
            )
        } else {
            isLoading = false
            Log.d("Photo Picker", "No media selected")
        }
    }

    Permission(
        permission = android.Manifest.permission.CAMERA,
        text = stringResource(R.string.camera_permission),
        permissionNotAvailableContent = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.camera_access_rejected),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        })
                    }) {
                    Text(
                        text = stringResource(R.string.open_settings),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    ) {

        Box(modifier = modifier) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onUseCase = {
                    previewUseCase = it
                },
                previewUseCase = previewUseCase,
                lifecycleOwner = lifecycleOwner
            )

            Button(
                onClick = {
                    launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    isLoading = true
                },
                shape = CircleShape,
                contentPadding = PaddingValues(16.dp),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .padding(vertical = 44.dp)
                    .padding(start = 40.dp, end = 8.dp)
                    .align(Alignment.BottomStart),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_gallery),
                    contentDescription = stringResource(R.string.open_gallery),
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                )
            }

            OutlinedButton(
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .align(Alignment.BottomCenter),
                shape = CircleShape,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(if (isPressed) 8.dp else 12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                onClick = { /* GNDN */ },
                enabled = false
            ) {
                Button(
                    modifier = Modifier
                        .size(80.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPressed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
                    ),
                    interactionSource = interactionSource,
                    enabled = !isLoading,
                    onClick = {
                        isLoading = true
                        if (isCameraReady) {
                            val photoFile = createCustomTempFile(context)
                            val outputOptions =
                                ImageCapture.OutputFileOptions.Builder(photoFile).build()
                            imageCaptureUseCase.takePicture(
                                outputOptions,
                                context.executor,
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        imageUri = output.savedUri

                                        processAndFetchCamera(
                                            imageUri = imageUri,
                                            context = context,
                                            historyViewModel = historyViewModel,
                                            onProcessAdditional = {
                                                saveToGallery(context, imageUri)
//                                                deleteTempFile(photoFile)
                                            },
                                            navigateToDetail = {
                                                navigateToDetail(it)
                                            }
                                        )
                                    }


                                    override fun onError(ex: ImageCaptureException) {
                                        isLoading = false
                                        deleteTempFile(photoFile)
                                        Toast.makeText(
                                            context,
                                            "Failed to capture image.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.e(ContentValues.TAG, "onError: ${ex.message}")
                                    }
                                }
                            )
                        } else {
                            isLoading = false
                            Toast.makeText(
                                context,
                                "Camera is not ready. Please wait for initialization.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    // No content
                }
            }

            if (isLoading) {
                LoadingScreen()
            }

            LaunchedEffect(previewUseCase) {
                val cameraProvider = context.getCameraProvider()
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        previewUseCase,
                        imageCaptureUseCase
                    )
                    isCameraReady = true

                } catch (ex: Exception) {
                    Log.e("CameraCapture", "Failed to bind camera use cases", ex)
                }
            }
        }
    }
}

fun processAndFetch(
    imageUri: Uri,
    context: Context,
    historyViewModel: HistoryViewModel,
    onProcessAdditional: () -> Unit,
    navigateToDetail: (Int) -> Unit
) {
    // Execute the time-consuming task in a background thread using coroutines
    CoroutineScope(Dispatchers.IO).launch {
        processImage(context, imageUri) { result ->
            val recipes = readRecipesFromJson(context)
            val foodName = recipes[result].name
            val lectineStatus = recipes[result].status

            val ingredients: List<Ingredient> = recipes[result].ingredients

            val foodPhoto = uriToFile(imageUri, context).reduceFileImage()
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
    navigateToDetail: (Int) -> Unit
) {
    processImage(context, imageUri) { result ->
        val recipes = readRecipesFromJson(context)
        val foodName = recipes[result].name
        val lectineStatus = recipes[result].status

        val ingredients: List<Ingredient> = recipes[result].ingredients

        val foodPhoto = imageUri.toFile().reduceFileImage()
        Log.d("Image File", "showImage: ${foodPhoto.path}")

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
