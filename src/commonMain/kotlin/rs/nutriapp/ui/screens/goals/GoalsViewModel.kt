package rs.nutriapp.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.model.ChallengeBoard
import rs.nutriapp.core.model.ChallengeId
import rs.nutriapp.core.model.Restriction
import rs.nutriapp.core.model.RestrictionCheck
import rs.nutriapp.core.model.RestrictionId
import rs.nutriapp.core.model.checkAll
import rs.nutriapp.core.model.today
import rs.nutriapp.core.model.toBoard
import rs.nutriapp.core.model.totalNutrition

data class GoalsUiState(
    val loading: Boolean = true,
    val restrictions: List<Restriction> = emptyList(),
    val violated: List<RestrictionCheck.Violated> = emptyList(),
    val challengeBoard: ChallengeBoard = ChallengeBoard(emptyList(), emptyList(), emptyList()),
)

class GoalsViewModel(private val repository: NutriRepository) : ViewModel() {

    val uiState: StateFlow<GoalsUiState> = combine(
        repository.goals,
        repository.mealPlan,
        repository.challenges,
    ) { goals, plan, challenges ->
        val intake = plan.today?.totalNutrition ?: rs.nutriapp.core.model.Nutrition.Zero
        val (violated, _) = goals.checkAll(intake)
        GoalsUiState(
            loading = false,
            restrictions = goals.restrictions,
            violated = violated,
            challengeBoard = challenges.toBoard(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GoalsUiState(),
    )

    fun toggleRestriction(id: RestrictionId) = repository.toggleRestriction(id)

    fun respond(id: ChallengeId, accept: Boolean) = repository.respondToChallenge(id, accept)
}
