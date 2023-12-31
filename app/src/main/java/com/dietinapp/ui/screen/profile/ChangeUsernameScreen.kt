package com.dietinapp.ui.screen.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dietinapp.R
import com.dietinapp.firebase.AuthViewModel
import com.dietinapp.ui.component.LoadingScreen
import com.dietinapp.ui.component.ErrorDialog

@Composable
fun ChangeUsernameScreen(
    modifier: Modifier = Modifier,
    oldUsername: String,
    authViewModel: AuthViewModel,
    onClick: () -> Unit
){
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var showDialog by remember { mutableStateOf(false) }
    authViewModel.showDialog.observe(lifecycleOwner){
        showDialog = it
    }

    var authError by remember { mutableStateOf("") }
    authViewModel.errorMessage.observe(lifecycleOwner){
        authError = it
    }

    var isLoading by remember { mutableStateOf(false) }
    authViewModel.isLoading.observe(lifecycleOwner){
        isLoading = it
    }

    var username by remember { mutableStateOf("") }

    var isUsernameError by rememberSaveable { mutableStateOf(false) }

    var usernameErrorMessage by rememberSaveable { mutableStateOf("") }

    fun validateUsername(username: String) {
        if (username.isEmpty()) {
            isUsernameError = true
            usernameErrorMessage = context.getString(R.string.empty_username_warning)
        } else if (username.length > 15){
            isUsernameError = true
            usernameErrorMessage = context.getString(R.string.username_length_warning)
        } else if (username.contains(" ")){
            isUsernameError = true
            usernameErrorMessage = context.getString(R.string.username_contain_space)
        } else {
            isUsernameError = false
        }
    }

    Column(
        Modifier
            .padding(vertical = 16.dp , horizontal = 32.dp)
            .fillMaxSize()
    ) {
        OutlinedTextField(
            value = oldUsername,
            enabled = false,
            singleLine = true,
            onValueChange = {},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = stringResource(R.string.old_username)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.old_username),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            colors = TextFieldDefaults.colors(
                errorTextColor = MaterialTheme.colorScheme.tertiary,
                errorLeadingIconColor = MaterialTheme.colorScheme.tertiary,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                errorContainerColor = MaterialTheme.colorScheme.background,
            ),
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.heightIn(min = 24.dp))

        OutlinedTextField(
            value = username,
            singleLine = true,
            onValueChange = {
                validateUsername(it)
                username = it
            },
            isError = isUsernameError,
            supportingText = {
                if (isUsernameError) {
                    Text(
                        text = usernameErrorMessage,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = stringResource(R.string.new_username)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.new_username),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            colors = TextFieldDefaults.colors(
                errorTextColor = MaterialTheme.colorScheme.tertiary,
                errorLeadingIconColor = MaterialTheme.colorScheme.tertiary,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                errorContainerColor = MaterialTheme.colorScheme.background,
            ),
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.heightIn(min = 32.dp))

        Column {
            Button(
                onClick = {
                    authViewModel.username.value = username
                    onClick()
                },
                enabled = !isUsernameError && username.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.change_username),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.background,
                )
            }
        }
    }
    if (isLoading) {
        LoadingScreen()
    }
    if (authError.isNotEmpty()) {
        ErrorDialog(
            showDialog = showDialog,
            errorMsg = authError,
            onDismiss =
            {
                isLoading = false
                showDialog = false
            }
        )
    }
}