package rs.nutriapp.ui.screens.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.model.ActivityLevel
import rs.nutriapp.core.model.Gender
import rs.nutriapp.core.model.PrimaryGoal

private val commonAllergies = listOf("Kikiriki", "Školjke", "Gluten", "Laktoza", "Jaja", "Soja", "Orasi")
private val commonPreferences = listOf("Bez laktoze", "Visoki proteini", "Bez svinjetine", "Vegetarijansko", "Vegansko", "Bez glutena")

class OnboardingViewModel(private val repository: NutriRepository) : ViewModel() {

    var step: OnboardingStep by mutableStateOf(OnboardingStep.Welcome)
        private set

    var displayName by mutableStateOf("")
    var age by mutableStateOf("")
    var heightCm by mutableStateOf("")
    var weightKg by mutableStateOf("")
    var gender by mutableStateOf(Gender.MUSKI)
    var activityLevel by mutableStateOf(ActivityLevel.UMERENO_AKTIVAN)
    var primaryGoal by mutableStateOf(PrimaryGoal.ODRZAVANJE)
    var allergies by mutableStateOf(setOf<String>())
    var preferences by mutableStateOf(setOf<String>())

    val allergyOptions = commonAllergies
    val preferenceOptions = commonPreferences

    /** Da li se moze preci na sledeci korak — iscrpan `when` po koraku. */
    val canAdvance: Boolean
        get() = when (step) {
            OnboardingStep.Welcome -> displayName.isNotBlank()
            OnboardingStep.BodyMetrics -> age.toIntOrNull() != null && heightCm.toDoubleOrNull() != null && weightKg.toDoubleOrNull() != null
            OnboardingStep.ActivityGoal -> true
            OnboardingStep.Allergies -> true
            OnboardingStep.Preferences -> true
        }

    fun toggleAllergy(item: String) {
        allergies = if (item in allergies) allergies - item else allergies + item
    }

    fun togglePreference(item: String) {
        preferences = if (item in preferences) preferences - item else preferences + item
    }

    fun next() {
        val i = OnboardingStep.all.indexOf(step)
        if (i < OnboardingStep.all.lastIndex) step = OnboardingStep.all[i + 1]
    }

    fun back() {
        val i = OnboardingStep.all.indexOf(step)
        if (i > 0) step = OnboardingStep.all[i - 1]
    }

    fun finish() {
        repository.updateProfile { profile ->
            profile.copy(
                displayName = displayName.trim(),
                initials = displayName.trim().split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString(""),
                age = age.toIntOrNull() ?: profile.age,
                heightCm = heightCm.toIntOrNull() ?: profile.heightCm,
                weightKg = weightKg.toDoubleOrNull() ?: profile.weightKg,
                gender = gender,
                activityLevel = activityLevel,
                primaryGoal = primaryGoal,
                allergies = allergies.toList(),
                preferences = preferences.toList(),
            )
        }
    }
}
