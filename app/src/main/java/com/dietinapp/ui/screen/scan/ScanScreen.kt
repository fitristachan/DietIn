package com.dietinapp.ui.screen.scan

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dietinapp.R
import com.dietinapp.model.processImage
import com.dietinapp.model.uri
import com.dietinapp.ui.component.CameraPreview
import com.dietinapp.ui.component.LoadingScreen
import com.dietinapp.utils.Permission
import com.dietinapp.utils.createCustomTempFile
import com.dietinapp.ui.component.executor
import com.dietinapp.ui.component.getCameraProvider

@Composable
fun ScanScreen(
    modifier: Modifier = Modifier,
    navigateToDetail: (Int) -> Unit
) {
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
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            var isLoading by remember { mutableStateOf(false)}

            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onUseCase = {
                    previewUseCase = it
                },
                previewUseCase = previewUseCase,
                lifecycleOwner = lifecycleOwner
            )

            OutlinedButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 32.dp)
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
                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                            imageCaptureUseCase.takePicture(
                                outputOptions,
                                context.executor,
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        imageUri = output.savedUri
                                        uri.value = output.savedUri.toString()
                                        // Call the function and pass a callback to receive the result
                                        processImage(context, imageUri) { result ->
                                            navigateToDetail(result)
                                        }


                                    }

                                    override fun onError(ex: ImageCaptureException) {
                                        isLoading = false
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

            if (isLoading){
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

