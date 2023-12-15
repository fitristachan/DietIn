package com.dietinapp.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
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
fun DietRecomendationCard(
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        Box {
            AlertDialog(
                containerColor = MaterialTheme.colorScheme.background,
                onDismissRequest = onDismiss,
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Image(painter = painterResource(id = R.drawable.logo),
                            contentDescription = stringResource(id = R.string.app_name))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.coming_soon),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
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