package com.techbeloved.hymnbook.shared.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun AppDialog(
    title: String,
    content: String,
    positiveText: String,
    negativeText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onConfirm,
            ) {
                Text(text = positiveText)
            }
        },
        dismissButton = {
            FilledTonalButton(
                onClick = onDismiss,
            ) {
                Text(text = negativeText)
            }
        },
        shape = MaterialTheme.shapes.medium,
        title = { Text(text = title) },
        text = { Text(text = content) },
        modifier = modifier,
    )
}
