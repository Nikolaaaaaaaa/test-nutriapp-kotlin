package rs.nutriapp.ui.screens.logging

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import rs.nutriapp.core.model.LoggedMeal
import rs.nutriapp.core.model.QuickAddIssue
import rs.nutriapp.ui.components.EmptyState
import rs.nutriapp.ui.components.PortionStepper
import rs.nutriapp.ui.nav.NutriTopBar

@Composable
fun LoggingScreen(onOpenNotifications: () -> Unit, onOpenMore: () -> Unit) {
    val viewModel = nutriViewModel { LoggingViewModel(it.repository) }
    val state by viewModel.uiState.collectAsState()
    var mealForQuickAdd by remember { mutableStateOf<LoggedMeal?>(null) }

    Scaffold(
        topBar = { NutriTopBar(title = "Logovanje", onNotifications = onOpenNotifications, onMore = onOpenMore) },
    ) { padding ->
        if (state.meals.isEmpty() && !state.loading) {
            EmptyState(icon = Icons.Outlined.EditNote, title = "Nema obroka za danas", modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = 112.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(state.meals, key = { it.id.raw }) { meal ->
                    val issue = state.issues[meal.id]
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(meal.name, style = MaterialTheme.typography.bodyLarge)
                                    if (issue != null) {
                                        Text("!", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                                Text(
                                    "${meal.time} · ${meal.nutrition.calories.rounded} kcal" +
                                        if (meal.quickAdd) " · QuickAdd" else "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            when {
                                issue != null -> TextButton(onClick = { mealForQuickAdd = meal }) { Text("Dopuni") }
                                !meal.logged -> TextButton(onClick = { viewModel.logMeal(meal.id) }) { Text("Odloguj") }
                                else -> Text("Odlogovano", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }

    mealForQuickAdd?.let { meal ->
        QuickAddDialog(
            meal = meal,
            issue = state.issues[meal.id],
            onDismiss = { mealForQuickAdd = null },
            onConfirm = { servings ->
                viewModel.completeQuickAdd(meal.id, servings)
                mealForQuickAdd = null
            },
        )
    }
}

@Composable
private fun QuickAddDialog(
    meal: LoggedMeal,
    issue: QuickAddIssue?,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var servings by remember(meal.id) { mutableStateOf(if (meal.servings > 0) meal.servings else 1) }
    val message = when (issue) {
        is QuickAddIssue.MissingPortionSize -> "Nedostaje broj porcija za \"${meal.name}\"."
        is QuickAddIssue.MissingNutrition -> "Nedostaju nutritivne vrednosti za \"${meal.name}\"."
        is QuickAddIssue.MissingRecipeLink -> "Ovaj brzi unos nije povezan sa receptom."
        null -> "Dopuni unos za \"${meal.name}\"."
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dopuni unos") },
        text = {
            Column {
                Text(message, style = MaterialTheme.typography.bodyMedium)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))
                PortionStepper(value = servings, onValueChange = { servings = it })
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(servings) }) { Text("Sačuvaj") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Otkaži") } },
    )
}
