package rs.nutriapp.core.data

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.json.Json
import rs.nutriapp.core.model.AppNotification
import rs.nutriapp.core.model.Category
import rs.nutriapp.core.model.Challenge
import rs.nutriapp.core.model.Goals
import rs.nutriapp.core.model.GroceryList
import rs.nutriapp.core.model.MealPlan
import rs.nutriapp.core.model.Product
import rs.nutriapp.core.model.Profile
import rs.nutriapp.core.model.Recipe
import rs.nutriapp.core.model.Stats
import rs.nutriapp.core.model.Store
import rs.nutriapp.core.model.Substitution

/**
 * Svi mock podaci na jednom mestu, ucitani jednom pri startu.
 *
 * Podaci dolaze iz [EmbeddedMockJson] — bajt-za-bajt kopija `shared-mock-data/`, ista
 * konvencija kao u React i Vue verziji (nazivi polja na engleskom, sadrzaj na srpskom),
 * samo ugradjena u Kotlin izvorni kod umesto ucitana kao runtime resurs preko
 * Compose Resources (vidi napomenu u `EmbeddedMockJson.kt` zasto).
 */
data class MockData(
    val recipes: List<Recipe>,
    val products: List<Product>,
    val stores: List<Store>,
    val categories: List<Category>,
    val profile: Profile,
    val goals: Goals,
    val mealPlan: MealPlan,
    val stats: Stats,
    val challenges: List<Challenge>,
    val substitutions: List<Substitution>,
    val groceryList: GroceryList,
    val notifications: List<AppNotification>,
)

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

@OptIn(ExperimentalEncodingApi::class)
private fun decode(base64: String): String = Base64.decode(base64).decodeToString()

@Suppress("RedundantSuspendModifier") // suspend zadrzan namerno — poziva se iz istog LaunchedEffect kao ostatak starta
suspend fun loadMockData(): MockData = MockData(
    recipes = json.decodeFromString(decode(EmbeddedMockJson.recipes)),
    products = json.decodeFromString(decode(EmbeddedMockJson.products)),
    stores = json.decodeFromString(decode(EmbeddedMockJson.stores)),
    categories = json.decodeFromString(decode(EmbeddedMockJson.categories)),
    profile = json.decodeFromString(decode(EmbeddedMockJson.profile)),
    goals = json.decodeFromString(decode(EmbeddedMockJson.goals)),
    mealPlan = json.decodeFromString(decode(EmbeddedMockJson.mealPlan)),
    stats = json.decodeFromString(decode(EmbeddedMockJson.stats)),
    challenges = json.decodeFromString(decode(EmbeddedMockJson.challenges)),
    substitutions = json.decodeFromString(decode(EmbeddedMockJson.substitutions)),
    groceryList = json.decodeFromString(decode(EmbeddedMockJson.groceryList)),
    notifications = json.decodeFromString(decode(EmbeddedMockJson.notifications)),
)
