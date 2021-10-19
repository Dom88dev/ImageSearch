package dom.project.imagesearch.model.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_searched")
data class SearchedKeyword(
    @PrimaryKey
    val keyword: String,
    @ColumnInfo(name = "date")
    val dateTime: Long
) {
}