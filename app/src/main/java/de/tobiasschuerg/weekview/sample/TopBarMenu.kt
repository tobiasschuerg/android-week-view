package de.tobiasschuerg.weekview.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/** Top bar text button that opens a dropdown; [items] get a `dismiss` callback to close it after a pick. */
@Composable
fun TopBarMenu(
    label: String,
    items: @Composable ColumnScope.(dismiss: () -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    // Box anchors the dropdown below its own button rather than the end of the bar.
    Box {
        TextButton(onClick = { expanded = true }) {
            Text(label, color = MaterialTheme.colorScheme.onPrimary)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items { expanded = false }
        }
    }
}
