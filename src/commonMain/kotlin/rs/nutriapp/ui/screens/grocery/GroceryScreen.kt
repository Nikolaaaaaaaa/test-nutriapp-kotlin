package rs.nutriapp.ui.screens.grocery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import rs.nutriapp.core.di.nutriViewModel
import rs.nutriapp.core.model.GroceryItem
import rs.nutriapp.core.model.GrocerySection
import rs.nutriapp.core.model.checkedCount
import rs.nutriapp.core.model.sectionTotal
import rs.nutriapp.ui.components.EmptyState
import rs.nutriapp.ui.nav.NutriTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryScreen(onOpenNotifications: () -> Unit, onOpenMore: () -> Unit) {
    val viewModel = nutriViewModel { GroceryViewModel(it.repository) }
    val state by viewModel.uiState.collectAsState()
    val list = state.list

    Scaffold(
        topBar = { NutriTopBar(title = "Lista za kupovinu", onNotifications = onOpenNotifications, onMore = onOpenMore) },
    ) { padding ->
        if (list == null || list.sections.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.ShoppingCart,
                title = if (state.loading) "Učitavanje…" else "Lista je prazna",
                modifier = Modifier.padding(padding),
            )
            return@Scaffold
        }

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
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Ukupno: ${state.liveTotal.format()}", style = MaterialTheme.typography.titleMedium)
                        state.liveByStore.forEach { store ->
                            Text(
                                "${store.storeName}: ${store.total.format()} (${store.itemCount} art.)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }
            }

            list.sections.forEach { section ->
                item(key = "section_${section.id.raw}") {
                    SectionHeader(
                        section = section,
                        onClearRare = { viewModel.clearRareSection(section.id) },
                    )
                }
                items(section.items, key = { it.id.raw }) { item ->
                    GroceryItemRow(
                        item = item,
                        stores = viewModel.stores,
                        onCheckedChange = { viewModel.setChecked(item.id, it) },
                        onRemove = { viewModel.removeItem(item.id) },
                        onStoreChange = { storeId -> viewModel.setStore(item.id, storeId) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(section: GrocerySection, onClearRare: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(section.name, style = MaterialTheme.typography.titleSmall)
            Text(
                "${section.checkedCount}/${section.items.size} · ${section.sectionTotal.format()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (section.rare && section.items.isNotEmpty()) {
            TextButton(onClick = onClearRare) { Text("Ukloni sve") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroceryItemRow(
    item: GroceryItem,
    stores: List<rs.nutriapp.core.model.Store>,
    onCheckedChange: (Boolean) -> Unit,
    onRemove: () -> Unit,
    onStoreChange: (rs.nutriapp.core.model.StoreId) -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onRemove()
                true
            } else {
                false
            }
        },
    )
    var storeMenuOpen by remember { mutableStateOf(false) }
    val currentStore = stores.firstOrNull { it.id == item.storeId }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Obriši",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(horizontal = 20.dp),
                )
            }
        },
    ) {
        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(10.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(checked = item.checked, onCheckedChange = onCheckedChange)
                Column(Modifier.weight(1f)) {
                    Text(
                        "${item.name} · ${item.amount} ${item.unit}",
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = if (item.checked) TextDecoration.LineThrough else null,
                        color = if (item.checked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    )
                    Box {
                        TextButton(onClick = { storeMenuOpen = true }, contentPadding = PaddingValues(0.dp)) {
                            Text(
                                "${currentStore?.name ?: item.storeId.raw} · ${item.price.format()}",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        DropdownMenu(expanded = storeMenuOpen, onDismissRequest = { storeMenuOpen = false }) {
                            stores.forEach { store ->
                                DropdownMenuItem(
                                    text = { Text(store.name) },
                                    onClick = {
                                        onStoreChange(store.id)
                                        storeMenuOpen = false
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
