package at.tamber.yokolog.screens.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tamber.yokolog.db.Item
import at.tamber.yokolog.db.ItemType
import at.tamber.yokolog.db.ItemTypesRepository
import at.tamber.yokolog.db.ItemWithType
import at.tamber.yokolog.db.ItemsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Date


class ItemViewModel(private val itemsRepository: ItemsRepository, private val itemTypesRepository: ItemTypesRepository) : ViewModel() {

    /**
     * Holds home ui state. The list of items are retrieved from [ItemsRepository] and mapped to
     * [HomeUiState]
     */
    val homeUiState: StateFlow<HomeUiState> = itemsRepository.getAllItemsStream().map {
        HomeUiState(it)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    val itemTypeState: StateFlow<TypeState> = itemTypesRepository.getAllItemsStream().map {
        TypeState(it)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = TypeState()
        )

    companion object {
        const val TIMEOUT_MILLIS = 5_000L
    }

    data class HomeUiState(val itemList: List<ItemWithType> = listOf())
    data class TypeState(val itemTypeList: List<ItemType> = listOf())

    /**
     * Inserts an [Item] in the Room database
     */
    suspend fun saveItem(description: String, itemType: ItemType) {
        if (validateInput(description.trim(), itemType)) {
            itemsRepository.insertItem(Item(description = description.trim(), timestamp = Date().time, itemTypeId = itemType.id))
        }
    }

    fun validateInput(description: String, itemType: ItemType): Boolean {
        if (itemType.isNumeric) {
            try {
                description.toFloat()
            } catch (e: NumberFormatException) {
                return false
            }
        }
        return description.isNotEmpty()
    }
}
