package at.tamber.yokolog.screens.edit

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tamber.yokolog.db.Item
import at.tamber.yokolog.db.ItemWithType
import at.tamber.yokolog.db.ItemsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val TAG = "ItemEditViewModel"

class ItemEditViewModel(private val itemsRepository: ItemsRepository, private val itemId: Int) : ViewModel() {

    private var newDescription: String = ""
    private val itemMutableLiveData: MutableLiveData<ItemWithType> = MutableLiveData<ItemWithType>()

    val item: MutableLiveData<ItemWithType>
        get() = itemMutableLiveData

    init {
        viewModelScope.launch { load(itemId) }
    }

    private suspend fun load(itemId: Int) = withContext(Dispatchers.IO) {
        itemsRepository.getItemStream(itemId).collect {
            withContext(Dispatchers.Main) {
                item.value = it
            }
        }
    }

//TODO input validation

    suspend fun saveChanges() {
        if (item.value == null) {
            return
        }
        val updatedItem = Item(id = item.value!!.item.id, description = newDescription, timestamp = item.value!!.item.timestamp, itemTypeId = item.value!!.itemType.id)
        itemsRepository.updateItem(updatedItem)
        Log.d(TAG, "updated item:$newDescription")
    }

    fun editedDescription(newDescription: String) {
        this.newDescription = newDescription
    }

    suspend fun deleteItem() {
        if (item.value == null) {
            return
        }
        itemsRepository.deleteItem(item.value!!.item)
    }
}
