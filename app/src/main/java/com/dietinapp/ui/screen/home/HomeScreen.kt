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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.dietinapp.R
import com.dietinapp.ui.component.ArticleCard
import com.dietinapp.ui.component.ScanCard
import com.dietinapp.utils.capitalizeFirstLetter

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    username: String,
    photo: String,
    navigateToDetail: () -> Unit,
    navigateToHistory: () -> Unit
){
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ){
        Row (
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column (
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.heightIn(min = 8.dp))

                Text(
                    text = "Hai, ${username.capitalizeFirstLetter()}!",
                    style = MaterialTheme.typography.headlineMedium)
                
                Text(
                    text = stringResource(R.string.welcome_back),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (photo.isEmpty() || photo == "" || photo == "null"){
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
                        modifier = Modifier.clip(shape = CircleShape).size(80.dp)
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

        ){
            Text(
                text = stringResource(R.string.history),
                style = MaterialTheme.typography.headlineSmall)

            TextButton(
                onClick = { navigateToHistory() },
            ) {
                Text(
                    text = stringResource(R.string.see_more),
                    style = MaterialTheme.typography.titleSmall,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.heightIn(min = 8.dp))

        LazyRow(
            userScrollEnabled = true,
            modifier = Modifier
                .wrapContentSize()
        ){
            items(count = 10) { index ->
                ScanCard(
                    modifier = Modifier,
                    foodName = index.toString(),
                    onClick = {
                        navigateToDetail()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.heightIn(min = 24.dp))

        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp)
        ){
            Text(
                text = stringResource(R.string.lectine_article),
                style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.heightIn(min = 8.dp))

            LazyColumn(
                userScrollEnabled = true,
                modifier = Modifier
                    .fillMaxWidth()
            ){
                items(count = 10) {index ->
                    ArticleCard(
                        modifier = Modifier,
                        articleTitle = index.toString()
                    )
                }
            }
        }

    }

}