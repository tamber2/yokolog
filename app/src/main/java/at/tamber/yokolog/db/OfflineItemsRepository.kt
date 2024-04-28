package at.tamber.yokolog.db

import kotlinx.coroutines.flow.Flow

class OfflineItemsRepository(private val itemDao: ItemDao) : ItemsRepository {
    override fun getAllItemsStream(): Flow<List<ItemWithType>> = itemDao.getAllItems()

    override fun getAllItemsStream(iconTypeId: Int): Flow<List<ItemWithType>> = itemDao.getAllItemsByType(iconTypeId)

    override fun getItemStream(id: Int): Flow<ItemWithType?> = itemDao.getItem(id)

    override suspend fun insertItem(item: Item) = itemDao.insert(item)

    override suspend fun deleteItem(item: Item) = itemDao.delete(item)

    override suspend fun updateItem(item: Item) = itemDao.update(item)
}
