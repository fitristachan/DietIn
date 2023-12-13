package com.dietinapp.article

import android.net.Uri
data class ArticleData(
    val id: Int,
    val title: String,
    val photo: Uri,
    val writer: String,
    val publisher: String,
    val link: Uri
)
