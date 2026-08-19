package rs.nutriapp.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Store(
    val id: StoreId,
    val name: String,
    val shortName: String,
    /** Hex boja iz podataka — koristi se za oznaku prodavnice. */
    val color: String,
    val address: String,
    val distanceKm: Double,
    val openUntil: String,
    val favorite: Boolean = false,
    val avoided: Boolean = false,
)

@Serializable
data class Category(
    val id: CategoryId,
    val name: String,
    val icon: String,
    val color: String,
)

@Serializable
data class Product(
    val id: ProductId,
    val name: String,
    val category: String,
    val unit: String,
    val nutrition: Nutrition = Nutrition.Zero,
    /**
     * Cena po prodavnici; `null` znaci da proizvod nije dostupan u toj prodavnici.
     *
     * Kljuc ostaje obican `String` namerno: JSON mape sa value-class kljucevima rade,
     * ali granicu tipizacije je jasnije drzati na jednom mestu — citanje ide kroz
     * `priceIn(StoreId)` ispod, pa ostatak aplikacije i dalje barata tipovima.
     */
    val prices: Map<String, Double?> = emptyMap(),
    val tags: List<String> = emptyList(),
    val custom: Boolean = false,
    val inGroceryList: Boolean = false,
)

// ── Ekstenzije ───────────────────────────────────────────────────────────────────────

fun Product.priceIn(store: StoreId): Rsd? = prices[store.raw]?.let(::Rsd)

fun Product.isAvailableIn(store: StoreId): Boolean = prices[store.raw] != null

/** Sve prodavnice u kojima proizvod postoji, od najjeftinije. */
fun Product.availableStores(): List<Pair<StoreId, Rsd>> =
    prices.mapNotNull { (id, price) -> price?.let { StoreId(id) to Rsd(it) } }
        .sortedBy { it.second }

fun Product.cheapestStore(): Pair<StoreId, Rsd>? = availableStores().firstOrNull()

/** Razlika izmedju najskuplje i najjeftinije ponude — "moguca usteda" na listi. */
fun Product.priceSpread(): Rsd {
    val offers = availableStores()
    if (offers.size < 2) return Rsd.Zero
    return offers.last().second - offers.first().second
}
