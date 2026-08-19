package rs.nutriapp.ui.screens.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import rs.nutriapp.core.di.nutriViewModel
import rs.nutriapp.core.model.RecipeId
import rs.nutriapp.ui.components.EmptyState
import rs.nutriapp.ui.components.RecipeCard
import rs.nutriapp.ui.nav.NutriTopBar

private enum class LibraryTab(val label: String) { RECIPES("Recepti"), PRODUCTS("Proizvodi"), SUBSTITUTIONS("Zamene") }

@Composable
fun LibraryScreen(onOpenRecipe: (RecipeId) -> Unit, onOpenMore: () -> Unit) {
    val viewModel = nutriViewModel { LibraryViewModel(it.repository) }
    val state by viewModel.uiState.collectAsState()
    var tab by remember { mutableStateOf(LibraryTab.RECIPES) }

    Scaffold(
        topBar = { NutriTopBar(title = "Biblioteka", onMore = onOpenMore) },
    ) { padding ->
        Column(Modifier.padding(top = padding.calculateTopPadding())) {
            TabRow(selectedTabIndex = tab.ordinal) {
                LibraryTab.entries.forEach { entry ->
                    Tab(selected = tab == entry, onClick = { tab = entry }, text = { Text(entry.label) })
                }
            }
            when (tab) {
                LibraryTab.RECIPES -> {
                    if (state.savedRecipes.isEmpty()) {
                        EmptyState(icon = Icons.Outlined.StarOutline, title = "Nema sačuvanih recepata")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 112.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(state.savedRecipes, key = { it.id.raw }) { recipe ->
                                RecipeCard(
                                    recipe = recipe,
                                    onClick = { onOpenRecipe(recipe.id) },
                                    onToggleStar = { viewModel.toggleRecipeStarred(recipe.id) },
                                )
                            }
                        }
                    }
                }
                LibraryTab.PRODUCTS -> {
                    if (state.savedProducts.isEmpty()) {
                        EmptyState(icon = Icons.Outlined.ShoppingBag, title = "Nema sačuvanih proizvoda")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 112.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(state.savedProducts, key = { it.id.raw }) { product ->
                                Card(
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Row(modifier = Modifier.padding(14.dp).fillMaxWidth()) {
                                        Text(product.name, style = MaterialTheme.typography.bodyLarge)
                                    }
                                }
                            }
                        }
                    }
                }
                LibraryTab.SUBSTITUTIONS -> {
                    if (state.starredSubstitutions.isEmpty()) {
                        EmptyState(icon = Icons.Outlined.SwapHoriz, title = "Nema omiljenih zamena")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 112.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(state.starredSubstitutions, key = { it.id.raw }) { sub ->
                                Card(
                                    shape = MaterialTheme.shapes.medium,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
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
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
