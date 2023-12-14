package com.dietinapp.ui.screen.detail

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.dietinapp.article.readArticleFromJson

@Composable
fun ArticleScreen(
    articleId: Int,
) {
    val context = LocalContext.current
    val articleJson = remember { readArticleFromJson(context) }
    val articleLink = articleJson[articleId].link.toString()

    Column {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()

                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.setSupportZoom(true)
                }
            },
            update = { webView ->
                webView.loadUrl(articleLink)
            },
            modifier = Modifier
                .fillMaxSize()
        )
    }
}