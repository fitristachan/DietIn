package com.dietin.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraCapture(
    modifier: Modifier,
    scanProcess: () -> Unit
) {

}


@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onUseCase: (UseCase) -> Unit = { },
    previewUseCase: UseCase,
    lifecycleOwner: LifecycleOwner
) {
    val coroutineScope = rememberCoroutineScope()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

//            val gestureDetector = ScaleGestureDetector(
//                context,
//                object : ScaleGestureDetector.OnScaleGestureListener {
//
//                    override fun onScale(detector: ScaleGestureDetector): Boolean {
//                        coroutineScope.launch {
//                            try {
//                                val cameraProvider = context.getCameraProvider()
//                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//                                cameraProvider.unbindAll()
//                                val camera = cameraProvider.bindToLifecycle(
//                                    lifecycleOwner,
//                                    cameraSelector,
//                                    previewUseCase
//                                )
//                                val cameraControl = camera.cameraControl
//                                val cameraInfo = camera.cameraInfo
//                                val currentZoomRatio = cameraInfo.zoomState.value?.zoomRatio ?: 0F
//                                val delta = detector.scaleFactor
//                                cameraControl.setZoomRatio(currentZoomRatio * delta)
//                            } catch (ex: Exception) {
//                                Log.e("CameraPreview", "Error adjusting zoom", ex)
//                            }
//                        }
//                        return true
//                    }
//
//                    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
//                        return true
//                    }
//
//                    override fun onScaleEnd(detector: ScaleGestureDetector) {
//                    }
//
//                })


//            previewView.setOnTouchListener { _, event ->
//                gestureDetector.onTouchEvent(event)
//                return@setOnTouchListener true
//            }

            onUseCase(
                Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
            )
            previewView
        }
    )
}


suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    ProcessCameraProvider.getInstance(this).also { future ->
        future.addListener({
            continuation.resume(future.get())
        }, executor)
    }
}

val Context.executor: Executor
    get() = ContextCompat.getMainExecutor(this)
