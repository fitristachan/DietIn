package com.dietinapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.FileDescriptor
import java.io.IOException

fun uriToBitmap(context: Context, selectedFileUri: Uri): Bitmap? {
    try {
        val parcelFileDescriptor =
            context.contentResolver.openFileDescriptor(selectedFileUri, "r")
        val fileDescriptor: FileDescriptor? = parcelFileDescriptor?.fileDescriptor

        if (fileDescriptor != null) {
            val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return bitmap
        } else {
            Log.e("uriToBitmap", "File descriptor is null")
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("uriToBitmap", "Failed to load bitmap from Uri: ${e.message}")
    }
    return null

}