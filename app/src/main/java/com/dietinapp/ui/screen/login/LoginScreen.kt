package com.dietinapp.ui.screen.login

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.dietinapp.R

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navigateToRegister: () -> Unit,
    loginGoogle: () -> Unit,
    loginCustom: () -> Unit,
) {
    val context = LocalContext.current

    val state = rememberScrollState()
    LaunchedEffect(Unit) { state.animateScrollTo(100) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isEmailError by rememberSaveable { mutableStateOf(false) }
    var isPasswordError by rememberSaveable { mutableStateOf(false) }

    var emailErrorMessage by rememberSaveable { mutableStateOf("") }
    var passwordErrorMessage by rememberSaveable { mutableStateOf("") }

    var isPasswordHidden by rememberSaveable { mutableStateOf(true) }
    val visibilityIcon =
        if (isPasswordHidden) R.drawable.ic_visibility else R.drawable.ic_visibility_off
    val description =
        if (isPasswordHidden) stringResource(R.string.show_password) else stringResource(R.string.hide_password)

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
        if (password.isEmpty()) {
            isPasswordError = true
            passwordErrorMessage = context.getString(R.string.empty_password_warning)
        } else {
            isPasswordError = false
        }
    }

    Column(
        Modifier
            .padding(16.dp)
            .verticalScroll(state)
            .fillMaxSize(),
    ) {

        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.app_name),
            modifier = Modifier
                .align(Alignment.Start)
                .widthIn(min = 52.dp)
                .heightIn(min = 48.dp)
        )

        Spacer(modifier = Modifier.heightIn(min = 16.dp))

        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(R.string.login_detail),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.heightIn(min = 32.dp))

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
                errorContainerColor = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
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
                errorContainerColor = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )

        Spacer(modifier = Modifier.heightIn(min = 32.dp))

        Column {
            Button(
                onClick = { loginCustom() },
                enabled = !isEmailError && !isPasswordError
                        && email.isNotEmpty() && password.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = stringResource(R.string.login),
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
                        .heightIn(min = 2.dp)
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
                    loginGoogle()
                },
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
                    text = stringResource(R.string.login_with_google),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Spacer(modifier = Modifier.heightIn(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = stringResource(R.string.register_ask),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge
                )
                TextButton(
                    onClick = {
                        navigateToRegister()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.register),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

    }
}
