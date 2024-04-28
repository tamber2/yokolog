import android.content.Context
import at.tamber.yokolog.db.AppDatabase
import at.tamber.yokolog.db.ItemTypesRepository
import at.tamber.yokolog.db.ItemsRepository
import at.tamber.yokolog.db.OfflineItemTypesRepository
import at.tamber.yokolog.db.OfflineItemsRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val itemsRepository: ItemsRepository
    val itemTypeRepository: ItemTypesRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(AppDatabase.getDatabase(context).itemDao())
    }

    override val itemTypeRepository: ItemTypesRepository by lazy {
        OfflineItemTypesRepository(AppDatabase.getDatabase(context).itemTypeDao())
    }

}