package dom.project.imagesearch.model.local

import androidx.paging.PagingSource
import androidx.room.*
import androidx.room.Dao
import dom.project.imagesearch.model.local.entity.SearchedKeyword

@Dao
interface Dao {

    @Query("select count(keyword) from history_searched")
    fun getCont(): Int

    @Query("select * from history_searched order by date desc")
    fun getHistorySearched(): PagingSource<Int, SearchedKeyword>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg data: SearchedKeyword)

    @Delete
    suspend fun delete(data: SearchedKeyword)

    @Query("delete from history_searched")
    suspend fun deleteAll()
}