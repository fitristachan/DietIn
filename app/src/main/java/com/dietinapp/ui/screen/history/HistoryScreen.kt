package com.dietinapp.ui.screen.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.dietinapp.R
import com.dietinapp.retrofit.data.viewmodel.HistoryPagingViewModel
import com.dietinapp.retrofit.data.viewmodel.HistoryPagingViewModelFactory
import com.dietinapp.ui.component.ErrorMessage
import com.dietinapp.ui.component.LoadingScreen
import com.dietinapp.ui.component.ScanCard

@Composable
fun HistoryScreen(
    modifier: Modifier,
    navigateToDetail: (String) -> Unit,
    navigateBack: () -> Unit,
    historyPagingViewModel: HistoryPagingViewModel = viewModel(
        factory = HistoryPagingViewModelFactory.getInstance(LocalContext.current)
    )
) {
    val historiesPagingItems = historyPagingViewModel.getHistories().collectAsLazyPagingItems()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            //back button
            IconButton(
                onClick = { navigateBack() },
                modifier = Modifier.size(35.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button)
                )
            }
            Text(
                text = stringResource(R.string.history),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            userScrollEnabled = true,
        ) {
            items(historiesPagingItems.itemCount) { index ->
                ScanCard(
                    modifier = Modifier.padding(vertical = 16.dp),
                    foodName = historiesPagingItems[index]!!.foodName,
                    foodPhoto = historiesPagingItems[index]!!.foodPhoto.toUri(),
                    foodStatus = if (!historiesPagingItems[index]!!.lectineStatus) stringResource(
                        R.string.low_lectine
                    )
                    else stringResource(R.string.high_lectine),
                    color = if (!historiesPagingItems[index]!!.lectineStatus) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.tertiary,
                    onClick = {
                        navigateToDetail(historiesPagingItems[index]!!.id)
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        ) {
            historiesPagingItems.apply {
                when {
                    this.loadState.refresh is LoadState.Loading -> {
                        item { LoadingScreen() }
                    }

                    loadState.refresh is LoadState.Error -> {
                        val error = historiesPagingItems.loadState.refresh as LoadState.Error
                        item {
                            ErrorMessage(
                                modifier = Modifier.wrapContentSize(),
                                message = error.error.localizedMessage!!,
                                onClickRetry = { retry() })
                        }
                    }

                    loadState.append is LoadState.Loading -> {
                        item { LoadingScreen() }
                    }

                    this.itemCount == 0 -> {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .fillMaxWidth()
                                    .heightIn(min = 140.dp)
                            ) {
                                Text(
                                    stringResource(R.string.never_scan_message),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    loadState.append is LoadState.Error -> {
                        val error = historiesPagingItems.loadState.append as LoadState.Error
                        item {
                            ErrorMessage(
                                modifier = Modifier,
                                message = error.error.localizedMessage!!,
                                onClickRetry = { retry() })
                        }
                    }
                }
            }
        }
    }
}

