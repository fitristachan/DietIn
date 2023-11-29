package com.dietin.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dietin.R
import com.dietin.ui.component.ProfileItemVector

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.dummy_photo),
                contentDescription = "foto",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(shape = CircleShape)
            )

            Spacer(modifier = Modifier.size(16.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.heightIn(min = 8.dp))

                Text(
                    text = "Keysha",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Keysha@gmail.com",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.heightIn(min = 28.dp))


        Column(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.contact_information),
                style = MaterialTheme.typography.titleLarge
            )


            LazyColumn(
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                item {
                    ProfileItemVector(
                        modifier = Modifier.padding(vertical = 8.dp),
                        itemTitle = stringResource(id = R.string.username),
                        itemIcon = Icons.Filled.Person
                    )
                }

                item {
                    ProfileItemVector(
                        modifier = Modifier.padding(vertical = 8.dp),
                        itemTitle = stringResource(id = R.string.email),
                        itemIcon = Icons.Filled.Email
                    )
                }

                item {
                    ProfileItemVector(
                        modifier = Modifier.padding(vertical = 8.dp),
                        itemTitle = stringResource(id = R.string.password),
                        itemIcon = Icons.Filled.Lock
                    )
                }
            }

            Spacer(modifier = Modifier.heightIn(min = 14.dp))
            Divider(modifier = Modifier.heightIn(min = 1.dp))
            Spacer(modifier = Modifier.heightIn(min = 16.dp))

            Text(
                text = stringResource(R.string.personal),
                style = MaterialTheme.typography.titleLarge
            )


            LazyColumn(
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                item {
                    ProfileItemVector(
                        modifier = Modifier.padding(vertical = 8.dp),
                        itemTitle = stringResource(id = R.string.history),
                        itemIcon = Icons.Filled.Person
                    )
                }

                item {
                    ProfileItemVector(
                        modifier = Modifier.padding(vertical = 8.dp),
                        itemTitle = stringResource(R.string.diet_recommendation),
                        itemIcon = Icons.Filled.Email
                    )
                }
            }

            Spacer(modifier = Modifier.heightIn(min = 16.dp))
            Divider(modifier = Modifier.heightIn(min = 1.dp))
            Spacer(modifier = Modifier.heightIn(min = 12.dp))

            LazyColumn(
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                item {
                    ProfileItemVector(
                        modifier = Modifier.padding(vertical = 8.dp),
                        itemTitle = stringResource(R.string.about_us),
                        itemIcon = Icons.Filled.Person
                    )
                }

                item {
                    ProfileItemVector(
                        modifier = Modifier.padding(vertical = 8.dp),
                        itemTitle = stringResource(R.string.log_out),
                        itemIcon = Icons.Filled.Email
                    )
                }
            }
        }
    }
}