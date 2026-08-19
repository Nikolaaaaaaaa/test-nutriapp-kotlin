package rs.nutriapp.core.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyStat(
    val date: String,
    val dayShort: String,
    val calories: Kcal,
    val protein: Grams,
    val carbs: Grams,
    val fat: Grams,
    val fiber: Grams,
    val sugar: Grams,
    val salt: Grams,
    val weight: Double,
    val water: Ml,
)

@Serializable
data class StatsSummary(
    val avgCalories: Kcal,
    val avgProtein: Grams,
    val avgCarbs: Grams,
    val avgFat: Grams,
    val avgFiber: Grams,
    val goalCalories: Kcal,
    val daysOnTarget: Int,
    val daysTracked: Int,
    val weightChange: Double,
    val adherencePct: Double,
    val longestStreak: Int,
)

@Serializable
data class MacroSlice(val name: String, val key: String, val value: Double, val grams: Grams, val color: String)

@Serializable
data class NutrientDeviation(
    val nutrient: String,
    val key: String,
    val goal: Double,
    val actual: Double,
    val deviationPct: Double,
    val status: DeviationStatus,
)

@Serializable
data class MealDistributionSlice(val meal: String, val calories: Kcal, val pct: Double)

@Serializable
data class TopLoggedRecipe(val recipeId: RecipeId, val name: String, val count: Int)

@Serializable
data class Stats(
    val rangeLabel: String,
    val daily: List<DailyStat> = emptyList(),
    val summary: StatsSummary,
    val macroSplit: List<MacroSlice> = emptyList(),
    val deviations: List<NutrientDeviation> = emptyList(),
    val mealDistribution: List<MealDistributionSlice> = emptyList(),
    val topLoggedRecipes: List<TopLoggedRecipe> = emptyList(),
)

// ── Ekstenzije koje grafici direktno koriste ────────────────────────────────────────

/** Klizni prosek preko `windowed` — koristi se za liniju trenda na grafiku kalorija. */
fun List<DailyStat>.calorieTrend(window: Int = 3): List<Double> =
    if (size < window) map { it.calories.value }
    else windowed(window, partialWindows = true) { it.map { d -> d.calories.value }.average() }

fun List<DailyStat>.weightRange(): ClosedFloatingPointRange<Double> {
    if (isEmpty()) return 0.0..1.0
    val values = map { it.weight }
    return (values.min())..(values.max())
}

/** Poslednjih N dana, najnoviji poslednji — grafik se crta sleva nadesno hronoloski. */
fun List<DailyStat>.lastDays(n: Int): List<DailyStat> = takeLast(n)
