package at.tamber.yokolog

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import at.tamber.yokolog.screens.add.ItemViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for ItemEditViewModel
        initializer {
            ItemViewModel(
                // this.createSavedStateHandle(),
                inventoryApplication().container.itemsRepository,
                inventoryApplication().container.itemTypeRepository,
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.inventoryApplication(): Application = (this[AndroidViewModelFactory.APPLICATION_KEY] as Application)
