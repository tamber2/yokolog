package at.tamber.yokolog.db

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface ItemTypesRepository {
    /**
     * Retrieve all the items from the given data source.
     */
    fun getAllItemsStream(): Flow<List<ItemType>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: Int): Flow<ItemType?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(itemType: ItemType)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(itemType: ItemType)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(itemType: ItemType)

    suspend fun insertItems(itemTypes: List<ItemType>)

    suspend fun getItemCount(): Int
}
