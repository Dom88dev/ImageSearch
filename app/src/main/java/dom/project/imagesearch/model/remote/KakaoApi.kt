package dom.project.imagesearch.model.remote

import dom.project.imagesearch.model.remote.dto.ImageSearchResult
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoApi {

    @GET("/v2/search/image")
    fun searchImages(
        @Query("query") keyword: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 30 )
    : ImageSearchResult
}