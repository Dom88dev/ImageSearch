package dom.project.imagesearch.model.remote

import retrofit2.http.GET

interface KakaoApi {

    @GET("/v2/search/image")
    fun searchImages()
}