package rs.nutriapp.ui.screens.newproduct

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import rs.nutriapp.core.di.nutriViewModel
import rs.nutriapp.core.model.ProductId
import rs.nutriapp.ui.nav.NutriTopBar

@Composable
fun NewProductScreen(onSaved: (ProductId) -> Unit, onBack: () -> Unit) {
    val viewModel = nutriViewModel { NewProductViewModel(it.repository) }

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var categoryMenuOpen by remember { mutableStateOf(false) }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { NutriTopBar(title = "Nova namirnica", showBack = true, onBack = onBack) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Naziv namirnice") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Column {
                TextButton(onClick = { categoryMenuOpen = true }) {
                    Text(category.ifBlank { "Izaberi kategoriju" })
                }
                DropdownMenu(expanded = categoryMenuOpen, onDismissRequest = { categoryMenuOpen = false }) {
                    viewModel.categories.forEach { c ->
                        DropdownMenuItem(text = { Text(c.name) }, onClick = { category = c.name; categoryMenuOpen = false })
                    }
                }
            }
            Text("Nutritivne vrednosti na 100 g", style = MaterialTheme.typography.titleSmall)
            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text("Kalorije (kcal)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = protein,
                    onValueChange = { protein = it },
                    label = { Text("Proteini g") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = carbs,
                    onValueChange = { carbs = it },
                    label = { Text("UH g") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = fat,
                    onValueChange = { fat = it },
                    label = { Text("Masti g") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
            }
            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Button(
                onClick = {
                    val result = viewModel.validate(
                        ProductFormInput(name, category, calories, protein, carbs, fat),
                    )
                    when (result) {
                        is ProductFormResult.Valid -> {
                            viewModel.save(result.product)
                            onSaved(result.product.id)
                        }
                        is ProductFormResult.Invalid -> error = result.message
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Sačuvaj namirnicu")
            }
        }
    }
}
