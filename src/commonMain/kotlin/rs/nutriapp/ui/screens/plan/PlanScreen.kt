package rs.nutriapp.ui.screens.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import rs.nutriapp.core.di.nutriViewModel
import rs.nutriapp.core.model.MealSlot
import rs.nutriapp.core.model.mealsBySlot
import rs.nutriapp.core.model.totalNutrition
import rs.nutriapp.ui.components.EmptyState
import rs.nutriapp.ui.nav.NutriTopBar

@Composable
fun PlanScreen(onOpenNotifications: () -> Unit, onOpenMore: () -> Unit) {
    val viewModel = nutriViewModel { PlanViewModel(it.repository) }
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { NutriTopBar(title = state.weekLabel.ifBlank { "Plan" }, onNotifications = onOpenNotifications, onMore = onOpenMore) },
    ) { padding ->
        Column(Modifier.padding(top = padding.calculateTopPadding())) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.days, key = { it.date }) { day ->
                    DayPill(
                        label = day.dayShort,
                        isToday = day.isToday,
                        isSelected = day.date == state.selectedDate,
                        onClick = { viewModel.selectDay(day.date) },
                    )
                }
            }

            val selected = state.selectedDay
            if (selected == null) {
                EmptyState(icon = Icons.Outlined.CalendarMonth, title = if (state.loading) "Učitavanje…" else "Nema plana")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Text(
                            "${selected.totalNutrition.calories.rounded} kcal ukupno · ${selected.dayName}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    selected.mealsBySlot().forEach { (slot, meals) ->
                        if (meals.isNotEmpty()) {
                            item(key = "header_${slot.name}") {
                                Text(slot.label, style = MaterialTheme.typography.titleSmall)
                            }
                            items(meals, key = { it.id.raw }) { meal ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Column {
                                            Text(meal.name, style = MaterialTheme.typography.bodyLarge)
                                            Text(
                                                "${meal.time} · ${meal.nutrition.calories.rounded} kcal",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                        if (!meal.logged) {
                                            androidx.compose.material3.TextButton(onClick = { viewModel.logMeal(meal.id) }) {
                                                Text("Odloguj")
                                            }
                                        } else {
                                            Icon(Icons.Outlined.Check, contentDescription = "Odlogovano", tint = MaterialTheme.colorScheme.primary)
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
}

@Composable
private fun DayPill(label: String, isToday: Boolean, isSelected: Boolean, onClick: () -> Unit) {
    val container = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainer
    }
    val content = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = container,
        modifier = Modifier.size(52.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(label, color = content, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
        }
    }
}
