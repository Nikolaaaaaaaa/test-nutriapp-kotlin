package rs.nutriapp.ui.screens.logging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.model.LoggedMeal
import rs.nutriapp.core.model.MealId
import rs.nutriapp.core.model.QuickAddIssue
import rs.nutriapp.core.model.detectIssue
import rs.nutriapp.core.model.today

data class LoggingUiState(
    val loading: Boolean = true,
    val meals: List<LoggedMeal> = emptyList(),
    val issues: Map<MealId, QuickAddIssue> = emptyMap(),
)

/**
 * `detectIssue()` (sealed `QuickAddIssue`) racuna se ovde jednom za sve obroke dana —
 * ekran onda samo cita mapu, bez ponovnog racunanja po stavci u kompoziciji.
 */
class LoggingViewModel(private val repository: NutriRepository) : ViewModel() {

    val uiState: StateFlow<LoggingUiState> = repository.mealPlan
        .map { plan ->
            val meals = plan.today?.meals.orEmpty()
            LoggingUiState(
                loading = false,
                meals = meals,
                issues = meals.mapNotNull { meal -> meal.detectIssue()?.let { meal.id to it } }.toMap(),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = LoggingUiState(),
        )

    fun logMeal(id: MealId) = repository.logMeal(id)

    fun completeQuickAdd(id: MealId, servings: Int) {
        repository.updateMeal(id) { it.copy(servings = servings, complete = true) }
    }
}
