package rs.nutriapp.ui.screens.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.model.MealId
import rs.nutriapp.core.model.PlanDay

enum class PlanView { WEEK, MONTH }

data class PlanUiState(
    val loading: Boolean = true,
    val weekLabel: String = "",
    val days: List<PlanDay> = emptyList(),
    val selectedDate: String? = null,
    val view: PlanView = PlanView.WEEK,
) {
    val selectedDay: PlanDay?
        get() = days.firstOrNull { it.date == selectedDate } ?: days.firstOrNull { it.isToday } ?: days.firstOrNull()
}

class PlanViewModel(private val repository: NutriRepository) : ViewModel() {

    private val selectedDateFlow = MutableStateFlow<String?>(null)
    private val viewFlow = MutableStateFlow(PlanView.WEEK)

    val uiState: StateFlow<PlanUiState> = combine(
        repository.mealPlan,
        selectedDateFlow,
        viewFlow,
    ) { plan, selectedDate, view ->
        PlanUiState(
            loading = false,
            weekLabel = plan.weekLabel,
            days = plan.days,
            selectedDate = selectedDate ?: plan.days.firstOrNull { it.isToday }?.date,
            view = view,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PlanUiState(),
    )

    fun selectDay(date: String) {
        selectedDateFlow.value = date
    }

    fun setView(view: PlanView) {
        viewFlow.value = view
    }

    fun logMeal(id: MealId) = repository.logMeal(id)
}
