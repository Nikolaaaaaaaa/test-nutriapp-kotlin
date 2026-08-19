package rs.nutriapp.core.model

import kotlinx.serialization.Serializable

/**
 * Nutritivne vrednosti.
 *
 * Zanimljiv deo su `operator` funkcije: zbog njih se dnevni unos pise kao
 * `meals.map { it.nutrition }.sum()`, a skaliranje porcije kao `recipe.nutrition * 1.5`.
 * Operator overloading ne postoji u TypeScript-u, pa React i Vue verzija na tim mestima
 * imaju rucno ispisano sabiranje polje po polje.
 */
@Serializable
data class Nutrition(
    val calories: Kcal = Kcal.Zero,
    val protein: Grams = Grams.Zero,
    val carbs: Grams = Grams.Zero,
    val fat: Grams = Grams.Zero,
    val fiber: Grams = Grams.Zero,
    val sugar: Grams = Grams.Zero,
    val salt: Grams = Grams.Zero,
) {
    operator fun plus(other: Nutrition) = Nutrition(
        calories = calories + other.calories,
        protein = protein + other.protein,
        carbs = carbs + other.carbs,
        fat = fat + other.fat,
        fiber = fiber + other.fiber,
        sugar = sugar + other.sugar,
        salt = salt + other.salt,
    )

    operator fun minus(other: Nutrition) = Nutrition(
        calories = calories - other.calories,
        protein = protein - other.protein,
        carbs = carbs - other.carbs,
        fat = fat - other.fat,
        fiber = fiber - other.fiber,
        sugar = sugar - other.sugar,
        salt = salt - other.salt,
    )

    /** Skaliranje na drugi broj porcija: `recipe.nutrition * 1.5`. */
    operator fun times(factor: Double) = Nutrition(
        calories = calories * factor,
        protein = protein * factor,
        carbs = carbs * factor,
        fat = fat * factor,
        fiber = fiber * factor,
        sugar = sugar * factor,
        salt = salt * factor,
    )

    /** Citanje nutrijenta preko enuma — koriste restrikcije i statistika. */
    operator fun get(nutrient: Nutrient): Double = when (nutrient) {
        Nutrient.CALORIES -> calories.value
        Nutrient.PROTEIN -> protein.value
        Nutrient.CARBS -> carbs.value
        Nutrient.FAT -> fat.value
        Nutrient.FIBER -> fiber.value
        Nutrient.SUGAR -> sugar.value
        Nutrient.SALT -> salt.value
    }

    /**
     * Raspodela kalorija po makronutrijentima (Atwater faktori: 4/4/9 kcal po gramu).
     * Vraca udele koji se sabiraju na 1.0.
     */
    val macroSplit: MacroSplit
        get() {
            val proteinKcal = protein.value * 4
            val carbsKcal = carbs.value * 4
            val fatKcal = fat.value * 9
            val total = proteinKcal + carbsKcal + fatKcal
            if (total <= 0.0) return MacroSplit(0.0, 0.0, 0.0)
            return MacroSplit(
                protein = proteinKcal / total,
                carbs = carbsKcal / total,
                fat = fatKcal / total,
            )
        }

    companion object {
        val Zero = Nutrition()
    }
}

data class MacroSplit(val protein: Double, val carbs: Double, val fat: Double)

/**
 * Nutrijenti kao enum umesto string unije.
 *
 * `key` je onaj isti string koji stoji u JSON-u (`"sugar"`, `"fiber"`...), pa se restrikcije
 * i odstupanja iz `stats.json` mapiraju bez `when` po sirovim stringovima.
 */
enum class Nutrient(val key: String, val label: String, val unit: String) {
    CALORIES("calories", "Kalorije", "kcal"),
    PROTEIN("protein", "Proteini", "g"),
    CARBS("carbs", "Ugljeni hidrati", "g"),
    FAT("fat", "Masti", "g"),
    FIBER("fiber", "Vlakna", "g"),
    SUGAR("sugar", "Šećer", "g"),
    SALT("salt", "So", "g");

    companion object {
        private val byKey = entries.associateBy { it.key }

        fun fromKey(key: String): Nutrient? = byKey[key]
    }
}

/** Zbir liste nutritivnih vrednosti — `meals.map { it.nutrition }.sum()`. */
fun Iterable<Nutrition>.sum(): Nutrition = fold(Nutrition.Zero, Nutrition::plus)

fun <T> Iterable<T>.sumOfNutrition(selector: (T) -> Nutrition): Nutrition =
    fold(Nutrition.Zero) { acc, item -> acc + selector(item) }
