package com.dietin.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import com.dietin.R
import com.dietin.ui.component.ArticleCard
import com.dietin.ui.component.ScanCard

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
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
                    text = "Hai, Keysha!",
                    style = MaterialTheme.typography.headlineMedium)
                
                Text(
                    text = "Hai, Keysha!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Image(
                painter = painterResource(R.drawable.dummy_photo),
                contentDescription = "foto",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(shape = CircleShape)
            )
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
                onClick = { /*TODO*/ },
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
                    foodName = index.toString()
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