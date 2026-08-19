package rs.nutriapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.model.Challenge
import rs.nutriapp.core.model.DailyGoals
import rs.nutriapp.core.model.LoggedMeal
import rs.nutriapp.core.model.MealId
import rs.nutriapp.core.model.Nutrition
import rs.nutriapp.core.model.Profile
import rs.nutriapp.core.model.today
import rs.nutriapp.core.model.totalNutrition

data class HomeUiState(
    val loading: Boolean = true,
    val greetingName: String = "",
    val streakDays: Int = 0,
    val dailyGoals: DailyGoals? = null,
    val intake: Nutrition = Nutrition.Zero,
    val meals: List<LoggedMeal> = emptyList(),
    val nextMeal: LoggedMeal? = null,
    val unreadNotifications: Int = 0,
    val activeChallenge: Challenge? = null,
) {
    val calorieFraction: Float
        get() {
            val goal = dailyGoals?.calories?.value ?: return 0f
            if (goal <= 0.0) return 0f
            return (intake.calories.value / goal).toFloat()
        }

    val proteinFraction: Float
        get() {
            val goal = dailyGoals?.protein?.value ?: return 0f
            if (goal <= 0.0) return 0f
            return (intake.protein.value / goal).toFloat()
        }

    val carbsFraction: Float
        get() {
            val goal = dailyGoals?.carbs?.value ?: return 0f
            if (goal <= 0.0) return 0f
            return (intake.carbs.value / goal).toFloat()
        }
}

/**
 * `combine()` spaja cetiri nezavisna flow-a (plan, golovi, profil, notifikacije) u jedan
 * `HomeUiState` — svaka promena bilo kog izvora automatski preracunava ceo ekran.
 * Ekran cita samo JEDAN `StateFlow`, bez rucnog uskladjivanja vise stanja.
 */
class HomeViewModel(private val repository: NutriRepository) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        repository.mealPlan,
        repository.goals,
        repository.profile,
        repository.notifications,
        repository.challenges,
    ) { plan, goals, profile, notifications, challenges ->
        val today = plan.today
        val meals = today?.meals.orEmpty()
        val intake = today?.totalNutrition ?: Nutrition.Zero
        HomeUiState(
            loading = false,
            greetingName = profile.displayName.substringBefore(" "),
            streakDays = profile.streakDays,
            dailyGoals = goals.daily,
            intake = intake,
            meals = meals,
            nextMeal = meals.firstOrNull { !it.logged },
            unreadNotifications = notifications.count { !it.read },
            activeChallenge = challenges.firstOrNull { it.status == rs.nutriapp.core.model.ChallengeStatus.AKTIVAN },
        )
    }.stateIn(
        scope = viewModelScope,
        // Namerno `Eagerly`, ne `WhileSubscribed(5_000)`: ovaj drugi svoje pokretanje i
        // zaustavljanje meri tajmerom, a vremenski zasnovane korutine na Kotlin/Wasm cilju
        // nisu okidale pouzdano — ekran bi povremeno zauvek ostao na "Učitavanje…".
        // Racunica je ionako trivijalna i podaci su u memoriji, pa nema sta da se stedi.
        started = SharingStarted.Eagerly,
        initialValue = HomeUiState(),
    )

    fun logMeal(id: MealId) = repository.logMeal(id)

    fun toggleRecipeStarred(recipeId: rs.nutriapp.core.model.RecipeId) = repository.toggleRecipeStarred(recipeId)
}
