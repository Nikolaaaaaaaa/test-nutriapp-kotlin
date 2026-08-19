package rs.nutriapp.ui.screens.recipeform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import rs.nutriapp.core.di.nutriViewModel
import rs.nutriapp.core.model.MealSlot
import rs.nutriapp.core.model.RecipeId
import rs.nutriapp.ui.nav.NutriTopBar

@Composable
fun RecipeFormScreen(editingId: RecipeId?, onSaved: (RecipeId) -> Unit, onBack: () -> Unit) {
    val viewModel = nutriViewModel(key = editingId?.raw ?: "new") { RecipeFormViewModel(it.repository, editingId) }
    val liveNutrition by viewModel.liveNutrition.collectAsState()
    var productMenuOpen by remember { mutableStateOf(false) }
    var newIngredientAmount by remember { mutableStateOf("100") }
    var newStepText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            NutriTopBar(
                title = if (editingId == null) "Novi recept" else "Izmena recepta",
                showBack = true,
                onBack = onBack,
            )
        },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                OutlinedTextField(
                    value = viewModel.name.value,
                    onValueChange = { viewModel.name.value = it },
                    label = { Text("Naziv recepta") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
            item {
                OutlinedTextField(
                    value = viewModel.description.value,
                    onValueChange = { viewModel.description.value = it },
                    label = { Text("Opis") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MealSlot.chronological.forEach { slot ->
                        FilterChip(
                            selected = viewModel.mealType.value == slot,
                            onClick = { viewModel.mealType.value = slot },
                            label = { Text(slot.label) },
                        )
                    }
                }
            }

            item {
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Nutritivna vrednost (uživo)", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(
                            "${liveNutrition.calories.rounded} kcal · ${liveNutrition.protein.rounded}g proteina · ${liveNutrition.carbs.rounded}g UH · ${liveNutrition.fat.rounded}g masti",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }

            item { Text("Sastojci", style = MaterialTheme.typography.titleMedium) }
            items(viewModel.ingredients.size) { index ->
                val ingredient = viewModel.ingredients[index]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("${ingredient.name} — ${ingredient.amount} ${ingredient.unit}", style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = { viewModel.removeIngredient(index) }) { Text("Ukloni") }
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.foundation.layout.Box {
                        TextButton(onClick = { productMenuOpen = true }) { Text("+ Dodaj sastojak") }
                        DropdownMenu(expanded = productMenuOpen, onDismissRequest = { productMenuOpen = false }) {
                            viewModel.allProducts.take(30).forEach { product ->
                                DropdownMenuItem(
                                    text = { Text(product.name) },
                                    onClick = {
                                        val amount = newIngredientAmount.toDoubleOrNull() ?: 100.0
                                        viewModel.addIngredient(product, amount, "g")
                                        productMenuOpen = false
                                    },
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = newIngredientAmount,
                        onValueChange = { newIngredientAmount = it },
                        label = { Text("g") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                }
            }

            item { Text("Koraci", style = MaterialTheme.typography.titleMedium) }
            items(viewModel.steps.size) { index ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("${index + 1}. ${viewModel.steps[index]}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    TextButton(onClick = { viewModel.removeStep(index) }) { Text("Ukloni") }
                }
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newStepText,
                        onValueChange = { newStepText = it },
                        label = { Text("Novi korak") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    TextButton(onClick = {
                        viewModel.addStep(newStepText)
                        newStepText = ""
                    }) { Text("Dodaj") }
                }
            }

            item {
                androidx.compose.material3.Button(
                    onClick = { onSaved(viewModel.save()) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = viewModel.name.value.isNotBlank(),
                ) {
                    Text("Sačuvaj recept")
                }
            }
        }
    }
}
