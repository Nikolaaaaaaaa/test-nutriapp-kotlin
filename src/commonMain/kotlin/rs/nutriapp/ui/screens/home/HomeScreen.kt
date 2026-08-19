package rs.nutriapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import rs.nutriapp.core.di.nutriViewModel
import rs.nutriapp.core.model.MealSlot
import rs.nutriapp.core.model.Nutrient
import rs.nutriapp.ui.components.MacroRing
import rs.nutriapp.ui.components.NutrientBar
import rs.nutriapp.ui.components.icon
import rs.nutriapp.ui.nav.NutriTopBar
import rs.nutriapp.ui.theme.StatusColors

@Composable
fun HomeScreen(
    onOpenSearch: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenMore: () -> Unit,
    onOpenRecipe: (rs.nutriapp.core.model.RecipeId) -> Unit,
) {
    val viewModel = nutriViewModel { HomeViewModel(it.repository) }
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            NutriTopBar(
                title = if (state.greetingName.isNotBlank()) "Zdravo, ${state.greetingName}" else "NutriApp",
                listState = listState,
                onSearch = onOpenSearch,
                onNotifications = onOpenNotifications,
                onMore = onOpenMore,
                unreadNotifications = state.unreadNotifications,
            )
        },
    ) { padding ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(bottom = 112.dp, top = padding.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                DailySummaryCard(state, modifier = Modifier.padding(horizontal = 16.dp))
            }
            state.nextMeal?.let { meal ->
                item {
                    NextMealCard(
                        mealName = meal.name,
                        slot = meal.slot,
                        calories = meal.nutrition.calories.rounded,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
            state.activeChallenge?.let { challenge ->
                item {
                    ActiveChallengeCard(
                        title = challenge.title,
                        progress = challenge.progress,
                        target = challenge.target,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
            item {
                Text(
                    "Obroci danas",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
            items(state.meals, key = { it.id.raw }) { meal ->
                MealRow(meal, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
private fun DailySummaryCard(state: HomeUiState, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        // Sve boje unutar kartice izvedene su iz njene pozadine: podrazumevane
        // `surface`/`onSurfaceVariant` boje bi se izgubile na obojenoj podlozi.
        val onCard = MaterialTheme.colorScheme.onPrimaryContainer
        val track = onCard.copy(alpha = 0.22f)

        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                MacroRing(
                    progress = state.calorieFraction,
                    color = onCard,
                    trackColor = track,
                    centerLabel = "${state.intake.calories.rounded}",
                    centerSubLabel = "/ ${state.dailyGoals?.calories?.rounded ?: 0} kcal",
                    centerLabelColor = onCard,
                    centerSubLabelColor = onCard.copy(alpha = 0.75f),
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    NutrientBar(
                        label = "Proteini",
                        valueLabel = "${state.intake.protein.rounded} / ${state.dailyGoals?.protein?.rounded ?: 0} g",
                        fraction = state.proteinFraction,
                        color = StatusColors.good,
                        labelColor = onCard,
                        valueColor = onCard.copy(alpha = 0.75f),
                        trackColor = track,
                    )
                    NutrientBar(
                        label = "Ugljeni hidrati",
                        valueLabel = "${state.intake.carbs.rounded} / ${state.dailyGoals?.carbs?.rounded ?: 0} g",
                        fraction = state.carbsFraction,
                        color = StatusColors.info,
                        labelColor = onCard,
                        valueColor = onCard.copy(alpha = 0.75f),
                        trackColor = track,
                    )
                }
            }
            if (state.streakDays > 0) {
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 14.dp))
                Text(
                    "${state.streakDays} dana niz — nastavi tako",
                    style = MaterialTheme.typography.labelLarge,
                    color = onCard.copy(alpha = 0.85f),
                )
            }
        }
    }
}

@Composable
private fun NextMealCard(mealName: String, slot: MealSlot, calories: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(slot.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.fillMaxWidth()) {
                Text("Sledeći: ${slot.label}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(mealName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text("$calories kcal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ActiveChallengeCard(title: String, progress: Int, target: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
    ) {
        val onCard = MaterialTheme.colorScheme.onTertiaryContainer

        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, color = onCard)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
            NutrientBar(
                label = "Napredak",
                valueLabel = "$progress / $target",
                fraction = if (target > 0) progress.toFloat() / target else 0f,
                color = onCard,
                labelColor = onCard,
                valueColor = onCard.copy(alpha = 0.75f),
                trackColor = onCard.copy(alpha = 0.22f),
            )
        }
    }
}

@Composable
private fun MealRow(meal: rs.nutriapp.core.model.LoggedMeal, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(meal.slot.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(meal.name, style = MaterialTheme.typography.bodyLarge)
                    if (!meal.complete) {
                        Text("!", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium)
                    }
                }
                Text(
                    "${meal.time} · ${meal.nutrition.calories.rounded} kcal" + if (meal.logged) "" else " · nije odlogovano",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
