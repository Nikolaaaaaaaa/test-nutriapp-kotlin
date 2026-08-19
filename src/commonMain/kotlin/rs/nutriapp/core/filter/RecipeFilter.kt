package rs.nutriapp.core.filter

import rs.nutriapp.core.model.Difficulty
import rs.nutriapp.core.model.MealSlot
import rs.nutriapp.core.model.Recipe
import rs.nutriapp.core.model.totalTime

enum class RecipeSort(val label: String) {
    RELEVANCE("Relevantnost"),
    CALORIES_ASC("Kalorije: rastuće"),
    TIME_ASC("Vreme: najbrže"),
    RATING_DESC("Ocena: najbolje"),
    NEWEST("Najnovije"),
}

/**
 * Type-safe DSL za filtriranje recepata — receiver lambda umesto niza pojedinacnih
 * bool/string parametara. Poziv na mestu upotrebe citi se kao mini-jezik:
 *
 * ```
 * recipes.filterBy {
 *     query(searchText)
 *     mealType(selected)
 *     maxCalories(500)
 * }
 * ```
 *
 * Ekvivalent u React/Vue bi bio niz uzastopnih `.filter()` poziva ili objekat opcija bez
 * provere u kompajl vremenu da li su polja smislena.
 */
class RecipeFilterScope internal constructor() {
    private var query: String = ""
    private var mealTypes: Set<MealSlot> = emptySet()
    private var tags: Set<String> = emptySet()
    private var maxCalories: Int? = null
    private var maxTimeMinutes: Int? = null
    private var maxDifficulty: Difficulty? = null

    fun query(text: String) {
        query = text
    }

    fun mealType(slots: Set<MealSlot>) {
        mealTypes = slots
    }

    fun tags(selected: Set<String>) {
        tags = selected
    }

    fun maxCalories(value: Int?) {
        maxCalories = value
    }

    fun maxTime(minutes: Int?) {
        maxTimeMinutes = minutes
    }

    fun maxDifficulty(difficulty: Difficulty?) {
        maxDifficulty = difficulty
    }

    internal fun matches(recipe: Recipe): Boolean {
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            val inName = recipe.name.lowercase().contains(q)
            val inTags = recipe.tags.any { it.lowercase().contains(q) }
            if (!inName && !inTags) return false
        }
        if (mealTypes.isNotEmpty() && recipe.mealType !in mealTypes) return false
        if (tags.isNotEmpty() && !tags.all { wanted -> recipe.tags.any { it.equals(wanted, ignoreCase = true) } }) return false
        if (maxCalories != null && recipe.nutrition.calories.value > maxCalories!!) return false
        if (maxTimeMinutes != null && recipe.totalTime.value > maxTimeMinutes!!) return false
        if (maxDifficulty != null && recipe.difficulty.level > maxDifficulty!!.level) return false
        return true
    }
}

fun List<Recipe>.filterBy(block: RecipeFilterScope.() -> Unit): List<Recipe> {
    val scope = RecipeFilterScope().apply(block)
    return filter(scope::matches)
}

fun List<Recipe>.sortedByOption(sort: RecipeSort): List<Recipe> = when (sort) {
    RecipeSort.RELEVANCE -> sortedByDescending { it.saveCount + it.logCount }
    RecipeSort.CALORIES_ASC -> sortedBy { it.nutrition.calories.value }
    RecipeSort.TIME_ASC -> sortedBy { it.totalTime.value }
    RecipeSort.RATING_DESC -> sortedByDescending { it.rating }
    RecipeSort.NEWEST -> sortedByDescending { it.createdAt }
}
