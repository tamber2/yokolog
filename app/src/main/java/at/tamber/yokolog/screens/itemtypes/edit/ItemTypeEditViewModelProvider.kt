package at.tamber.yokolog.screens.itemtypes.edit

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import at.tamber.yokolog.inventoryApplication

object ItemTypeEditViewModelProvider {
    fun Factory(itemId: Int): ViewModelProvider.Factory {
        return viewModelFactory {   // Initializer for ItemEditViewModel
            initializer {
                ItemTypeEditViewModel(itemsRepository = inventoryApplication().container.itemsRepository, itemTypesRepository = inventoryApplication().container.itemTypeRepository, itemId = itemId)
            }
        }
    }
}
