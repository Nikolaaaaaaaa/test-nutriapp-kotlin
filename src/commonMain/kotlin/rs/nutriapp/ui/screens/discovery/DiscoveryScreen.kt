package rs.nutriapp.ui.screens.discovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import rs.nutriapp.core.di.nutriViewModel
import rs.nutriapp.core.model.MealSlot
import rs.nutriapp.core.model.RecipeId
import rs.nutriapp.ui.components.EmptyState
import rs.nutriapp.ui.components.FilterChipRow
import rs.nutriapp.ui.components.RecipeCard
import rs.nutriapp.ui.nav.NutriTopBar

@Composable
fun DiscoveryScreen(
    onOpenRecipe: (RecipeId) -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenMore: () -> Unit,
) {
    val viewModel = nutriViewModel { DiscoveryViewModel(it.repository) }
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    Scaffold(
        topBar = { NutriTopBar(title = "Otkrij", listState = listState, onNotifications = onOpenNotifications, onMore = onOpenMore) },
    ) { padding ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(top = padding.calculateTopPadding(), bottom = 112.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                OutlinedTextField(
                    value = state.filters.query,
                    onValueChange = viewModel::setQuery,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Pretraži recepte i tagove…") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.extraLarge,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                )
            }
            item {
                FilterChipRow(
                    items = MealSlot.chronological,
                    selected = state.filters.mealTypes,
                    onToggle = viewModel::toggleMealType,
                    label = { it.label },
                )
            }
            if (state.availableTags.isNotEmpty()) {
                item {
                    FilterChipRow(
                        items = state.availableTags,
                        selected = state.filters.tags,
                        onToggle = viewModel::toggleTag,
                        label = { it },
                    )
                }
            }
            item {
                Text(
                    "${state.results.size} recepata",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            if (state.results.isEmpty() && !state.loading) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.SearchOff,
                        title = "Nema rezultata",
                        subtitle = "Probaj da promeniš filtere ili obriši pretragu.",
                    )
                }
            }
            items(state.results, key = { it.id.raw }) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onClick = { onOpenRecipe(recipe.id) },
                    onToggleStar = { viewModel.toggleStarred(recipe.id) },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .animateItem(),
                )
            }
        }
    }
}
