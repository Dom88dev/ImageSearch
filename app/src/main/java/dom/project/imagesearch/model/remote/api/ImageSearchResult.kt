package dom.project.imagesearch.model.remote.api

import com.google.gson.annotations.SerializedName
import dom.project.imagesearch.model.remote.dto.Document
import dom.project.imagesearch.model.remote.dto.SearchMeta

data class ImageSearchResult(
    @SerializedName("meta")
    val meta: SearchMeta,

    @SerializedName("documents")
    val documents: List<Document>
) {
}