package com.techbeloved.hymnbook.shared.ui.search

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import com.techbeloved.hymnbook.shared.ui.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppSearchBar(
    query: String,
    onSearch: (String) -> Unit,
    onQueryChange: (newQuery: String) -> Unit,
    placeholderText: String,
    modifier: Modifier = Modifier,
    maxChar: Int = 50,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Column(modifier = modifier) {
        AppTopBar(
            modifier = Modifier.fillMaxWidth(),
            titleContent = {
                val interactionSource = remember { MutableInteractionSource() }
                BasicTextField(
                    modifier = Modifier.fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = query,
                    onValueChange = { if (query.length < maxChar) onQueryChange(it) },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search,
                    ),
                    keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
                    maxLines = 1,
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        TextFieldDefaults.DecorationBox(
                            innerTextField = innerTextField,
                            value = query,
                            enabled = true,
                            singleLine = true,
                            placeholder = {
                                Text(text = placeholderText)
                            },
                            visualTransformation = VisualTransformation.None,
                            interactionSource = interactionSource,
                            container = { },
                        )
                    },
                )
            },
            actions = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(imageVector = Icons.Rounded.Clear, contentDescription = "Clear search")
                    }
                }
            }
        )
        HorizontalDivider(
            thickness = Dp.Hairline,
            modifier = Modifier.fillMaxWidth(),
        )
    }

}
