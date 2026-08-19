package rs.nutriapp.core.model

import kotlinx.serialization.Serializable

@Serializable
data class GroceryItem(
    val id: GroceryItemId,
    val productId: ProductId,
    val name: String,
    val amount: Double,
    val unit: String,
    val storeId: StoreId,
    val price: Rsd,
    val checked: Boolean = false,
    val fromRecipeIds: List<RecipeId> = emptyList(),
)

@Serializable
data class GrocerySection(
    val id: SectionId,
    val name: String,
    /** Sekcija "retko kupovano" (so, začini) — ima bulk-remove. */
    val rare: Boolean = false,
    val note: String? = null,
    val items: List<GroceryItem> = emptyList(),
)

@Serializable
data class StoreTotal(
    val storeId: StoreId,
    val storeName: String,
    val itemCount: Int,
    val total: Rsd,
)

@Serializable
data class GroceryTotals(
    val itemCount: Int,
    val checkedCount: Int,
    val estimatedTotal: Rsd,
    val currency: String,
    val byStore: List<StoreTotal> = emptyList(),
    val potentialSavings: Rsd,
)

@Serializable
data class GroceryList(
    val id: String,
    val name: String,
    val generatedFrom: String,
    val generatedAt: String,
    val sections: List<GrocerySection> = emptyList(),
    val totals: GroceryTotals,
)

// ── Ekstenzije ───────────────────────────────────────────────────────────────────────

val GrocerySection.checkedCount: Int get() = items.count { it.checked }

val GrocerySection.sectionTotal: Rsd get() = items.sumOfRsd { it.price }

/**
 * Zbir sveze izracunat iz stavki, umesto uskladistene vrednosti u `totals` —
 * koristi se posle izmene prodavnice po artiklu, da broj odmah odrazava izbor.
 */
fun GroceryList.recomputedTotal(): Rsd = sections.sumOfRsd { it.sectionTotal }

/** `storeNameOf` dolazi iz kataloga prodavnica (`NutriRepository`), ne iz liste za kupovinu. */
fun GroceryList.recomputedByStore(storeNameOf: (StoreId) -> String): List<StoreTotal> =
    sections.flatMap { it.items }
        .groupBy { it.storeId }
        .map { (storeId, items) ->
            StoreTotal(
                storeId = storeId,
                storeName = storeNameOf(storeId),
                itemCount = items.size,
                total = items.sumOfRsd { it.price },
            )
        }
        .sortedByDescending { it.total }

fun GroceryList.withItemChecked(itemId: GroceryItemId, checked: Boolean): GroceryList = copy(
    sections = sections.map { section ->
        section.copy(items = section.items.map { if (it.id == itemId) it.copy(checked = checked) else it })
    },
)

fun GroceryList.withItemRemoved(itemId: GroceryItemId): GroceryList = copy(
    sections = sections.map { section -> section.copy(items = section.items.filterNot { it.id == itemId }) },
)

/** Bulk-remove cele "retko kupovano" sekcije. */
fun GroceryList.withRareSectionCleared(sectionId: SectionId): GroceryList = copy(
    sections = sections.map { section -> if (section.id == sectionId) section.copy(items = emptyList()) else section },
)

fun GroceryList.withItemStore(itemId: GroceryItemId, storeId: StoreId, newPrice: Rsd): GroceryList = copy(
    sections = sections.map { section ->
        section.copy(
            items = section.items.map {
                if (it.id == itemId) it.copy(storeId = storeId, price = newPrice) else it
            },
        )
    },
)
