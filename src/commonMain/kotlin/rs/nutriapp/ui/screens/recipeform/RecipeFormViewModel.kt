package rs.nutriapp.ui.screens.recipeform

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.model.Difficulty
import rs.nutriapp.core.model.MealSlot
import rs.nutriapp.core.model.Minutes
import rs.nutriapp.core.model.Nutrition
import rs.nutriapp.core.model.Product
import rs.nutriapp.core.model.Recipe
import rs.nutriapp.core.model.RecipeId
import rs.nutriapp.core.model.RecipeIngredient
import rs.nutriapp.core.model.sumOfNutrition

data class IngredientDraft(
    val product: Product?,
    val name: String,
    val amount: Double,
    val unit: String,
)

/** Nutritivna vrednost jednog sastojka za trenutnu kolicinu — pretpostavka je da su vrednosti u `Product.nutrition` na 100g/100ml. */
private fun IngredientDraft.estimatedNutrition(): Nutrition {
    val base = product?.nutrition ?: return Nutrition.Zero
    val isWeightBased = unit.contains("g", ignoreCase = true) || unit.contains("ml", ignoreCase = true)
    val factor = if (isWeightBased) amount / 100.0 else amount
    return base * factor
}

/**
 * `mutableStateListOf` (Compose snapshot state) umesto `MutableStateFlow<List<...>>` —
 * lista sastojaka se menja direktno, a `snapshotFlow { }` pretvara te promene u hladan
 * `Flow` koji `stateIn` pretplaćuje. Ovo je most izmedju Compose Snapshot sistema i
 * Kotlin Flow-a — koncept koji React/Vue reaktivnost nema u ovom obliku (najbliže je
 * Vue `watch` nad reaktivnim nizom, ali bez tipiziranog operator lanca kakav Flow nudi).
 */
class RecipeFormViewModel(
    private val repository: NutriRepository,
    private val editingId: RecipeId?,
) : ViewModel() {

    val name = mutableStateOf("")
    val description = mutableStateOf("")
    val mealType = mutableStateOf(MealSlot.RUCAK)
    val difficulty = mutableStateOf(Difficulty.LAKO)
    val servings = mutableStateOf(2)
    val prepTime = mutableStateOf(15)
    val cookTime = mutableStateOf(15)
    val ingredients = mutableStateListOf<IngredientDraft>()
    val steps = mutableStateListOf<String>()

    val liveNutrition: StateFlow<Nutrition> = snapshotFlow { ingredients.toList() }
        .map { list -> list.sumOfNutrition { it.estimatedNutrition() } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Nutrition.Zero)

    val allProducts get() = repository.products.value

    init {
        editingId?.let { id ->
            repository.recipes.value.firstOrNull { it.id == id }?.let(::loadFrom)
        }
    }

    private fun loadFrom(recipe: Recipe) {
        name.value = recipe.name
        description.value = recipe.description
        mealType.value = recipe.mealType
        difficulty.value = recipe.difficulty
        servings.value = recipe.servings
        prepTime.value = recipe.prepTime.value
        cookTime.value = recipe.cookTime.value
        steps.clear()
        steps.addAll(recipe.steps)
        ingredients.clear()
        ingredients.addAll(
            recipe.ingredients.map { ing ->
                val product = ing.productId?.let { pid -> allProducts.firstOrNull { it.id == pid } }
                IngredientDraft(product, ing.name, ing.amount, ing.unit)
            },
        )
    }

    fun addIngredient(product: Product, amount: Double, unit: String) {
        ingredients.add(IngredientDraft(product, product.name, amount, unit))
    }

    fun removeIngredient(index: Int) {
        if (index in ingredients.indices) ingredients.removeAt(index)
    }

    fun addStep(text: String) {
        if (text.isNotBlank()) steps.add(text)
    }

    fun removeStep(index: Int) {
        if (index in steps.indices) steps.removeAt(index)
    }

    fun save(): RecipeId {
        val id = editingId ?: RecipeId("r-custom-${kotlin.random.Random.nextInt(10000, 99999)}")
        val perServing = liveNutrition.value * (1.0 / servings.value.coerceAtLeast(1))
        val recipe = Recipe(
            id = id,
            name = name.value.trim(),
            description = description.value.trim(),
            image = name.value.lowercase().replace(" ", "-"),
            prepTime = Minutes(prepTime.value),
            cookTime = Minutes(cookTime.value),
            difficulty = difficulty.value,
            servings = servings.value,
            mealType = mealType.value,
            ingredients = ingredients.map { RecipeIngredient(it.product?.id, it.name, it.amount, it.unit) },
            nutrition = perServing,
            steps = steps.toList(),
            author = repository.profile.value.displayName,
            createdAt = "danas",
        )
        repository.upsertRecipe(recipe)
        return id
    }
}
