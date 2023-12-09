package com.dietinapp.model

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import com.dietinapp.ml.ModelActivation153Cleaned9392V4
import com.dietinapp.utils.uriToBitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

var imageUriTemp = mutableStateOf("")

fun processImage(context: Context, imageUri: Uri?, callback: (Int) -> Unit) {
    imageUri?.let { uri ->
        val imageBitmap = uriToBitmap(context, uri)
        val model = ModelActivation153Cleaned9392V4.newInstance(context)

        if (imageBitmap != null) {
            val downsampledBitmap = Bitmap.createScaledBitmap(imageBitmap, 299, 299, true)
            val width = downsampledBitmap.width
            val height = downsampledBitmap.height

            // Creates inputs for reference.
            val inputFeature0 =
                TensorBuffer.createFixedSize(intArrayOf(1, 299, 299, 3), DataType.FLOAT32)

            val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(4 * width * height * 3)
            byteBuffer.order(ByteOrder.nativeOrder())

            val intValues = IntArray(width * height)

            downsampledBitmap.getPixels(intValues, 0, width, 0, 0, width, height)
            var pixel = 0

            // Iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for (i in 0 until width) {
                for (j in 0 until height) {
                    val value = intValues[pixel++] // RGB
                    byteBuffer.putFloat((value shr 16 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((value shr 8 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((value and 0xFF) * (1f / 255f))
                }
            }

            inputFeature0.loadBuffer(byteBuffer)

            // Runs model inference and gets result.
            val outputs: ModelActivation153Cleaned9392V4.Outputs = model.process(inputFeature0)
            val outputFeature0: TensorBuffer = outputs.outputFeature0AsTensorBuffer
            val confidences = outputFeature0.floatArray

            // find the index of the class with the biggest confidence.
            var maxPos = 0
            var maxConfidence = 0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }
            
            // Invoke the callback with the result
            callback.invoke(maxPos)
            imageUriTemp.value = imageUri.toString()

            downsampledBitmap.recycle()
        } else {
            Toast.makeText(context, "Failed to load image bitmap.", Toast.LENGTH_SHORT).show()
        }
        model.close()
    }
}
