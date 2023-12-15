package com.dietinapp.ui.screen.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.dietinapp.R
import com.dietinapp.article.readArticleFromJson
import com.dietinapp.database.datastore.UserPreferenceViewModel
import com.dietinapp.retrofit.data.viewmodel.HistoryPagingViewModel
import com.dietinapp.retrofit.data.viewmodel.HistoryPagingViewModelFactory
import com.dietinapp.ui.component.ArticleCard
import com.dietinapp.ui.component.ScanCard
import com.dietinapp.utils.capitalizeFirstLetter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    username: String,
    historyPagingViewModel: HistoryPagingViewModel = viewModel(
        factory = HistoryPagingViewModelFactory.getInstance(LocalContext.current)
    ),
    userPreferenceViewModel: UserPreferenceViewModel,
    navigateToDetail: (String) -> Unit,
    navigateToHistory: () -> Unit,
    navigateToArticle: (Int) -> Unit,
) {
    val historiesPagingItems =
        historyPagingViewModel.getHistoriesLimited("", "", null)
            .collectAsLazyPagingItems()
    LaunchedEffect(historyPagingViewModel) {
        historyPagingViewModel.getHistoriesLimited("", "", null)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val articleJson = remember { readArticleFromJson(context) }
    var photo by remember { mutableStateOf("") }
    userPreferenceViewModel.getPhoto().observe(lifecycleOwner) {
        photo = it.toString()
    }

    LazyColumn(
        userScrollEnabled = true,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
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
                if (historiesPagingItems.itemCount == 0) {
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
                                contentDescription = stringResource(R.string.never_scan),
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
                    items(historiesPagingItems.itemCount.coerceAtMost(5)) { index ->
                        ScanCard(
                            modifier = Modifier.padding(vertical = 16.dp),
                            foodName = historiesPagingItems[index]!!.foodName,
                            foodPhoto = historiesPagingItems[index]!!.foodPhoto.toUri(),
                            foodStatus = if (!historiesPagingItems[index]!!.lectineStatus) stringResource(
                                R.string.free_lectine
                            )
                            else stringResource(R.string.contain_lectine),
                            color = if (!historiesPagingItems[index]!!.lectineStatus) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.tertiary,
                            onClick = {
                                navigateToDetail(historiesPagingItems[index]!!.id)
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.heightIn(min = 8.dp))
        }

        stickyHeader {
            Text(
                text = stringResource(R.string.lectine_article),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 4.dp, vertical = 12.dp)
                    .padding(top = 8.dp)
            )
        }

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