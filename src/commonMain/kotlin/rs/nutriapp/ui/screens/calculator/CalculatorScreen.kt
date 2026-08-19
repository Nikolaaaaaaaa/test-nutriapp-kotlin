package rs.nutriapp.ui.screens.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import rs.nutriapp.core.di.nutriViewModel
import rs.nutriapp.core.model.ActivityLevel
import rs.nutriapp.core.model.CalorieResult
import rs.nutriapp.core.model.Gender
import rs.nutriapp.ui.nav.NutriTopBar

@Composable
fun CalculatorScreen(onOpenMore: () -> Unit) {
    val viewModel = nutriViewModel { CalculatorViewModel(it.repository) }
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { NutriTopBar(title = "Kalkulator kalorija", onMore = onOpenMore) },
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = 112.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = if (state.input.weightKg == 0.0) "" else "${state.input.weightKg}",
                        onValueChange = { text -> text.toDoubleOrNull()?.let(viewModel::setWeight) },
                        label = { Text("Kg") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = if (state.input.heightCm == 0.0) "" else "${state.input.heightCm.toInt()}",
                        onValueChange = { text -> text.toDoubleOrNull()?.let(viewModel::setHeight) },
                        label = { Text("Cm") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = if (state.input.age == 0) "" else "${state.input.age}",
                        onValueChange = { text -> text.toIntOrNull()?.let(viewModel::setAge) },
                        label = { Text("Godine") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Gender.entries.forEach { g ->
                        FilterChip(
                            selected = state.input.gender == g,
                            onClick = { viewModel.setGender(g) },
                            label = { Text(g.label) },
                        )
                    }
                }
            }
            item {
                Text("Nivo aktivnosti", style = MaterialTheme.typography.titleSmall)
            }
            items(ActivityLevel.entries) { level ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                ) {
                    FilterChip(
                        selected = state.input.activityLevel == level,
                        onClick = { viewModel.setActivityLevel(level) },
                        label = { Text("${level.label} — ${level.description}") },
                    )
                }
            }
            items(state.results) { result -> FormulaResultCard(result) }
        }
    }
}

@Composable
private fun FormulaResultCard(result: CalorieResult) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(result.formula.label, style = MaterialTheme.typography.titleSmall)
            Text(result.formula.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 6.dp))
            Text("BMR: ${result.bmr.rounded} kcal · TDEE: ${result.tdee.rounded} kcal", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
