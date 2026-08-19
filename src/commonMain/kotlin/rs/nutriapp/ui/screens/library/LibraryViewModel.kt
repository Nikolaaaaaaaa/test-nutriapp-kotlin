package rs.nutriapp.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.model.Product
import rs.nutriapp.core.model.Recipe
import rs.nutriapp.core.model.RecipeId
import rs.nutriapp.core.model.Substitution

data class LibraryUiState(
    val loading: Boolean = true,
    val savedRecipes: List<Recipe> = emptyList(),
    val savedProducts: List<Product> = emptyList(),
    val starredSubstitutions: List<Substitution> = emptyList(),
)

/** Tri liste izvedene iz profila (sacuvani id-jevi) preseceni sa katalozima — jedan `combine`. */
class LibraryViewModel(private val repository: NutriRepository) : ViewModel() {

    val uiState: StateFlow<LibraryUiState> = combine(
        repository.recipes,
        repository.products,
        repository.substitutions,
        repository.profile,
    ) { recipes, products, substitutions, profile ->
        LibraryUiState(
            loading = false,
            savedRecipes = recipes.filter { it.starred || it.id in profile.savedRecipeIds },
            savedProducts = products.filter { it.id in profile.savedProductIds },
            starredSubstitutions = substitutions.filter { it.starred },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LibraryUiState(),
    )

    fun toggleRecipeStarred(id: RecipeId) = repository.toggleRecipeStarred(id)
}
