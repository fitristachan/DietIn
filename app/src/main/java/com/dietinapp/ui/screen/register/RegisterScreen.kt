package com.dietinapp.ui.screen.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.Alignment
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
fun RegisterScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    navigateToLogin: () -> Unit,
    registerGoogle: () -> Unit,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val state = rememberScrollState(0)

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

    var email by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }

    var confirmPassword by remember { mutableStateOf("") }

    var isUsernameError by rememberSaveable { mutableStateOf(false) }
    var isEmailError by rememberSaveable { mutableStateOf(false) }
    var isPasswordError by rememberSaveable { mutableStateOf(false) }
    var isConfirmPasswordError by rememberSaveable { mutableStateOf(false) }

    var usernameErrorMessage by rememberSaveable { mutableStateOf("") }
    var emailErrorMessage by rememberSaveable { mutableStateOf("") }
    var passwordErrorMessage by rememberSaveable { mutableStateOf("") }
    var confirmPasswordErrorMessage by rememberSaveable { mutableStateOf("") }

    var isPasswordHidden by rememberSaveable { mutableStateOf(true) }
    val visibilityIcon =
        if (isPasswordHidden) R.drawable.ic_visibility else R.drawable.ic_visibility_off
    val description =
        if (isPasswordHidden) stringResource(R.string.show_password) else stringResource(R.string.hide_password)

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

    fun validateEmail(email: String) {
        if (email.isEmpty()) {
            isEmailError = true
            emailErrorMessage = context.getString(R.string.empty_email_warning)
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isEmailError = true
            emailErrorMessage = context.getString(R.string.wrong_email_warning)
        } else {
            isEmailError = false
        }
    }

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


    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(state),
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 16.dp)
                .widthIn(min = 52.dp)
                .heightIn(min = 48.dp)
        )
        Text(
            text = stringResource(R.string.register_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(R.string.register_detail),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.heightIn(min = 32.dp))

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
                    contentDescription = stringResource(R.string.username)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.username),
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

        Spacer(modifier = Modifier.heightIn(min = 16.dp))

        OutlinedTextField(
            value = email,
            singleLine = true,
            onValueChange = {
                validateEmail(it)
                email = it
            },
            isError = isEmailError,
            supportingText = {
                if (isEmailError) {
                    Text(
                        text = emailErrorMessage,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = stringResource(R.string.email)
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.email),
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

        Spacer(modifier = Modifier.heightIn(min = 16.dp))

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
                    text = stringResource(R.string.password),
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

        Spacer(modifier = Modifier.heightIn(min = 16.dp))

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
                    authViewModel.email.value = email
                    authViewModel.username.value = username
                    authViewModel.password.value = password
                    onClick()
                },
                enabled = !isUsernameError && !isEmailError && !isPasswordError && !isConfirmPasswordError
                        && username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.register),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.background,
                )
            }

            Spacer(modifier = Modifier.heightIn(min = 16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = stringResource(R.string.or_inline),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 2.dp)
                        .padding(horizontal = 4.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            Spacer(modifier = Modifier.heightIn(min = 16.dp))

            Button(
                onClick = {
                    registerGoogle() },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.background),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_google),
                    contentDescription = stringResource(R.string.login_with_google),
                    modifier = Modifier
                        .heightIn(max = 22.dp)
                        .align(Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.sizeIn(12.dp))

                Text(
                    text = stringResource(R.string.register_with_google),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        Spacer(modifier = Modifier.heightIn(min = 16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.login_ask),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge
            )
            TextButton(
                onClick = { navigateToLogin() }
            ) {
                Text(
                    text = stringResource(R.string.login),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
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
