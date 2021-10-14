package dom.project.imagesearch.model.remote.dto

import com.google.gson.annotations.SerializedName

data class SearchMeta(
    @SerializedName("total_count") val totalCnt: Int,
    @SerializedName("pageable_count") val pageableCnt: Int,
    @SerializedName("is_end") val isEnd: Boolean
) {
}