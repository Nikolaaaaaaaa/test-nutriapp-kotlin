package rs.nutriapp.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import rs.nutriapp.core.di.nutriViewModel
import rs.nutriapp.ui.components.EmptyState
import rs.nutriapp.ui.nav.Route

/**
 * Globalna pretraga — dostupna sa svakog ekrana preko dugmeta u top bar-u i preko `Ctrl+K`
 * (hvatanje precice je u `App.kt`, na root nivou). React verzija ovo radi kao overlay
 * sa tabovima; ovde je jedna lista sa tri tipa rezultata (ekrani, recepti, proizvodi).
 */
@Composable
fun SearchOverlay(visible: Boolean, onDismiss: () -> Unit, onNavigate: (Route) -> Unit) {
    val viewModel = nutriViewModel { SearchViewModel(it.repository) }
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val focusRequester = remember { FocusRequester() }

    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(Modifier.statusBarsPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zatvori pretragu")
                    }
                    OutlinedTextField(
                        value = query,
                        onValueChange = viewModel::setQuery,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        placeholder = { Text("Pretraži recepte, proizvode, ekrane… (Ctrl+K)") },
                        singleLine = true,
                        shape = MaterialTheme.shapes.extraLarge,
                    )
                }

                if (query.isBlank()) {
                    EmptyState(icon = Icons.Outlined.Search, title = "Otkucaj da pretražiš", subtitle = "Recepti, proizvodi i ekrani aplikacije")
                } else if (results.isEmpty()) {
                    EmptyState(icon = Icons.Outlined.SearchOff, title = "Nema rezultata za \"$query\"")
                } else {
                    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                        items(results) { result ->
                            SearchResultRow(result) {
                                when (result) {
                                    is SearchResult.RecipeHit -> onNavigate(Route.RecipeDetail(result.recipe.id))
                                    is SearchResult.ProductHit -> Unit
                                    is SearchResult.ScreenHit -> onNavigate(result.route)
                                }
                                onDismiss()
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(visible) {
        if (!visible) viewModel.setQuery("")
    }
}

@Composable
private fun SearchResultRow(result: SearchResult, onClick: () -> Unit) {
    val (icon, label, sub) = when (result) {
        is SearchResult.RecipeHit -> Triple(Icons.Outlined.RestaurantMenu, result.recipe.name, "Recept")
        is SearchResult.ProductHit -> Triple(Icons.Outlined.ShoppingCart, result.product.name, "Proizvod")
        is SearchResult.ScreenHit -> Triple(result.icon, result.label, "Ekran")
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Column {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
