package com.dietinapp.article

import android.content.Context
import android.net.Uri
import org.json.JSONArray

fun readArticleFromJson(context: Context): List<ArticleData> {
    val jsonString = context.assets.open("article.json").bufferedReader().use {
        it.readText()
    }

    val articlesList = mutableListOf<ArticleData>()

    val jsonArray = JSONArray(jsonString)
    for (i in 0 until jsonArray.length()) {
        val articleObject = jsonArray.getJSONObject(i)

        val id = articleObject.optInt("id", -1)
        val title = articleObject.optString("title", "")
        val photoUriString = articleObject.optString("photo", "")
        val photo = Uri.parse(photoUriString)
        val writer = articleObject.optString("writer", "")
        val publisher = articleObject.optString("publisher", "")
        val linkUriString = articleObject.optString("link", "")
        val link = Uri.parse(linkUriString)

        val article = ArticleData(id, title, photo, writer, publisher, link)
        articlesList.add(article)
    }

    return articlesList
}