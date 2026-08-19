package rs.nutriapp.ui.screens.substitutions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.model.Substitution
import rs.nutriapp.core.model.SubstitutionId
import rs.nutriapp.core.model.byCategory
import rs.nutriapp.core.model.matching

data class SubstitutionsUiState(
    val loading: Boolean = true,
    val query: String = "",
    val grouped: Map<String, List<Substitution>> = emptyMap(),
)

class SubstitutionsViewModel(private val repository: NutriRepository) : ViewModel() {

    private val queryFlow = MutableStateFlow("")

    val uiState: StateFlow<SubstitutionsUiState> = combine(
        repository.substitutions,
        queryFlow,
    ) { substitutions, query ->
        SubstitutionsUiState(
            loading = false,
            query = query,
            grouped = substitutions.matching(query).byCategory(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = SubstitutionsUiState(),
    )

    fun setQuery(text: String) {
        queryFlow.value = text
    }

    fun toggleStarred(id: SubstitutionId) = repository.toggleSubstitutionStarred(id)
}
