package dom.project.imagesearch.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import dom.project.imagesearch.model.local.Dao
import dom.project.imagesearch.model.local.entity.SearchedKeyword
import dom.project.imagesearch.model.remote.api.KakaoApi
import dom.project.imagesearch.utills.ImagePagingSource
import dom.project.imagesearch.utills.KAKAO_SEARCH_PAGE_SIZE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(
    private val apiService: KakaoApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val dao: Dao
) {

    fun searchImages(query: String) = Pager(
        config = PagingConfig(
            pageSize = KAKAO_SEARCH_PAGE_SIZE,
            initialLoadSize = KAKAO_SEARCH_PAGE_SIZE,
            maxSize = KAKAO_SEARCH_PAGE_SIZE * 50,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { ImagePagingSource(apiService, query) }
    ).flow

    //todo 로컬에 검색기록을 저장해서 보여주는 기능을 추가하는 것도 ㄱㅊ할듯
    suspend fun getKeywordHistory() = withContext(ioDispatcher) {
        Pager(config = PagingConfig(10)) {
            dao.getHistorySearched()
        }.flow
    }

    suspend fun saveKeyword(keyword: String) = withContext(ioDispatcher) {
        dao.insert(SearchedKeyword(keyword, System.currentTimeMillis()))
    }

    suspend fun deleteKeyword(keyword: SearchedKeyword) = withContext(ioDispatcher) {
        dao.delete(keyword)
    }
}