package dom.project.imagesearch.model.remote.dto

import com.google.gson.annotations.SerializedName

data class ImageSearchResult(
    @SerializedName("meta")
    val meta: SearchMeta,

    @SerializedName("documents")
    val documents: List<Document>
) {
}