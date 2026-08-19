package rs.nutriapp.core.model

import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/**
 * Identifikatori kao value klase.
 *
 * Svi su ispod haube `String`, ali `RecipeId` se ne moze proslediti tamo gde se ceka
 * `ProductId`. Kod sa 12 razlicitih `id: string` polja u TS-u tu gresku ne hvata.
 */
@Serializable
@JvmInline
value class RecipeId(val raw: String)

@Serializable
@JvmInline
value class ProductId(val raw: String)

@Serializable
@JvmInline
value class StoreId(val raw: String)

@Serializable
@JvmInline
value class CategoryId(val raw: String)

@Serializable
@JvmInline
value class MealId(val raw: String)

@Serializable
@JvmInline
value class ChallengeId(val raw: String)

@Serializable
@JvmInline
value class SubstitutionId(val raw: String)

@Serializable
@JvmInline
value class GroceryItemId(val raw: String)

@Serializable
@JvmInline
value class SectionId(val raw: String)

@Serializable
@JvmInline
value class RestrictionId(val raw: String)

@Serializable
@JvmInline
value class NotificationId(val raw: String)

@Serializable
@JvmInline
value class UserId(val raw: String)
