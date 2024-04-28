package at.tamber.yokolog.screens.edit

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import at.tamber.yokolog.Application

object ItemEditViewModelProvider {
    fun Factory(itemId: Int): ViewModelProvider.Factory {
        return viewModelFactory {   // Initializer for ItemEditViewModel
            initializer {
                ItemEditViewModel(inventoryApplication().container.itemsRepository, itemId = itemId)
            }
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.inventoryApplication(): Application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
