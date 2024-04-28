package at.tamber.yokolog.db

import kotlinx.coroutines.flow.Flow

class OfflineItemTypesRepository(private val itemDao: ItemTypeDao) : ItemTypesRepository {
    override fun getAllItemsStream(): Flow<List<ItemType>> = itemDao.getAllItems()

    override fun getItemStream(id: Int): Flow<ItemType?> = itemDao.getItemType(id)

    override suspend fun insertItem(itemType: ItemType) = itemDao.insert(itemType)

    override suspend fun deleteItem(itemType: ItemType) = itemDao.delete(itemType)

    override suspend fun updateItem(itemType: ItemType) = itemDao.update(itemType)

    override suspend fun insertItems(itemTypes: List<ItemType>) = itemDao.insert(itemTypes)

    override suspend fun getItemCount(): Int = itemDao.count()
}
