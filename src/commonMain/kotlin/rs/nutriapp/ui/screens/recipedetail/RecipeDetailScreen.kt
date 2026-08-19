package rs.nutriapp.ui.screens.recipedetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.nutriapp.core.di.nutriViewModel
import rs.nutriapp.core.model.RecipeId
import rs.nutriapp.core.model.totalTime
import rs.nutriapp.ui.components.EmptyState
import rs.nutriapp.ui.components.NutrientBar
import rs.nutriapp.ui.components.PlaceholderImage
import rs.nutriapp.ui.components.PortionStepper
import rs.nutriapp.ui.nav.NutriTopBar
import rs.nutriapp.ui.theme.StatusColors

@Composable
fun RecipeDetailScreen(recipeId: RecipeId, onBack: () -> Unit) {
    val viewModel = nutriViewModel { RecipeDetailViewModel(it.repository, recipeId) }
    val state by viewModel.uiState.collectAsState()
    val recipe = state.recipe

    Scaffold(
        topBar = {
            NutriTopBar(
                title = recipe?.name ?: "Recept",
                showBack = true,
                onBack = onBack,
            )
        },
    ) { padding ->
        if (recipe == null) {
            EmptyState(
                icon = Icons.Outlined.RestaurantMenu,
                title = if (state.loading) "Učitavanje…" else "Recept nije pronađen",
                modifier = Modifier.padding(padding),
            )
            return@Scaffold
        }

        LazyColumn(
            contentPadding = PaddingValues(top = padding.calculateTopPadding(), bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                PlaceholderImage(
                    seed = recipe.name,
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                    cornerRadius = 0.dp,
                    textSize = 56.sp,
                )
            }
            item {
                Column(Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(recipe.name, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
                        IconButton(onClick = viewModel::toggleStarred) {
                            Icon(
                                if (recipe.starred) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = "Omiljeno",
                                tint = if (recipe.starred) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Text(
                        recipe.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = {},
                            label = { Text("${recipe.totalTime.value} min") },
                            leadingIcon = { Icon(Icons.Outlined.Schedule, contentDescription = null) },
                        )
                        AssistChip(onClick = {}, label = { Text(recipe.difficulty.label) })
                        AssistChip(
                            onClick = {},
                            label = { Text("${recipe.rating}") },
                            leadingIcon = { Icon(Icons.Filled.Star, contentDescription = null) },
                        )
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("Porcije", style = MaterialTheme.typography.titleSmall)
                            PortionStepper(value = state.servings, onValueChange = viewModel::setServings)
                        }
                        androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))
                        NutrientBar(
                            "Kalorije (ukupno)",
                            "${state.totalNutrition.calories.rounded} kcal",
                            1f,
                            StatusColors.good,
                        )
                    }
                }
            }
            item {
                Text(
                    "Sastojci",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
            items(recipe.ingredients) { ingredient ->
                Column(Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(ingredient.name, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            ingredient.formatAmount(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    val suggestion = state.suggestedSwaps[ingredient.name]
                    val isSwapped = ingredient.name in state.appliedSwaps
                    if (suggestion != null && !isSwapped) {
                        TextButton(onClick = { viewModel.applySwap(ingredient.name, suggestion) }) {
                            Text("Zameni sa: ${suggestion.toName} (${suggestion.ratio})")
                        }
                    }
                }
            }
            item {
                Text(
                    "Koraci",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
            itemsIndexed(recipe.steps) { index, step ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        "${index + 1}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(step, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
