package rs.nutriapp.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Substitution(
    val id: SubstitutionId,
    val fromProductId: ProductId? = null,
    val fromName: String,
    val toProductId: ProductId? = null,
    val toName: String,
    val ratio: String,
    val reason: String,
    val category: String,
    val starred: Boolean = false,
    val usedCount: Int = 0,
    val nutritionDelta: Nutrition = Nutrition.Zero,
    val custom: Boolean = false,
)

/** Da li je zamena povoljnija nutritivno — koristi se za bedž "manje kalorija" u katalogu. */
val Substitution.isCalorieReduction: Boolean get() = nutritionDelta.calories.value < 0

// `toSortedMap()` je JVM-only (backed by java.util.TreeMap) — na common/wasm cilju se
// sortiranje po kljucu radi eksplicitno, `LinkedHashMap` cuva taj redosled pri iteraciji.
fun List<Substitution>.byCategory(): Map<String, List<Substitution>> =
    groupBy { it.category }
        .toList()
        .sortedBy { (category, _) -> category }
        .toMap()

fun List<Substitution>.matching(query: String): List<Substitution> {
    if (query.isBlank()) return this
    val q = query.trim().lowercase()
    return filter { it.fromName.lowercase().contains(q) || it.toName.lowercase().contains(q) }
}
