package com.dietinapp.ui.screen.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.dietinapp.ui.component.Search
import com.dietinapp.utils.queryMonth
import com.dietinapp.utils.queryToday
import com.dietinapp.utils.queryYear

@Composable
fun HistoryScreen(
    modifier: Modifier,
    navigateToDetail: (String) -> Unit,
    historyPagingViewModel: HistoryPagingViewModel = viewModel(
        factory = HistoryPagingViewModelFactory.getInstance(LocalContext.current)
    )
) {
    var queryName by remember { mutableStateOf("") }
    var queryDate by remember { mutableStateOf("") }
    var queryStatus by remember { mutableStateOf<Boolean?>(null) }

    val historiesPagingItems =
        historyPagingViewModel.getHistories(queryName, queryDate, queryStatus)
            .collectAsLazyPagingItems()
    LaunchedEffect(historyPagingViewModel) {
        historyPagingViewModel.getHistories(queryName, queryDate, queryStatus)
    }

    val interactionTodaySource = remember { MutableInteractionSource() }
    val isTodayPressed by interactionTodaySource.collectIsPressedAsState()

    val interactionYearSource = remember { MutableInteractionSource() }
    val isYearPressed by interactionYearSource.collectIsPressedAsState()

    val interactionMonthSource = remember { MutableInteractionSource() }
    val isMonthPressed by interactionMonthSource.collectIsPressedAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyRow(
            userScrollEnabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            item {
                Button(
                    onClick = {
                        queryDate = ""
                        queryStatus = null },
                    interactionSource = interactionTodaySource,
                    colors = ButtonDefaults.buttonColors(Color.White),
                    border = BorderStroke(
                        1.dp,
                        if (isTodayPressed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 2.dp)
                ) {
                    Text(
                        stringResource(R.string.filter_all),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isTodayPressed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                Button(
                    onClick = { queryDate = queryToday() },
                    interactionSource = interactionTodaySource,
                    colors = ButtonDefaults.buttonColors(Color.White),
                    border = BorderStroke(
                        1.dp,
                        if (isTodayPressed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 2.dp)
                )
                {
                    Text(
                        "Hari ini",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isTodayPressed) Color.DarkGray else MaterialTheme.colorScheme.primary
                    )
                }
            }
            item {
                Button(
                    onClick = { queryDate = queryMonth() },
                    interactionSource = interactionMonthSource,
                    colors = ButtonDefaults.buttonColors(Color.White),
                    border = BorderStroke(
                        1.dp,
                        if (isMonthPressed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 2.dp)
                )
                {
                    Text(
                        stringResource(R.string.filter_month),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isTodayPressed) Color.DarkGray else MaterialTheme.colorScheme.primary
                    )
                }
            }
            item {
                Button(
                    onClick = { queryDate = queryYear() },
                    interactionSource = interactionYearSource,
                    colors = ButtonDefaults.buttonColors(Color.White),
                    border = BorderStroke(
                        1.dp,
                        if (isYearPressed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 2.dp)
                )
                {
                    Text(
                        stringResource(R.string.filter_year),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isTodayPressed) Color.DarkGray else MaterialTheme.colorScheme.primary
                    )
                }
            }
            item {
                Button(
                    onClick = { queryStatus = false },
                    interactionSource = interactionYearSource,
                    colors = ButtonDefaults.buttonColors(Color.White),
                    border = BorderStroke(
                        1.dp,
                        if (isYearPressed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 2.dp)
                )
                {
                    Text(
                        stringResource(R.string.low_lectine),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isTodayPressed) Color.DarkGray else MaterialTheme.colorScheme.primary
                    )
                }
            }
            item {
                Button(
                    onClick = { queryStatus = true },
                    interactionSource = interactionYearSource,
                    colors = ButtonDefaults.buttonColors(Color.White),
                    border = BorderStroke(
                        1.dp,
                        if (isYearPressed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 2.dp)
                )
                {
                    Text(
                        stringResource(R.string.high_lectine),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isTodayPressed) Color.DarkGray else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Search(
                modifier = Modifier
                    .fillMaxWidth(),
                query = queryName,
                onQueryChange = {
                    queryName = it
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
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
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxSize()
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.onboarding_first),
                                    contentDescription = stringResource(R.string.never_scan_message),
                                    modifier = Modifier
                                        .size(80.dp)
                                        .align(Alignment.CenterHorizontally)
                                )

                                Spacer(modifier = Modifier.heightIn(min = 16.dp))

                                Text(
                                    stringResource(R.string.never_scan_message),
                                    style = MaterialTheme.typography.titleMedium
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

