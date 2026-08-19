package rs.nutriapp.ui.screens.recipedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.model.Nutrition
import rs.nutriapp.core.model.Recipe
import rs.nutriapp.core.model.RecipeId
import rs.nutriapp.core.model.RecipeIngredient
import rs.nutriapp.core.model.Substitution
import rs.nutriapp.core.model.scaledTo
import rs.nutriapp.core.model.totalNutrition
import rs.nutriapp.core.model.withIngredientSwapped

data class RecipeDetailUiState(
    val loading: Boolean = true,
    val recipe: Recipe? = null,
    val servings: Int = 1,
    val appliedSwaps: Map<String, RecipeIngredient> = emptyMap(),
    val suggestedSwaps: Map<String, Substitution> = emptyMap(),
) {
    /** Nutritivna vrednost celog (skaliranog) recepta, sa primenjenim zamenama. */
    val totalNutrition: Nutrition get() = recipe?.totalNutrition(servings) ?: Nutrition.Zero
}

/**
 * `servings` je posebno stanje (ne deo `Recipe`) — `scaledTo()` je cist `copy()`, original
 * u repozitorijumu se ne dira dok korisnik "isprobava" druge porcije. Zamene sastojaka su
 * isto lokalne dok se ne resi da li se cuvaju.
 */
class RecipeDetailViewModel(
    private val repository: NutriRepository,
    private val recipeId: RecipeId,
) : ViewModel() {

    private val servingsFlow = MutableStateFlow<Int?>(null)
    private val swapsFlow = MutableStateFlow<Map<String, RecipeIngredient>>(emptyMap())

    val uiState: StateFlow<RecipeDetailUiState> = combine(
        repository.recipes,
        repository.substitutions,
        servingsFlow,
        swapsFlow,
    ) { recipes, substitutions, servingsOverride, swaps ->
        val base = recipes.firstOrNull { it.id == recipeId }
        val servings = servingsOverride ?: base?.servings ?: 1
        val scaled = base?.scaledTo(servings)
        val withSwaps = swaps.entries.fold(scaled) { acc, (from, to) -> acc?.withIngredientSwapped(from, to) }

        val suggestions = base?.ingredients.orEmpty().mapNotNull { ingredient ->
            val match = substitutions.firstOrNull {
                it.fromName.equals(ingredient.name, ignoreCase = true) ||
                    (it.fromProductId != null && it.fromProductId == ingredient.productId)
            }
            match?.let { ingredient.name to it }
        }.toMap()

        RecipeDetailUiState(
            loading = base == null,
            recipe = withSwaps,
            servings = servings,
            appliedSwaps = swaps,
            suggestedSwaps = suggestions,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = RecipeDetailUiState(),
    )

    fun setServings(value: Int) {
        if (value > 0) servingsFlow.value = value
    }

    fun applySwap(ingredientName: String, substitution: Substitution) {
        swapsFlow.update {
            it + (ingredientName to RecipeIngredient(
                productId = substitution.toProductId,
                name = substitution.toName,
                amount = 0.0, // kolicina se preracunava rucno po odnosu; 0 = "vidi opis zamene"
                unit = "",
            ))
        }
    }

    fun revertSwap(ingredientName: String) {
        swapsFlow.update { it - ingredientName }
    }

    fun toggleStarred() = repository.toggleRecipeStarred(recipeId)

    fun toggleSaved() = repository.toggleSavedRecipe(recipeId)
}
