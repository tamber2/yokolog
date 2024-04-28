package at.tamber.yokolog.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Item(
    @ColumnInfo(name = "description")
    val description: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    /**
     * Date().time
     * millis since 1970.01.01
     */
    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "itemTypeId")
    val itemTypeId: Int,
)