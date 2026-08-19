package rs.nutriapp.core.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import rs.nutriapp.core.model.AppNotification
import rs.nutriapp.core.model.Category
import rs.nutriapp.core.model.Challenge
import rs.nutriapp.core.model.ChallengeId
import rs.nutriapp.core.model.ChallengeStatus
import rs.nutriapp.core.model.Goals
import rs.nutriapp.core.model.GroceryItemId
import rs.nutriapp.core.model.GroceryList
import rs.nutriapp.core.model.LoggedMeal
import rs.nutriapp.core.model.MealId
import rs.nutriapp.core.model.MealPlan
import rs.nutriapp.core.model.NotificationId
import rs.nutriapp.core.model.Product
import rs.nutriapp.core.model.ProductId
import rs.nutriapp.core.model.Profile
import rs.nutriapp.core.model.Recipe
import rs.nutriapp.core.model.RecipeId
import rs.nutriapp.core.model.RestrictionId
import rs.nutriapp.core.model.Rsd
import rs.nutriapp.core.model.SectionId
import rs.nutriapp.core.model.Stats
import rs.nutriapp.core.model.Store
import rs.nutriapp.core.model.StoreId
import rs.nutriapp.core.model.StoreTotal
import rs.nutriapp.core.model.Substitution
import rs.nutriapp.core.model.SubstitutionId
import rs.nutriapp.core.model.recomputedByStore
import rs.nutriapp.core.model.recomputedTotal
import rs.nutriapp.core.model.withItemChecked
import rs.nutriapp.core.model.withItemRemoved
import rs.nutriapp.core.model.withItemStore
import rs.nutriapp.core.model.withRareSectionCleared
import rs.nutriapp.ui.theme.ThemeMode

/**
 * Jedini izvor istine u aplikaciji.
 *
 * Svaki komad podataka je privatni `MutableStateFlow`, izlozen napolje kao read-only
 * `StateFlow`. Svaka mutacija ide kroz `update { }` — atomsko citaj-izmeni-upisi bez
 * rucnog "diff" koraka.
 *
 * Ovo je razlog zasto zvezdica na receptu odmah promeni Biblioteku, i zasto stikliranje
 * stavke na Grocery listi odmah promeni brojac na Dashboard-u: oba ekrana citaju
 * ISTI flow preko `combine()` u svom ViewModel-u, nema "javi drugoj komponenti" koraka
 * kakav bi bio potreban u React-u bez konteksta ili u Vue-u bez Pinia store-a.
 *
 * Bez trajnog cuvanja — sve zivi u memoriji i nestaje na refresh, kao i React/Vue verzija.
 */
class NutriRepository(data: MockData) {

    private val recipesFlow = MutableStateFlow(data.recipes)
    val recipes: StateFlow<List<Recipe>> = recipesFlow.asStateFlow()

    private val productsFlow = MutableStateFlow(data.products)
    val products: StateFlow<List<Product>> = productsFlow.asStateFlow()

    val stores: List<Store> = data.stores
    val categories: List<Category> = data.categories

    private val profileFlow = MutableStateFlow(data.profile)
    val profile: StateFlow<Profile> = profileFlow.asStateFlow()

    private val goalsFlow = MutableStateFlow(data.goals)
    val goals: StateFlow<Goals> = goalsFlow.asStateFlow()

    private val mealPlanFlow = MutableStateFlow(data.mealPlan)
    val mealPlan: StateFlow<MealPlan> = mealPlanFlow.asStateFlow()

    /** Statistika je istorijski snimak (30 dana unazad) — jedini deo koji se ne mutira. */
    val stats: Stats = data.stats

    private val challengesFlow = MutableStateFlow(data.challenges)
    val challenges: StateFlow<List<Challenge>> = challengesFlow.asStateFlow()

    private val substitutionsFlow = MutableStateFlow(data.substitutions)
    val substitutions: StateFlow<List<Substitution>> = substitutionsFlow.asStateFlow()

    private val groceryListFlow = MutableStateFlow(data.groceryList)
    val groceryList: StateFlow<GroceryList> = groceryListFlow.asStateFlow()

    private val notificationsFlow = MutableStateFlow(data.notifications)
    val notifications: StateFlow<List<AppNotification>> = notificationsFlow.asStateFlow()

    private val themeModeFlow = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = themeModeFlow.asStateFlow()

    fun storeName(id: StoreId): String = stores.firstOrNull { it.id == id }?.name ?: id.raw

    // ── Recepti ──────────────────────────────────────────────────────────────────────

