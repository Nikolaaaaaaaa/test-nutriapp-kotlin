package rs.nutriapp.core.model

import kotlinx.serialization.Serializable

@Serializable
data class LoggedMeal(
    val id: MealId,
    val slot: MealSlot,
    val recipeId: RecipeId? = null,
    val name: String,
    val servings: Int = 1,
    val time: String,
    val logged: Boolean = false,
    /** `false` znaci da treba prikazati "!" indikator nepotpunog unosa. */
    val complete: Boolean = true,
    val quickAdd: Boolean = false,
    val nutrition: Nutrition = Nutrition.Zero,
)

@Serializable
data class PlanDay(
    val date: String,
    val dayName: String,
    val dayShort: String,
    val isToday: Boolean = false,
    val meals: List<LoggedMeal> = emptyList(),
)

@Serializable
data class MealPlan(
    val currentDate: String,
    val weekLabel: String,
    val days: List<PlanDay> = emptyList(),
)

// ── QuickAdd nepotpunosti ────────────────────────────────────────────────────────────

/**
 * Sta tacno fali u brzo dodatom unosu.
 *
 * Sealed umesto jednog bool `complete` polja: ekran za dopunu ovako zna tacno koje
 * polje da otvori, bez ponovnog pogadjanja iz naziva/nutritivnih vrednosti.
 */
sealed interface QuickAddIssue {
    val meal: LoggedMeal

    data class MissingPortionSize(override val meal: LoggedMeal) : QuickAddIssue
    data class MissingNutrition(override val meal: LoggedMeal) : QuickAddIssue
    data class MissingRecipeLink(override val meal: LoggedMeal) : QuickAddIssue
}

fun LoggedMeal.detectIssue(): QuickAddIssue? {
    if (complete) return null
    return when {
        servings <= 0 -> QuickAddIssue.MissingPortionSize(this)
        nutrition == Nutrition.Zero -> QuickAddIssue.MissingNutrition(this)
        quickAdd && recipeId == null -> QuickAddIssue.MissingRecipeLink(this)
        else -> QuickAddIssue.MissingNutrition(this)
    }
}

// ── Ekstenzije nad danom / planom ────────────────────────────────────────────────────

val PlanDay.loggedMeals: List<LoggedMeal> get() = meals.filter { it.logged }

val PlanDay.totalNutrition: Nutrition get() = loggedMeals.sumOfNutrition { it.nutrition }

val PlanDay.incompleteCount: Int get() = meals.count { !it.complete }

/** Obroci grupisani po slotu, po hronoloskom redosledu dana (dorucak -> uzina -> rucak -> vecera). */
fun PlanDay.mealsBySlot(): Map<MealSlot, List<LoggedMeal>> =
    MealSlot.chronological.associateWith { slot -> meals.filter { it.slot == slot } }

val MealPlan.today: PlanDay? get() = days.firstOrNull { it.isToday }

/** Ukupan nedeljni unos, dan po dan. */
val MealPlan.weeklyTotals: Nutrition get() = days.sumOfNutrition { it.totalNutrition }

fun MealPlan.dayOn(date: String): PlanDay? = days.firstOrNull { it.date == date }
