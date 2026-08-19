package rs.nutriapp.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Friend(
    val id: UserId,
    val username: String,
    val displayName: String,
    val initials: String,
    val sharedRecipes: Int = 0,
    val status: FriendStatus,
)

@Serializable
data class VisibilitySettings(
    val profileVisibility: Visibility = Visibility.PRIJATELJI,
    val sharedRecipesVisibility: Visibility = Visibility.PRIJATELJI,
    val statsVisibility: Visibility = Visibility.PRIVATNO,
    val mealPlanVisibility: Visibility = Visibility.PRIVATNO,
    val allowFriendRequests: Boolean = true,
    val showInSearch: Boolean = true,
)

@Serializable
data class Profile(
    val id: UserId,
    val username: String,
    val displayName: String,
    val email: String,
    val initials: String,
    val memberSince: String,
    val age: Int,
    val gender: Gender,
    val heightCm: Int,
    val weightKg: Double,
    val targetWeightKg: Double,
    val activityLevel: ActivityLevel,
    val primaryGoal: PrimaryGoal,
    val location: String = "",
    val locationEnabled: Boolean = false,
    val allergies: List<String> = emptyList(),
    val preferences: List<String> = emptyList(),
    val dislikedTags: List<String> = emptyList(),
    val blacklistProductIds: List<ProductId> = emptyList(),
    val favoriteStoreIds: List<StoreId> = emptyList(),
    val avoidedStoreIds: List<StoreId> = emptyList(),
    val streakDays: Int = 0,
    val friends: List<Friend> = emptyList(),
    val visibility: VisibilitySettings = VisibilitySettings(),
    val savedRecipeIds: List<RecipeId> = emptyList(),
    val savedProductIds: List<ProductId> = emptyList(),
)

// ── Ekstenzije ───────────────────────────────────────────────────────────────────────

/**
 * Prijatelji razvrstani po statusu jednim prolazom.
 *
 * `groupBy` + destrukturiranje umesto tri odvojena `filter` poziva.
 */
fun Profile.friendsByStatus(): Map<FriendStatus, List<Friend>> = friends.groupBy { it.status }

val Profile.acceptedFriends: List<Friend>
    get() = friends.filter { it.status == FriendStatus.PRIJATELJ }

val Profile.incomingRequests: List<Friend>
    get() = friends.filter { it.status == FriendStatus.ZAHTEV_PRIMLJEN }

val Profile.outgoingRequests: List<Friend>
    get() = friends.filter { it.status == FriendStatus.ZAHTEV_POSLAT }

/** Koliko kilograma deli korisnika od ciljne tezine (negativno = treba skinuti). */
val Profile.weightDelta: Double get() = targetWeightKg - weightKg

/** Indeks telesne mase. */
val Profile.bmi: Double
    get() {
        val meters = heightCm / 100.0
        return weightKg / (meters * meters)
    }

val Profile.bmiCategory: String
    get() = when {
        bmi < 18.5 -> "Pothranjenost"
        bmi < 25.0 -> "Normalna težina"
        bmi < 30.0 -> "Prekomerna težina"
        else -> "Gojaznost"
    }