    fun toggleRecipeStarred(id: RecipeId) {
        recipesFlow.update { list -> list.map { if (it.id == id) it.copy(starred = !it.starred) else it } }
    }

    fun upsertRecipe(recipe: Recipe) {
        recipesFlow.update { list ->
            if (list.any { it.id == recipe.id }) list.map { if (it.id == recipe.id) recipe else it }
            else list + recipe
        }
    }

    // ── Golovi i restrikcije ────────────────────────────────────────────────────────

    fun toggleRestriction(id: RestrictionId) {
        goalsFlow.update { g ->
            g.copy(restrictions = g.restrictions.map { if (it.id == id) it.copy(active = !it.active) else it })
        }
    }

    // ── Izazovi ──────────────────────────────────────────────────────────────────────

    fun respondToChallenge(id: ChallengeId, accept: Boolean) {
        challengesFlow.update { list ->
            list.map {
                if (it.id == id) it.copy(status = if (accept) ChallengeStatus.AKTIVAN else ChallengeStatus.ODBIJEN)
                else it
            }
        }
    }

    // ── Supstitucije ─────────────────────────────────────────────────────────────────

    fun toggleSubstitutionStarred(id: SubstitutionId) {
        substitutionsFlow.update { list -> list.map { if (it.id == id) it.copy(starred = !it.starred) else it } }
    }

    fun addCustomSubstitution(substitution: Substitution) {
        substitutionsFlow.update { it + substitution }
    }

    // ── Logovanje obroka / plan ──────────────────────────────────────────────────────

    fun updateMeal(mealId: MealId, transform: (LoggedMeal) -> LoggedMeal) {
        mealPlanFlow.update { plan ->
            plan.copy(
                days = plan.days.map { day ->
                    day.copy(meals = day.meals.map { if (it.id == mealId) transform(it) else it })
                },
            )
        }
    }

    fun logMeal(mealId: MealId) = updateMeal(mealId) { it.copy(logged = true) }

    // ── Lista za kupovinu ────────────────────────────────────────────────────────────

    fun setItemChecked(itemId: GroceryItemId, checked: Boolean) {
        groceryListFlow.update { it.withItemChecked(itemId, checked) }
    }

    fun removeItem(itemId: GroceryItemId) {
        groceryListFlow.update { it.withItemRemoved(itemId) }
    }

    fun clearRareSection(sectionId: SectionId) {
        groceryListFlow.update { it.withRareSectionCleared(sectionId) }
    }

    fun setItemStore(itemId: GroceryItemId, storeId: StoreId) {
        groceryListFlow.update { list ->
            val item = list.sections.flatMap { it.items }.firstOrNull { it.id == itemId } ?: return@update list
            val product = productsFlow.value.firstOrNull { it.id == item.productId } ?: return@update list
            val newPrice = product.prices[storeId.raw] ?: return@update list
            list.withItemStore(itemId, storeId, Rsd(newPrice))
        }
    }

    /** Ukupna cena i raspodela po prodavnici, uvek izracunata iz trenutnih stavki. */
    fun recomputeGroceryTotals(): Pair<Rsd, List<StoreTotal>> {
        val list = groceryListFlow.value
        return list.recomputedTotal() to list.recomputedByStore(::storeName)
    }

    // ── Notifikacije ─────────────────────────────────────────────────────────────────

    fun markNotificationRead(id: NotificationId) {
        notificationsFlow.update { list -> list.map { if (it.id == id) it.copy(read = true) else it } }
    }

    fun markAllNotificationsRead() {
        notificationsFlow.update { list -> list.map { it.copy(read = true) } }
    }

    // ── Profil ───────────────────────────────────────────────────────────────────────

    fun updateProfile(transform: (Profile) -> Profile) {
        profileFlow.update(transform)
    }

    fun toggleSavedRecipe(id: RecipeId) {
        profileFlow.update { p ->
            if (id in p.savedRecipeIds) p.copy(savedRecipeIds = p.savedRecipeIds - id)
            else p.copy(savedRecipeIds = p.savedRecipeIds + id)
        }
    }

    fun toggleSavedProduct(id: ProductId) {
        profileFlow.update { p ->
            if (id in p.savedProductIds) p.copy(savedProductIds = p.savedProductIds - id)
            else p.copy(savedProductIds = p.savedProductIds + id)
        }
    }

    fun addCustomProduct(product: Product) {
        productsFlow.update { it + product }
    }

    // ── Tema ─────────────────────────────────────────────────────────────────────────

    fun setThemeMode(mode: ThemeMode) {
        themeModeFlow.value = mode
    }
}
