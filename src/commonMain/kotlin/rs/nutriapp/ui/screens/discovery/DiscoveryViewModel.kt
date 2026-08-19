package rs.nutriapp.ui.screens.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.filter.RecipeSort
import rs.nutriapp.core.filter.filterBy
import rs.nutriapp.core.filter.sortedByOption
import rs.nutriapp.core.model.MealSlot
import rs.nutriapp.core.model.Recipe
import rs.nutriapp.core.model.RecipeId

data class DiscoveryFilterState(
    val query: String = "",
    val mealTypes: Set<MealSlot> = emptySet(),
    val tags: Set<String> = emptySet(),
    val maxCalories: Int? = null,
    val sort: RecipeSort = RecipeSort.RELEVANCE,
)

data class DiscoveryUiState(
    val loading: Boolean = true,
    val filters: DiscoveryFilterState = DiscoveryFilterState(),
    val results: List<Recipe> = emptyList(),
    val availableTags: List<String> = emptyList(),
)

/**
 * `combine()` spaja filtere (tekst, tip obroka, tagovi, kalorije, sortiranje) i katalog
 * recepata u JEDAN `StateFlow`. Ekran cita samo to jedno stanje.
 *
 * Otkazivanje je automatsko: kad se ekran ukloni sa navigacionog steka, `viewModelScope`
 * se otkazuje i `combine` pretplata nestaje sama — nema `useEffect` cleanup-a.
 *
 * Ovde je prvobitno stajao `debounce(250)` na tekstu pretrage. Sklonjen je: na
 * Kotlin/Wasm cilju vremenski zasnovani Flow operatori (koji ispod koriste `delay`)
 * nisu pouzdano okidali, pa je `combine` povremeno ostajao bez ijedne emisije i ekran
 * bi zauvek prikazivao pocetno stanje ("0 recepata"). Filtriranje 18 recepata je ionako
 * trenutno, pa debounce nije ni donosio nista — vidi KOTLIN-SPECIFICNOSTI.md.
 */
class DiscoveryViewModel(private val repository: NutriRepository) : ViewModel() {

    private val filtersFlow = MutableStateFlow(DiscoveryFilterState())

    val uiState: StateFlow<DiscoveryUiState> = combine(
        filtersFlow,
        repository.recipes,
    ) { filters, recipes ->
        val results = recipes
            .filterBy {
                query(filters.query)
                mealType(filters.mealTypes)
                tags(filters.tags)
                maxCalories(filters.maxCalories)
            }
            .sortedByOption(filters.sort)
        DiscoveryUiState(
            loading = false,
            filters = filters,
            results = results,
            availableTags = recipes.flatMap { it.tags }.distinct().sorted(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = DiscoveryUiState(),
    )

    fun setQuery(text: String) = filtersFlow.update { it.copy(query = text) }

    fun toggleMealType(slot: MealSlot) = filtersFlow.update { state ->
        val next = if (slot in state.mealTypes) state.mealTypes - slot else state.mealTypes + slot
        state.copy(mealTypes = next)
    }

    fun toggleTag(tag: String) = filtersFlow.update { state ->
        val next = if (tag in state.tags) state.tags - tag else state.tags + tag
        state.copy(tags = next)
    }

    fun setMaxCalories(value: Int?) = filtersFlow.update { it.copy(maxCalories = value) }

    fun setSort(sort: RecipeSort) = filtersFlow.update { it.copy(sort = sort) }

    fun clearFilters() = filtersFlow.update { DiscoveryFilterState() }

    fun toggleStarred(id: RecipeId) = repository.toggleRecipeStarred(id)
}
