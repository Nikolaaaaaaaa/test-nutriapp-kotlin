package rs.nutriapp.ui.screens.substitutions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import rs.nutriapp.core.di.nutriViewModel
import rs.nutriapp.core.model.Substitution
import rs.nutriapp.core.model.isCalorieReduction
import rs.nutriapp.ui.components.EmptyState
import rs.nutriapp.ui.nav.NutriTopBar

@Composable
fun SubstitutionsScreen(onOpenMore: () -> Unit) {
    val viewModel = nutriViewModel { SubstitutionsViewModel(it.repository) }
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { NutriTopBar(title = "Supstitucije", onMore = onOpenMore) },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = viewModel::setQuery,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Pretraži zamene…") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.extraLarge,
                )
            }
            if (state.grouped.isEmpty() && !state.loading) {
                item { EmptyState(icon = Icons.Outlined.SwapHoriz, title = "Nema rezultata") }
            }
            state.grouped.forEach { (category, items) ->
                item(key = "cat_$category") {
                    Text(category, style = MaterialTheme.typography.titleSmall)
                }
                items(items, key = { it.id.raw }) { sub ->
                    SubstitutionRow(sub, onToggleStar = { viewModel.toggleStarred(sub.id) })
                }
            }
        }
    }
}

@Composable
private fun SubstitutionRow(sub: Substitution, onToggleStar: () -> Unit) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(sub.fromName, style = MaterialTheme.typography.bodyLarge)
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "zameniti sa",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(sub.toName, style = MaterialTheme.typography.bodyLarge)
                }
                Text(
                    "${sub.ratio} · ${sub.reason}" + if (sub.isCalorieReduction) " · manje kalorija" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onToggleStar) {
                Icon(
                    if (sub.starred) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = "Omiljeno",
                    tint = if (sub.starred) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
