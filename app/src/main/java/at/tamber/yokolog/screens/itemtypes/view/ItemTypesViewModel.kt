package at.tamber.yokolog.screens.itemtypes.view

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tamber.yokolog.StateException
import at.tamber.yokolog.db.ItemType
import at.tamber.yokolog.db.ItemTypesRepository
import at.tamber.yokolog.db.ItemWithType
import at.tamber.yokolog.db.ItemsRepository
import at.tamber.yokolog.toHexCode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ItemTypesViewModel(private val itemTypesRepository: ItemTypesRepository, private val itemsRepository: ItemsRepository) : ViewModel() {

    val state: StateFlow<ItemTypesScreenState> = itemTypesRepository.getAllItemsStream().map { ItemTypesScreenState(it) }.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS), initialValue = ItemTypesScreenState(listOf())
    )

    private val itemsState: StateFlow<Items> = itemsRepository.getAllItemsStream().map { Items(it) }.stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS), initialValue = Items(listOf())
    )

    init {
        viewModelScope.launch { itemsState.collect() }
    }

    data class Items(val itemWithTypeList: List<ItemWithType>)

    companion object {
        const val TAG = "ItemTypesViewModel"
        const val TIMEOUT_MILLIS = 5_000L
    }

    data class ItemTypesScreenState(val itemList: List<ItemType> = listOf())

    @Throws(StateException::class)
    suspend fun save(descriptionText: String, unitText: String, value: Color, isNumeric: Boolean) {
        if (state.value.itemList.find { it.name == descriptionText } == null) {
            itemTypesRepository.insertItem(ItemType(name = descriptionText, unit = unitText, color = value.toHexCode(), isNumeric = isNumeric))
        } else {
            Log.d(TAG, "can't add item type, one with the same name already exists")
            throw StateException("can't add item type, one with the same name already exists")
        }
    }

}