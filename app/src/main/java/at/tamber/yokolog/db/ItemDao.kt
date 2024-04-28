package at.tamber.yokolog.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Transaction
    @Query("SELECT * from item WHERE id = :id")
    fun getItem(id: Int): Flow<ItemWithType>

    @Transaction
    @Query("SELECT * from item ORDER BY timestamp DESC")
    fun getAllItems(): Flow<List<ItemWithType>>

    @Transaction
    @Query("SELECT * from item WHERE itemTypeId = :itemTypeId ORDER BY timestamp ASC")
    fun getAllItemsByType(itemTypeId: Int): Flow<List<ItemWithType>>

    @Transaction
    @Query("SELECT * from item")
    fun getItemsWithType(): Flow<List<ItemWithType>>


}
