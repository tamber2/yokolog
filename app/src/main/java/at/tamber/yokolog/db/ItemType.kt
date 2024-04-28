package at.tamber.yokolog.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name"], unique = true)])
data class ItemType(
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "isNumeric")
    val isNumeric: Boolean,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "unit")
    val unit: String,

    @ColumnInfo(name = "color")
    val color: String,
)