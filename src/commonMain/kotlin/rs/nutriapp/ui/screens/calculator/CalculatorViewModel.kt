package rs.nutriapp.ui.screens.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.model.ActivityLevel
import rs.nutriapp.core.model.CalorieInput
import rs.nutriapp.core.model.CalorieResult
import rs.nutriapp.core.model.Gender
import rs.nutriapp.core.model.evaluateAll

data class CalculatorUiState(
    val input: CalorieInput,
    val results: List<CalorieResult>,
)

/**
 * Svaka izmena polja odmah preracunava sve tri formule — `map` nad jednim `MutableStateFlow<CalorieInput>`,
 * bez posebnog "izracunaj" dugmeta.
 */
class CalculatorViewModel(repository: NutriRepository) : ViewModel() {

    private val inputFlow = MutableStateFlow(
        repository.profile.value.let { profile ->
            CalorieInput(
                weightKg = profile.weightKg,
                heightCm = profile.heightCm.toDouble(),
                age = profile.age,
                gender = profile.gender,
                activityLevel = profile.activityLevel,
            )
        },
    )

    val uiState: StateFlow<CalculatorUiState> = inputFlow
        .map { input -> CalculatorUiState(input, input.evaluateAll()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = inputFlow.value.let { CalculatorUiState(it, it.evaluateAll()) },
        )

    fun setWeight(value: Double) = inputFlow.update { it.copy(weightKg = value) }
    fun setHeight(value: Double) = inputFlow.update { it.copy(heightCm = value) }
    fun setAge(value: Int) = inputFlow.update { it.copy(age = value) }
    fun setGender(value: Gender) = inputFlow.update { it.copy(gender = value) }
    fun setActivityLevel(value: ActivityLevel) = inputFlow.update { it.copy(activityLevel = value) }
    fun setBodyFatPct(value: Double?) = inputFlow.update { it.copy(bodyFatPct = value) }
}
