package rs.nutriapp.core.model

import kotlinx.serialization.Serializable

@Serializable
data class DailyGoals(
    val calories: Kcal,
    val protein: Grams,
    val carbs: Grams,
    val fat: Grams,
    val fiber: Grams,
    val sugar: Grams,
    val salt: Grams,
    val water: Ml,
) {
    /** Isti podaci u obliku `Nutrition`, da se golovi i unos mogu porediti direktno. */
    val asNutrition: Nutrition
        get() = Nutrition(calories, protein, carbs, fat, fiber, sugar, salt)
}

@Serializable
data class MealGoal(
    val meal: MealSlot,
    val label: String,
    val calories: Kcal,
    val protein: Grams,
    val carbs: Grams,
    val fat: Grams,
)

@Serializable
data class WeeklyGoals(
    val calories: Kcal,
    val protein: Grams,
    val workouts: Int,
    val maxCheatMeals: Int,
)

@Serializable
data class Restriction(
    val id: RestrictionId,
    val scope: RestrictionScope,
    val label: String,
    /**
     * Sirov kljuc iz JSON-a. Nije `Nutrient` jer podaci pored sedam nutrijenata nose i
     * `"cheatMeals"` — nesto sto se ne cita iz nutritivnog unosa. Tipizovano citanje
     * ide kroz [target].
     */
    val nutrient: String,
    val operator: RestrictionOperator,
    val value: Double,
    val unit: String,
    val active: Boolean = true,
) {
    val target: RestrictionTarget get() = RestrictionTarget.fromKey(nutrient)
}

/**
 * Na sta se restrikcija odnosi.
 *
 * Sealed interface umesto golog stringa: `when` nad ovim tipom kompajler proverava na
 * iscrpnost, pa se `cheatMeals` ne moze slucajno provuci kroz granu koja ocekuje nutrijent.
 */
sealed interface RestrictionTarget {
    /** Jedan od sedam nutrijenata — merljiv iz [Nutrition]. */
    data class OfNutrient(val nutrient: Nutrient) : RestrictionTarget

    /** Broj "cheat" obroka nedeljno — broji se iz plana, ne iz nutritivnog unosa. */
    data object CheatMeals : RestrictionTarget

    /** Kljuc koji model jos ne poznaje — cuva se da se podatak ne izgubi. */
    data class Other(val key: String) : RestrictionTarget

    companion object {
        fun fromKey(key: String): RestrictionTarget = when (key) {
            "cheatMeals" -> CheatMeals
            else -> Nutrient.fromKey(key)?.let(::OfNutrient) ?: Other(key)
        }
    }
}

@Serializable
data class Goals(
    val daily: DailyGoals,
    val perMeal: List<MealGoal> = emptyList(),
    val weekly: WeeklyGoals,
    val restrictions: List<Restriction> = emptyList(),
)

// ── Provera restrikcija ──────────────────────────────────────────────────────────────

/**
 * Ishod provere jedne restrikcije.
 *
 * Sealed interface umesto bulean zastavice: pozivalac mora da obradi sva tri slucaja,
 * a svaki nosi tacno one podatke koji su mu potrebni za prikaz.
 */
sealed interface RestrictionCheck {
    val restriction: Restriction

    /** Restrikcija je iskljucena — ne racuna se. */
    data class Inactive(override val restriction: Restriction) : RestrictionCheck

    /** Cilj restrikcije se ne cita iz nutritivnog unosa (npr. broj "cheat" obroka). */
    data class NotTracked(override val restriction: Restriction) : RestrictionCheck

    data class Satisfied(
        override val restriction: Restriction,
        val actual: Double,
    ) : RestrictionCheck

    data class Violated(
        override val restriction: Restriction,
        val actual: Double,
        /** Koliko se preslo (ili nedostaje), uvek pozitivno. */
        val by: Double,
    ) : RestrictionCheck
}

fun Restriction.check(intake: Nutrition): RestrictionCheck {
    if (!active) return RestrictionCheck.Inactive(this)
    val measured = when (val t = target) {
        is RestrictionTarget.OfNutrient -> t.nutrient
        RestrictionTarget.CheatMeals, is RestrictionTarget.Other ->
            return RestrictionCheck.NotTracked(this)
    }
    val actual = intake[measured]
    val ok = when (operator) {
        RestrictionOperator.MIN -> actual >= value
        RestrictionOperator.MAX -> actual <= value
    }
    return if (ok) {
        RestrictionCheck.Satisfied(this, actual)
    } else {
        val by = when (operator) {
            RestrictionOperator.MIN -> value - actual
            RestrictionOperator.MAX -> actual - value
        }
        RestrictionCheck.Violated(this, actual, by)
    }
}

/**
 * Provera svih restrikcija odjednom, razvrstano na prekrsene i ostale.
 *
 * `partition` vraca par lista u jednom prolazu — u JS-u su to dva `filter` poziva.
 */
fun Goals.checkAll(intake: Nutrition): Pair<List<RestrictionCheck.Violated>, List<RestrictionCheck>> {
    val checks = restrictions.map { it.check(intake) }
    val violated = checks.filterIsInstance<RestrictionCheck.Violated>()
    return violated to checks
}

/** Gol za zadati obrok, ako je definisan. */
fun Goals.goalFor(slot: MealSlot): MealGoal? = perMeal.firstOrNull { it.meal == slot }
