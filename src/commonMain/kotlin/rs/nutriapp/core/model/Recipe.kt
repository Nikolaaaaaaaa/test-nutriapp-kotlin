package rs.nutriapp.core.model

import kotlinx.serialization.Serializable

@Serializable
data class RecipeIngredient(
    val productId: ProductId? = null,
    val name: String,
    val amount: Double,
    val unit: String,
) {
    /** "200 g", "1.5 kg", "8 kom" — bez suvisne decimale kad je broj ceo. */
    fun formatAmount(): String {
        val whole = amount.toInt()
        val text = if (amount == whole.toDouble()) "$whole" else "$amount"
        return "$text $unit"
    }
}

@Serializable
data class Recipe(
    val id: RecipeId,
    val name: String,
    val description: String,
    /** Kljuc za lokalno generisan placeholder, ne URL. */
    val image: String,
    val prepTime: Minutes,
    val cookTime: Minutes,
    val difficulty: Difficulty,
    val servings: Int,
    val mealType: MealSlot,
    val tags: List<String> = emptyList(),
    val ingredients: List<RecipeIngredient> = emptyList(),
    /** Nutritivne vrednosti **po porciji**. */
    val nutrition: Nutrition = Nutrition.Zero,
    val steps: List<String> = emptyList(),
    val saveCount: Int = 0,
    val logCount: Int = 0,
    val rating: Double = 0.0,
    val author: String = "",
    val starred: Boolean = false,
    val createdAt: String = "",
)

// ── Ekstenzije ───────────────────────────────────────────────────────────────────────
// Ponasanje stoji uz model, ali van `data class` tela — pa `Recipe` ostaje cist podatak,
// a logika se dodaje bez nasledjivanja i bez wrapper klase. U TS-u bi ovo bile slobodne
// funkcije koje se pozivaju kao `totalTime(recipe)`; u Kotlinu se citaju kao svojstvo tipa.

val Recipe.totalTime: Minutes get() = prepTime + cookTime

/** Nutritivne vrednosti celog recepta, za zadati broj porcija. */
fun Recipe.totalNutrition(servings: Int = this.servings): Nutrition =
    nutrition * servings.toDouble()

/**
 * Skaliranje recepta na drugi broj porcija.
 *
 * Kolicine sastojaka se mnoze, a nutritivna vrednost **po porciji** ostaje ista —
 * menja se samo ukupna. `copy()` pravi novi objekat, original se ne dira.
 */
fun Recipe.scaledTo(newServings: Int): Recipe {
    require(newServings > 0) { "Broj porcija mora biti pozitivan, dobijeno: $newServings" }
    if (newServings == servings) return this
    val factor = newServings.toDouble() / servings
    return copy(
        servings = newServings,
        ingredients = ingredients.map { it.copy(amount = it.amount * factor) },
    )
}

/** Zamena jednog sastojka drugim — koristi katalog supstitucija na detalju recepta. */
fun Recipe.withIngredientSwapped(original: String, replacement: RecipeIngredient): Recipe =
    copy(ingredients = ingredients.map { if (it.name == original) replacement else it })

/** Da li recept krsi neku od korisnikovih alergija ili nepozeljnih tagova. */
fun Recipe.conflictsWith(profile: Profile): List<String> = buildList {
    val lowerTags = tags.map { it.lowercase() }
    profile.dislikedTags.forEach { disliked ->
        if (disliked.lowercase() in lowerTags) add("ne voliš: $disliked")
    }
    profile.allergies.forEach { allergen ->
        val hit = ingredients.any { it.name.contains(allergen, ignoreCase = true) }
        if (hit) add("alergija: $allergen")
    }
    profile.blacklistProductIds.forEach { blocked ->
        val hit = ingredients.any { it.productId == blocked }
        if (hit) add("na crnoj listi")
    }
}
