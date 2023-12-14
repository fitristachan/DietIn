package com.dietinapp.ui.screen.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.dietinapp.R
import com.dietinapp.firebase.AuthViewModel
import com.dietinapp.ui.component.LoadingScreen
import com.dietinapp.ui.component.ErrorDialog
import java.util.regex.Pattern

@Composable
fun ChangePasswordScreen(
    modifier: Modifier = Modifier,
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

    var password by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var isPasswordError by rememberSaveable { mutableStateOf(false) }
    var isOldPasswordError by rememberSaveable { mutableStateOf(false) }
    var isConfirmPasswordError by rememberSaveable { mutableStateOf(false) }

    var passwordErrorMessage by rememberSaveable { mutableStateOf("") }
    var oldPasswordErrorMessage by rememberSaveable { mutableStateOf("") }
    var confirmPasswordErrorMessage by rememberSaveable { mutableStateOf("") }

    var isPasswordHidden by rememberSaveable { mutableStateOf(true) }
    val visibilityIcon =
        if (isPasswordHidden) R.drawable.ic_visibility else R.drawable.ic_visibility_off
    val description =
        if (isPasswordHidden) stringResource(R.string.show_password) else stringResource(R.string.hide_password)

    fun validatePassword(password: String) {
        val pattern: Pattern = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}\$"
        )

        if (password.isEmpty()) {
            isPasswordError = true
            passwordErrorMessage = context.getString(R.string.empty_password_warning)
        } else if (password.length < 6 ) {
            isPasswordError = true
            passwordErrorMessage = context.getString(R.string.password_length_warning)
        } else if (!pattern.matcher(password).matches()) {
            isPasswordError = true
            passwordErrorMessage = context.getString(R.string.password_patern_warning)
        } else {
            isPasswordError = false
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String) {

        if (confirmPassword.isEmpty()) {
            isConfirmPasswordError = true
            confirmPasswordErrorMessage = context.getString(R.string.confirm_password_empty_warning)
        } else if (password != confirmPassword) {
            isConfirmPasswordError = true
            confirmPasswordErrorMessage = context.getString(R.string.password_not_match_warning)
        } else {
            isConfirmPasswordError = false
        }
    }

    fun validateOldPassword(oldPassword: String) {
        if (oldPassword.isEmpty()) {
            isOldPasswordError = true
            oldPasswordErrorMessage = context.getString(R.string.old_password_empty_warning)
        } else {
            isOldPasswordError = false
        }
    }

    Column(
        Modifier
            .padding(vertical = 16.dp , horizontal = 32.dp)
            .fillMaxSize()
    ) {
        OutlinedTextField(
            value = oldPassword,
            singleLine = true,
            onValueChange = {
                validateOldPassword(it)
                oldPassword = it
            },
            isError = isOldPasswordError,
            supportingText = {
                if (isOldPasswordError) {
                    Text(
                        text = oldPasswordErrorMessage,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            },
            visualTransformation = if (isPasswordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = stringResource(R.string.old_password)
                )
            },
            trailingIcon = {
                IconButton(onClick = { isPasswordHidden = !isPasswordHidden }) {
                    Icon(
                        painter = painterResource(visibilityIcon),
                        contentDescription = description
                    )
                }
            },
            label = {
                Text(
                    text = stringResource(R.string.old_password),
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
            value = password,
            singleLine = true,
            onValueChange = {
                validatePassword(it)
                password = it
            },
            isError = isPasswordError,
            supportingText = {
                if (isPasswordError) {
                    Text(
                        text = passwordErrorMessage,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            },
            visualTransformation = if (isPasswordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = stringResource(R.string.new_password)
                )
            },
            trailingIcon = {
                IconButton(onClick = { isPasswordHidden = !isPasswordHidden }) {
                    Icon(
                        painter = painterResource(visibilityIcon),
                        contentDescription = description
                    )
                }
            },
            label = {
                Text(
                    text = stringResource(R.string.new_password),
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
            value = confirmPassword,
            singleLine = true,
            onValueChange = {
                validateConfirmPassword(password, it)
                confirmPassword = it
            },
            isError = isConfirmPasswordError,
            supportingText = {
                if (isConfirmPasswordError) {
                    Text(
                        text = confirmPasswordErrorMessage,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            },
            visualTransformation = if (isPasswordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = stringResource(R.string.password)
                )
            },
            trailingIcon = {
                IconButton(onClick = { isPasswordHidden = !isPasswordHidden }) {
                    Icon(
                        painter = painterResource(visibilityIcon),
                        contentDescription = description
                    )
                }
            },
            label = {
                Text(
                    text = stringResource(R.string.confirm_password),
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
                    authViewModel.password.value = password
                    authViewModel.oldPassword.value = oldPassword
                    onClick()
                },
                enabled = !isPasswordError && !isOldPasswordError && !isConfirmPasswordError  && password.isNotEmpty() && oldPassword.isNotEmpty() && confirmPassword.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.change_password),
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