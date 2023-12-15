package com.dietinapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dietinapp.R

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Search(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20))
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(20))
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(10)
            )
            .clickable {  },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            modifier = modifier
                .weight(5f)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            value = query,
            onValueChange = onQueryChange,
            textStyle = MaterialTheme.typography.titleSmall,
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search_placeholder),
                        color = Color.Gray.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                innerTextField()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
            singleLine = true
        )
        Box(
            modifier = modifier
                .weight(1f)
                .size(40.dp)
                .background(color = Color.Transparent, shape = CircleShape),
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}