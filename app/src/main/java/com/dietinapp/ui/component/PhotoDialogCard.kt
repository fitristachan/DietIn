package com.dietinapp.ui.component

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.dietinapp.R
import com.dietinapp.database.datastore.UserPreferenceViewModel
import com.dietinapp.firebase.AuthViewModel
import com.dietinapp.utils.uriToFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PhotoDialogCard(
    modifier: Modifier = Modifier,
    context: Context,
    showDialog: Boolean,
    oldPhoto: Uri,
    onDismiss: () -> Unit,
    authViewModel: AuthViewModel,
    userPreferenceViewModel: UserPreferenceViewModel
){
    if (showDialog) {
        var imageUri by remember { mutableStateOf<Uri?>(oldPhoto) }
        var isEnabled by remember { mutableStateOf(false) }

        val launcherGallery = rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        )
        { uri: Uri? ->
            if (uri != null) {
                imageUri = uri
                isEnabled = true
            } else {
                Log.d("Photo Picker", "No media selected")
            }
        }

        if (imageUri == null){
            isEnabled = false
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onBackground.copy(0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(20))
                    .wrapContentSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { onDismiss() },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp, end = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.close)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.change_photo),
                    style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(24.dp))

                AsyncImage(
                    model = imageUri,
                    contentDescription = stringResource(R.string.old_photo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20))
                        .background(Color.Transparent, RoundedCornerShape(20))
                        .size(120.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        border = BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.primary
                        ),
                        onClick = { launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                    ) {
                        Text(
                            text = stringResource(R.string.choose_new_photo),
                            style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                val parseImage = uriToFile(imageUri!!, context)
                                authViewModel.updatePhoto(
                                    photo = parseImage.toUri(),
                                    onUpdateComplete = {
                                        userPreferenceViewModel.savePhoto(parseImage.toUri().toString())
                                        Toast.makeText(
                                            context,
                                            R.string.change_photo_successfully, Toast.LENGTH_SHORT
                                        ).show()
                                        onDismiss()
                                    },
                                    onUpdateError = {
                                        Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT)
                                            .show()
                                        onDismiss()
                                    }
                                )
                            }
                        },
                        enabled = isEnabled
                    ) {
                        Text(
                            text = stringResource(R.string.change_photo),
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}