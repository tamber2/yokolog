package at.tamber.yokolog.screens.itemtypes.edit

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tamber.yokolog.R
import at.tamber.yokolog.StateException
import at.tamber.yokolog.db.ItemType
import at.tamber.yokolog.db.ItemTypesRepository
import at.tamber.yokolog.db.ItemsRepository
import at.tamber.yokolog.screens.add.ItemViewModel
import at.tamber.yokolog.screens.add.ItemViewModel.HomeUiState
import at.tamber.yokolog.screens.itemtypes.view.ItemTypesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemTypeEditViewModel(private val itemTypesRepository: ItemTypesRepository, private val itemsRepository: ItemsRepository, private val itemId: Int) : ViewModel() {

    private var newName: String = ""
    private var newUnitText: String = ""
    private var newColor: String = ""
    private val itemMutableLiveData: MutableLiveData<ItemType> = MutableLiveData<ItemType>()
    private var itemCountInDb = 0

    val item: MutableLiveData<ItemType>
        get() = itemMutableLiveData

    init {
        viewModelScope.launch {
            load(itemId)
        }
    }

    val itemsState: StateFlow<HomeUiState> = itemsRepository.getAllItemsStream().map {
        HomeUiState(it)
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(ItemViewModel.TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )

    private suspend fun load(itemId: Int) = withContext(Dispatchers.IO) {
        itemCountInDb = itemTypesRepository.getItemCount()

        itemTypesRepository.getItemStream(itemId).collect {
            withContext(Dispatchers.Main) {
                if (it != null) {
                    item.value = it

                    newName = item.value!!.name
                    newUnitText = item.value!!.unit
                    newColor = item.value!!.color
                }
            }
        }
    }

    fun editName(newName: String) {
        this.newName = newName
    }

    fun editUnit(newUnit: String) {
        this.newUnitText = newUnit
    }

    fun editColor(newColor: String) {
        this.newColor = newColor
    }

    @Throws(StateException::class)
    suspend fun delete(context: Context) {
        //there always have to be one item type left
        if (itemCountInDb > 1) {
            if (itemsState.value.itemList.find { it.itemType.id == item.value!!.id } == null) {
                Log.d(TAG, "safely deleting item type, it is not in use")
                itemTypesRepository.deleteItem(item.value!!)
            } else {
                Log.d(TAG, "can't delete, item type in use")
                throw StateException(context.getString(R.string.error_item_type_in_use))
            }
        } else {
            throw StateException(context.getString(R.string.error_cant_delete_type))
        }
    }

    @Throws(StateException::class)
    suspend fun save(context: Context) {
        if (itemsState.value.itemList.find { it.itemType.name == newName } == null) {
            itemTypesRepository.updateItem(ItemType(id = item.value!!.id, name = newName, unit = newUnitText, color = newColor, isNumeric = item.value!!.isNumeric))
        } else {
            Log.d(ItemTypesViewModel.TAG, "can't edit item type, one with the same name already exists")
            throw StateException(context.getString(R.string.error_saving_item_type))
        }
    }

    companion object {
        const val TAG = "ItemTypeEditViewModel"
    }
}
