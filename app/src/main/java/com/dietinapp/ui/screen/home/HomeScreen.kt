package com.dietinapp.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.dietinapp.R
import com.dietinapp.article.readArticleFromJson
import com.dietinapp.retrofit.data.viewmodel.HistoryViewModel
import com.dietinapp.ui.component.ArticleCard
import com.dietinapp.ui.component.ScanCard
import com.dietinapp.utils.capitalizeFirstLetter

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    username: String,
    photo: String,
    historyViewModel: HistoryViewModel,
    navigateToDetail: (String) -> Unit,
    navigateToHistory: () -> Unit,
    navigateToArticle: (Int) -> Unit,
) {
    val historyList by historyViewModel.getHistoriesLimited().collectAsState()
    LaunchedEffect(historyViewModel) {
        historyViewModel.getHistoriesLimited()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.heightIn(min = 8.dp))

                Text(
                    text = "Hai, ${username.capitalizeFirstLetter()}!",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = stringResource(R.string.welcome_back),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (photo.isEmpty() || photo == "" || photo == "null") {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .border(5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                        .wrapContentSize()
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_avatar),
                        contentDescription = username,
                        modifier = Modifier
                            .clip(shape = CircleShape)
                            .size(80.dp)
                    )
                }
            } else {
                AsyncImage(
                    model = photo.toUri(),
                    contentDescription = username,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(shape = CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.heightIn(min = 24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                text = stringResource(R.string.history),
                style = MaterialTheme.typography.headlineSmall
            )

            TextButton(
                onClick = { navigateToHistory() },
            ) {
                Text(
                    text = stringResource(R.string.see_more),
                    style = MaterialTheme.typography.titleSmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.heightIn(min = 8.dp))

        LazyRow(
            userScrollEnabled = true,
            modifier = Modifier
                .wrapContentSize()
        ) {
            if (historyList.isEmpty()) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Image(
                            painter = painterResource(R.drawable.onboarding_first),
                            contentDescription = stringResource(R.string.never_scan_message),
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.CenterVertically)
                        )

                        Spacer(modifier = Modifier.widthIn(min = 16.dp))

                        Text(
                            stringResource(R.string.never_scan_message),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            } else {
                items(items = historyList.take(5), key = { it.id }) { history ->
                    ScanCard(
                        modifier = Modifier,
                        foodName = history.foodName,
                        foodPhoto = history.foodPhoto.toUri(),
                        foodStatus = if (!history.lectineStatus) stringResource(R.string.low_lectine)
                        else stringResource(R.string.high_lectine),
                        color = if (!history.lectineStatus) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.tertiary,
                        onClick = {
                            navigateToDetail(history.id)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.heightIn(min = 24.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.lectine_article),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.heightIn(min = 8.dp))

            val context = LocalContext.current
            val articleJson = remember { readArticleFromJson(context) }
            LazyColumn(
                userScrollEnabled = true,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(articleJson, key = { it.id }) { article ->
                    ArticleCard(
                        modifier = Modifier,
                        title = article.title,
                        writer = article.writer,
                        publisher = article.publisher,
                        photo = article.photo,
                        onClick = {
                            navigateToArticle(article.id)
                        }
                    )
                }
            }
        }
    }
}