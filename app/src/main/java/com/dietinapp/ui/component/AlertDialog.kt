package com.dietinapp.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dietinapp.R

@Composable
fun ErrorDialog(
    showDialog: Boolean,
    errorMsg: String,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        Box {
            AlertDialog(
                containerColor = MaterialTheme.colorScheme.background,
                onDismissRequest = onDismiss,
                text = {
                    Text(
                        text = errorMsg,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
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

@Composable
fun ConfirmationDialog(
    showDialog: Boolean,
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            containerColor = MaterialTheme.colorScheme.background,
            onDismissRequest = onCancel,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary)
            },
            text = {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground)
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    onClick = onConfirm) {
                    Text(
                        text = stringResource(id = R.string.logout_confirm),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondaryContainer)
                }
            },
            dismissButton = {
                Button(onClick = onCancel) {
                    Text(
                        text = stringResource(id = R.string.logout_cancel),
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        )
    }
}