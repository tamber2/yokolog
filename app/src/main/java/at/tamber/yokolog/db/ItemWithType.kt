package at.tamber.yokolog.db

import androidx.room.Embedded
import androidx.room.Relation

data class ItemWithType(
    @Embedded
    val item: Item,

    @Relation(
        parentColumn = "itemTypeId",
        entityColumn = "id"
    )
    val itemType: ItemType
)