package com.dietinapp.ui.screen.profile


import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import coil.compose.AsyncImage
import com.dietinapp.R
import com.dietinapp.database.datastore.UserPreferenceViewModel
import com.dietinapp.firebase.AuthViewModel
import com.dietinapp.ui.activity.AuthActivity
import com.dietinapp.ui.activity.findActivity
import com.dietinapp.ui.component.AboutUsCard
import com.dietinapp.ui.component.ConfirmationDialog
import com.dietinapp.ui.component.DietRecomendationCard
import com.dietinapp.ui.component.PhotoDialogCard
import com.dietinapp.ui.component.ProfileItemPainter
import com.dietinapp.ui.component.ProfileItemVector
import com.dietinapp.utils.capitalizeFirstLetter

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    username: String,
    email: String,
    authViewModel: AuthViewModel,
    userPreferenceViewModel: UserPreferenceViewModel,
    navigateToHistory: () -> Unit,
    navigateToChangeUsername: () -> Unit,
    navigateToChangeEmail: () -> Unit,
    navigateToChangePassword: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var showPhotoDialog by remember { mutableStateOf(false) }
    var showAboutUs by remember { mutableStateOf(false) }
    var showDietRecomendation by remember { mutableStateOf(false) }


    var photo by remember { mutableStateOf("") }
    userPreferenceViewModel.getPhoto().observe(lifecycleOwner) {
        photo = it.toString()
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (photo.isEmpty() || photo == "" || photo == "null") {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .border(5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                        .wrapContentSize()
                        .clickable(
                            enabled = true,
                            onClick = {
                                showPhotoDialog = true
                            }
                        ),
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
                        .size(70.dp)
                        .clip(shape = CircleShape)
                        .clickable(
                            enabled = true,
                            onClick = {
                                showPhotoDialog = true
                            }
                        )
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.heightIn(min = 8.dp))

                Text(
                    text = username.capitalizeFirstLetter(),
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.heightIn(min = 24.dp))


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
                        itemIcon = Icons.Filled.Person,
                        onClick = {
                            navigateToChangeUsername()
                        }
                    )
                }

                item {
                    ProfileItemVector(
                        modifier = Modifier.padding(vertical = 8.dp),
                        itemTitle = stringResource(id = R.string.email),
                        itemIcon = Icons.Filled.Email,
                        onClick = {
                            navigateToChangeEmail()
                        }
                    )
                }

                item {
                    ProfileItemVector(
                        modifier = Modifier.padding(vertical = 8.dp),
                        itemTitle = stringResource(id = R.string.password),
                        itemIcon = Icons.Filled.Lock,
                        onClick = {
                            navigateToChangePassword()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.heightIn(min = 14.dp))
            Divider(modifier = Modifier.heightIn(min = 1.dp))
            Spacer(modifier = Modifier.heightIn(min = 14.dp))

            Text(
                text = stringResource(R.string.personal),
                style = MaterialTheme.typography.titleLarge
            )


            LazyColumn(
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                item {
                    ProfileItemPainter(
                        modifier = Modifier
                            .padding(vertical = 8.dp),
                        itemTitle = stringResource(id = R.string.history),
                        itemIcon = R.drawable.ic_history,
                        itemTint = MaterialTheme.colorScheme.primary,
                        textColor = MaterialTheme.colorScheme.onBackground,
                        onClick = { navigateToHistory() }
                    )
                }

                item {
                    ProfileItemPainter(
                        modifier = Modifier
                            .padding(vertical = 8.dp),
                        itemTitle = stringResource(R.string.diet_recommendation),
                        itemIcon = R.drawable.ic_diet,
                        itemTint = MaterialTheme.colorScheme.primary,
                        textColor = MaterialTheme.colorScheme.onBackground,
                        onClick = { showDietRecomendation = true }
                    )
                }
            }

            Spacer(modifier = Modifier.heightIn(min = 14.dp))
            Divider(modifier = Modifier.heightIn(min = 1.dp))
            Spacer(modifier = Modifier.heightIn(min = 12.dp))

            var showDialog by remember { mutableStateOf(false) }

            LazyColumn(
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                item {
                    ProfileItemPainter(
                        modifier = Modifier
                            .padding(vertical = 8.dp),
                        itemTitle = stringResource(R.string.about_us),
                        itemIcon = R.drawable.ic_group,
                        itemTint = MaterialTheme.colorScheme.primary,
                        textColor = MaterialTheme.colorScheme.onBackground,
                        onClick = { showAboutUs = true }
                    )
                }

                item {
                    ProfileItemPainter(
                        modifier = Modifier
                            .padding(vertical = 8.dp),
                        itemTitle = stringResource(R.string.log_out),
                        itemIcon = R.drawable.ic_logout,
                        itemTint = MaterialTheme.colorScheme.tertiary,
                        textColor = MaterialTheme.colorScheme.tertiary,
                        onClick = { showDialog = true }
                    )
                }
            }

            AboutUsCard(showDialog = showAboutUs) {
                showAboutUs = false
            }

            DietRecomendationCard(showDialog = showDietRecomendation) {
                showDietRecomendation = false
            }

            ConfirmationDialog(
                showDialog = showDialog,
                title = stringResource(id = R.string.logout_confirmation_title),
                text = stringResource(id = R.string.logout_confirmation_message),
                onConfirm = {
                    authViewModel.signOut(
                        userPreferenceViewModel,
                        onSignOutComplete = {
                            context.findActivity()?.finish()
                            context.startActivity(
                                Intent(
                                    context,
                                    AuthActivity::class.java
                                )
                            )
                        })
                    showDialog = false
                },
                onCancel = {
                    showDialog = false
                }
            )
        }
    }

    PhotoDialogCard(
        showDialog = showPhotoDialog,
        oldPhoto = photo.toUri(),
        onDismiss = { showPhotoDialog = false },
        authViewModel = authViewModel,
        userPreferenceViewModel = userPreferenceViewModel,
        context = context
    )
}


