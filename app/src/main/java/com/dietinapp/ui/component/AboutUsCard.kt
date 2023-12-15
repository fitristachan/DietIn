package com.dietinapp.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dietinapp.R

@Composable
fun AboutUsCard(
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        Box {
            AlertDialog(
                containerColor = MaterialTheme.colorScheme.background,
                onDismissRequest = onDismiss,
                text = {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ){
                        Image(painter = painterResource(id = R.drawable.logo),
                            contentDescription = stringResource(id = R.string.app_name))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.app_slogan),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        onClick = onDismiss
                    ) {
                        Text(
                            text = "Ok",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
            )
        }
    }
}