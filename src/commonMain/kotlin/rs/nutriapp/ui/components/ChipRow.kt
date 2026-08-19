package rs.nutriapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Vodoravna, skrolabilna lista cipova za filtriranje — koristi se u Discovery i Supstitucijama. */
@Composable
fun <T> FilterChipRow(
    items: List<T>,
    selected: Set<T>,
    onToggle: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items) { item ->
            val isSelected = item in selected
            FilterChip(
                selected = isSelected,
                onClick = { onToggle(item) },
                label = { Text(label(item), style = MaterialTheme.typography.labelLarge) },
            )
        }
    }
}
