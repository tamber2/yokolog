package at.tamber.yokolog.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemTypeDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(itemType: ItemType)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(itemType: List<ItemType>)

    @Update
    suspend fun update(itemType: ItemType)

    @Delete
    suspend fun delete(itemType: ItemType)

    @Query("SELECT * from itemtype WHERE id = :id")
    fun getItemType(id: Int): Flow<ItemType?>

    @Query("SELECT * from itemtype ORDER BY name ASC")
    fun getAllItems(): Flow<List<ItemType>>

    @Query("SELECT COUNT(*) FROM itemtype")
    fun count(): Int
}
