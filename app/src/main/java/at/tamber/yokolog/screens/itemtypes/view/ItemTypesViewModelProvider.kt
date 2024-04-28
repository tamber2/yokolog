@file:JvmName("ItemTypesViewModelKt")

package at.tamber.yokolog.screens.itemtypes.view

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import at.tamber.yokolog.inventoryApplication

object ItemTypesViewModelProvider {
    fun Factory(): ViewModelProvider.Factory {
        return viewModelFactory {   // Initializer for ItemEditViewModel
            initializer {
                ItemTypesViewModel(inventoryApplication().container.itemTypeRepository, inventoryApplication().container.itemsRepository)
            }
        }
    }
}
