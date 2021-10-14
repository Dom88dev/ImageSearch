package dom.project.imagesearch.utills

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dom.project.imagesearch.model.remote.api.KakaoApi
import dom.project.imagesearch.model.remote.dto.Document
import retrofit2.HttpException
import java.io.IOException

class ImagePagingSource(
    private val service: KakaoApi,
    private val query: String
) : PagingSource<Int, Document>() {
    override fun getRefreshKey(state: PagingState<Int, Document>): Int? {
        // anchorPosition은 로드된 PageData에서 가장 최근에 접근했던 아이템의 포지션이다.
        // anchorPosition이 null이라는건 PageData가 만들어지지 않았단 것이기에 초기 로딩이란 뜻
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Document> {
        // 초기 로드 시 params.key = null, 따라서 null 일 시 페이지 첫 인덱스를 삽입해준다.
        val position = params.key ?: KAKAO_SEARCH_STARTING_PAGE_INDEX
        return try {
            // params.loadSize는 PagingConfig에서 설정한 페이지 사이즈와 일반적으로 같으나
            // 초기 로드시 config에 초기 로딩 사이즈도 따로 지정하지 않는다면 페이지 사이즈의 3배가 로드된다.
            val response = service.searchImages(query, position, params.loadSize)
            val images = response.documents
            val nextKey =
                if (response.meta.isEnd) null else position + (params.loadSize / KAKAO_SEARCH_PAGE_SIZE)
            LoadResult.Page(
                data = images,
                prevKey = if (position == KAKAO_SEARCH_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}