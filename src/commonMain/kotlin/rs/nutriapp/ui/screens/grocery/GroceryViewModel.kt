package rs.nutriapp.ui.screens.grocery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import rs.nutriapp.core.data.NutriRepository
import rs.nutriapp.core.model.GroceryItemId
import rs.nutriapp.core.model.GroceryList
import rs.nutriapp.core.model.Rsd
import rs.nutriapp.core.model.SectionId
import rs.nutriapp.core.model.StoreId
import rs.nutriapp.core.model.StoreTotal
import rs.nutriapp.core.model.recomputedByStore
import rs.nutriapp.core.model.recomputedTotal

data class GroceryUiState(
    val loading: Boolean = true,
    val list: GroceryList? = null,
    val liveTotal: Rsd = Rsd.Zero,
    val liveByStore: List<StoreTotal> = emptyList(),
)

class GroceryViewModel(private val repository: NutriRepository) : ViewModel() {

    val uiState: StateFlow<GroceryUiState> = repository.groceryList
        .map { list ->
            GroceryUiState(
                loading = false,
                list = list,
                liveTotal = list.recomputedTotal(),
                liveByStore = list.recomputedByStore(repository::storeName),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = GroceryUiState(),
        )

    val stores = repository.stores

    fun setChecked(itemId: GroceryItemId, checked: Boolean) = repository.setItemChecked(itemId, checked)

    fun removeItem(itemId: GroceryItemId) = repository.removeItem(itemId)

    fun clearRareSection(sectionId: SectionId) = repository.clearRareSection(sectionId)

    fun setStore(itemId: GroceryItemId, storeId: StoreId) = repository.setItemStore(itemId, storeId)
}
