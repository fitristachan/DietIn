package com.dietinapp.model

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.dietinapp.ml.ModelActivation1539594V9
import com.dietinapp.utils.uriToBitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

var indexResult by mutableStateOf(0)
fun processImage(context: Context, imageUri: Uri?) {
    imageUri?.let { uri ->
        val imageBitmap = uriToBitmap(context, uri)
        val model = ModelActivation1539594V9.newInstance(context)

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

            //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
            for (i in 0 until width) {
                for (j in 0 until height) {
                    val value = intValues[pixel++] // RGB
                    byteBuffer.putFloat((value shr 16 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((value shr 8 and 0xFF) * (1f / 255f))
                    byteBuffer.putFloat((value and 0xFF) * (1f / 255f))
                }
            }

            inputFeature0.loadBuffer(byteBuffer)
            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(width, height, ResizeOp.ResizeMethod.BILINEAR))
                .build()

            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(imageBitmap)
            val processedImage = imageProcessor.process(tensorImage)
            inputFeature0.loadBuffer(processedImage.buffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

            var maxConfidence = outputFeature0[0]
            var maxIndex = 0

            outputFeature0.forEachIndexed { index, confidence ->
                if (confidence > maxConfidence) {
                    maxConfidence = confidence
                    maxIndex = index
                }
            }


            indexResult = maxIndex

            downsampledBitmap.recycle()

        } else {
            Toast.makeText(context, "Failed to load image bitmap.", Toast.LENGTH_SHORT).show()
        }
        model.close()
    }
}