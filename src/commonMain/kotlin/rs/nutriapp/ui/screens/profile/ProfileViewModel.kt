package rs.nutriapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.model.Friend
import rs.nutriapp.core.model.Profile
import rs.nutriapp.core.model.acceptedFriends
import rs.nutriapp.core.model.incomingRequests
import rs.nutriapp.ui.theme.ThemeMode

data class ProfileUiState(
    val loading: Boolean = true,
    val profile: Profile? = null,
    val friends: List<Friend> = emptyList(),
    val incomingRequests: List<Friend> = emptyList(),
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
)

class ProfileViewModel(private val repository: NutriRepository) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        repository.profile,
        repository.themeMode,
    ) { profile, themeMode ->
        ProfileUiState(
            loading = false,
            profile = profile,
            friends = profile.acceptedFriends,
            incomingRequests = profile.incomingRequests,
            themeMode = themeMode,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ProfileUiState(),
    )

    fun setThemeMode(mode: ThemeMode) = repository.setThemeMode(mode)
}
