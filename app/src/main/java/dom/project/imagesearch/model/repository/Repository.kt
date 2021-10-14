package dom.project.imagesearch.model.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import dom.project.imagesearch.model.remote.api.KakaoApi
import dom.project.imagesearch.utills.ImagePagingSource
import dom.project.imagesearch.utills.KAKAO_SEARCH_PAGE_SIZE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class Repository(
    private val apiService: KakaoApi,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
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
}