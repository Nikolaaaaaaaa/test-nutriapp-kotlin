package rs.nutriapp.ui.screens.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.model.Product
import rs.nutriapp.core.model.Recipe
import rs.nutriapp.ui.nav.Route

/** Sealed rezultat pretrage — tri razlicita tipa u jednoj listi, bez "tag" polja koje se rucno proverava. */
sealed interface SearchResult {
    data class RecipeHit(val recipe: Recipe) : SearchResult
    data class ProductHit(val product: Product) : SearchResult
    data class ScreenHit(val route: Route, val label: String, val icon: ImageVector) : SearchResult
}

private data class ScreenShortcut(val route: Route, val label: String, val icon: ImageVector)

private val screenShortcuts: List<ScreenShortcut> = listOf(
    ScreenShortcut(Route.Home, "Početna", Icons.Outlined.Home),
    ScreenShortcut(Route.Discovery, "Otkrij", Icons.Outlined.Explore),
    ScreenShortcut(Route.Plan, "Plan", Icons.Outlined.CalendarMonth),
    ScreenShortcut(Route.Grocery, "Lista za kupovinu", Icons.Outlined.ShoppingCart),
    ScreenShortcut(Route.Goals, "Golovi i izazovi", Icons.Outlined.Flag),
    ScreenShortcut(Route.Stats, "Statistika", Icons.Outlined.BarChart),
    ScreenShortcut(Route.Substitutions, "Supstitucije", Icons.Outlined.SwapHoriz),
    ScreenShortcut(Route.Library, "Biblioteka", Icons.Outlined.StarOutline),
    ScreenShortcut(Route.Calculator, "Kalkulator kalorija", Icons.Outlined.Calculate),
    ScreenShortcut(Route.Profile, "Profil", Icons.Outlined.Person),
)

/**
 * Globalna pretraga preko `combine` tri izvora (upit, recepti, proizvodi).
 *
 * Bez `debounce`-a, iz istog razloga kao u `DiscoveryViewModel`: vremenski zasnovani
 * Flow operatori nisu pouzdano okidali na Kotlin/Wasm cilju.
 */
class SearchViewModel(private val repository: NutriRepository) : ViewModel() {

    private val queryFlow = MutableStateFlow("")

    val results: StateFlow<List<SearchResult>> = combine(
        queryFlow,
        repository.recipes,
        repository.products,
    ) { query, recipes, products ->
        if (query.isBlank()) return@combine emptyList()
        val q = query.trim().lowercase()

        val screens = screenShortcuts
            .filter { it.label.lowercase().contains(q) }
            .map { SearchResult.ScreenHit(it.route, it.label, it.icon) }

        val recipeHits = recipes
            .filter { it.name.lowercase().contains(q) || it.tags.any { t -> t.lowercase().contains(q) } }
            .take(8)
            .map { SearchResult.RecipeHit(it) }

        val productHits = products
            .filter { it.name.lowercase().contains(q) }
            .take(8)
            .map { SearchResult.ProductHit(it) }

        screens + recipeHits + productHits
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

    val query: StateFlow<String> = queryFlow

    fun setQuery(text: String) {
        queryFlow.value = text
    }
}
